package com.stakater.nordmart.gateway;


import com.stakater.nordmart.gateway.client.ClientFactory;
import com.stakater.nordmart.gateway.config.Config;
import com.stakater.nordmart.gateway.handler.CartHandler;
import com.stakater.nordmart.gateway.handler.ProductHandler;
import com.stakater.nordmart.gateway.handler.ReviewHandler;
import com.stakater.nordmart.gateway.router.NordmartRouter;
import com.stakater.nordmart.gateway.tracing.TracingConfiguration;
import com.uber.jaeger.Tracer;
import com.uber.jaeger.metrics.Metrics;
import com.uber.jaeger.metrics.NullStatsReporter;
import com.uber.jaeger.metrics.StatsFactoryImpl;
import com.uber.jaeger.propagation.b3.B3TextMapCodec;
import com.uber.jaeger.reporters.RemoteReporter;
import com.uber.jaeger.samplers.ConstSampler;
import com.uber.jaeger.senders.HttpSender;
import io.opentracing.propagation.Format.Builtin;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.rxjava.circuitbreaker.CircuitBreaker;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.servicediscovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import rx.Single;

@SpringBootApplication
public class GatewayVerticle extends AbstractVerticle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayVerticle.class);

    private final ProductHandler productHandler = new ProductHandler();
    private final CartHandler cartHandler = new CartHandler();
    private final ReviewHandler reviewHandler = new ReviewHandler();

    @Autowired
    private Environment env;

    @Bean
    public io.opentracing.Tracer jaegerTracer()
    {
        String jaegerServiceName = env.getProperty("opentracing.servicename");
        String jaegerEndpoint = env.getProperty("jaeger.collector.endpoint");

        LOGGER.info("Service name for jaeger tracing {}, jaeger endpoint {}", jaegerServiceName, jaegerEndpoint);

        Tracer.Builder builder = new Tracer.Builder(jaegerServiceName,
                new RemoteReporter(new HttpSender(jaegerEndpoint), 10,
                        65000, new Metrics(new StatsFactoryImpl(new NullStatsReporter()))),
                new ConstSampler(true))
                .registerInjector(Builtin.HTTP_HEADERS, new B3TextMapCodec())
                .registerExtractor(Builtin.HTTP_HEADERS, new B3TextMapCodec());
        return builder.build();
    }

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

        productHandler.setCircuit(circuit);
        cartHandler.setCircuit(circuit);
        reviewHandler.setCircuit(circuit);

        Router router = new NordmartRouter(vertx, productHandler, cartHandler, reviewHandler).getRouter();

        ServiceDiscovery.create(vertx, discovery -> {
            ClientFactory clientFactory = new ClientFactory(discovery, vertx, config);
            // Zip all 4 requests
            Single.zip(clientFactory.getCatalogClient(), clientFactory.getInventoryClient(),
                clientFactory.getCartClient(), clientFactory.getReviewClient(), (c, i, ct, r) -> {
                // When everything is done
                productHandler.setClient(c);
                productHandler.inventoryHandler.setClient(i);
                cartHandler.setClient(ct);
                reviewHandler.setClient(r);
                return vertx.createHttpServer()
                    .requestHandler(router::accept)
                    .listen(config.getServerPort());
            }).subscribe();
        });
    }

}
