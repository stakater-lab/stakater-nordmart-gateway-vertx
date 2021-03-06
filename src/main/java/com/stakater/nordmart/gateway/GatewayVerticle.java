package com.stakater.nordmart.gateway;


import com.stakater.nordmart.gateway.client.ClientFactory;
import com.stakater.nordmart.gateway.config.Config;
import com.stakater.nordmart.gateway.handler.CartHandler;
import com.stakater.nordmart.gateway.handler.CustomerHandler;
import com.stakater.nordmart.gateway.handler.ProductHandler;
import com.stakater.nordmart.gateway.handler.ProductSearchHandler;
import com.stakater.nordmart.gateway.handler.ReviewHandler;
import com.stakater.nordmart.gateway.handler.PromotionHandler;
import com.stakater.nordmart.gateway.router.NordmartRouter;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.rxjava.circuitbreaker.CircuitBreaker;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.servicediscovery.ServiceDiscovery;
import rx.Single;

public class GatewayVerticle extends AbstractVerticle
{
    private final CustomerHandler customerHandler = new CustomerHandler();
    private final ProductHandler productHandler = new ProductHandler();
    private final CartHandler cartHandler = new CartHandler();
    private final ReviewHandler reviewHandler = new ReviewHandler();
    private final ProductSearchHandler productSearchHandler = new ProductSearchHandler();
    private final PromotionHandler promotionHandler = new PromotionHandler();

    @Override
    public void start()
    {
        Config config = Config.getConfig();
        CircuitBreaker circuit = CircuitBreaker.create("inventory-circuit-breaker", vertx,
            new CircuitBreakerOptions()
                .setFallbackOnFailure(true)
                .setMaxFailures(3)
                .setResetTimeout(5000)
                .setTimeout(1000)
        );

        customerHandler.setCircuit(circuit);
        productHandler.setCircuit(circuit);
        cartHandler.setCircuit(circuit);
        reviewHandler.setCircuit(circuit);
        productSearchHandler.setCircuit(circuit);
        promotionHandler.setCircuit(circuit);

        Router router = new NordmartRouter(vertx, customerHandler, productHandler, cartHandler, reviewHandler, productSearchHandler, promotionHandler)
                .getRouter();

        ServiceDiscovery.create(vertx, discovery -> {
            ClientFactory clientFactory = new ClientFactory(discovery, vertx, config);
            // Zip all 4 requests
            Single.zip(clientFactory.getCustomerClient(), clientFactory.getCatalogClient(), clientFactory.getInventoryClient(),
                clientFactory.getCartClient(), clientFactory.getReviewClient(), clientFactory.getProductSearch(), clientFactory.getPromotionClient(), (z, c, i, ct, r, ps, pr) -> {
                // When everything is done
                customerHandler.setClient(z);
                productHandler.setClient(c);
                productHandler.inventoryHandler.setClient(i);
                cartHandler.setClient(ct);
                reviewHandler.setClient(r);
                productSearchHandler.setClient(ps);
                promotionHandler.setClient(pr);
                return vertx.createHttpServer()
                    .requestHandler(router::accept)
                    .listen(config.getServerPort());
            }).subscribe();
        });
    }

}
