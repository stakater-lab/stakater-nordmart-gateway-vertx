package com.stakater.nordmart.gateway.router;

import com.stakater.nordmart.gateway.handler.CartHandler;
import com.stakater.nordmart.gateway.handler.CustomerHandler;
import com.stakater.nordmart.gateway.handler.ProductHandler;
import com.stakater.nordmart.gateway.handler.ProductSearchHandler;
import com.stakater.nordmart.gateway.handler.ReviewHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;

public class NordmartRouter
{
    private Router router;

    public NordmartRouter(Vertx vertex, CustomerHandler customerHandler, ProductHandler productHandler,
                          CartHandler cartHandler, ReviewHandler reviewHandler, ProductSearchHandler productSearchHandler)
    {
        router = Router.router(vertex);
        router.route().handler(CorsHandler.create("*")
            .allowedMethod(HttpMethod.GET)
            .allowedMethod(HttpMethod.POST)
            .allowedMethod(HttpMethod.DELETE)
            .allowedMethod(HttpMethod.PUT)
            .allowedHeader("Access-Control-Allow-Method")
            .allowedHeader("Access-Control-Allow-Origin")
            .allowedHeader("Access-Control-Allow-Credentials")
            .allowedHeader("Content-Type")
            .allowedHeader("x-request-id")
            .allowedHeader("x-b3-traceid")
            .allowedHeader("x-b3-spanid")
            .allowedHeader("x-b3-parentspanid")
            .allowedHeader("x-b3-sampled")
            .allowedHeader("x-b3-flags")
            .allowedHeader("x-ot-span-context")
            .allowedHeader("authorization"));

        router.route().handler(BodyHandler.create());
        router.get("/health").handler(ctx -> ctx.response().end(new JsonObject().put("status", "UP").toString()));
        router.get("/api/products").handler(productHandler::products);
        router.get("/api/cart/:cartId").handler(cartHandler::getCart);
        router.post("/api/cart/checkout/:cartId").handler(cartHandler::checkout);
        router.post("/api/cart/:cartId/:tmpId").handler(cartHandler::setCart);
        router.post("/api/cart/:cartId/:itemId/:quantity").handler(cartHandler::addToCart);
        router.delete("/api/cart/:cartId/:itemId/:quantity").handler(cartHandler::deleteFromCart);
        router.get("/api/review/:productId").handler(reviewHandler::getReviews);
        router.post("/api/review/:productId/:customerName/:rating/:text").handler(reviewHandler::addReview);
        router.delete("/api/review/:reviewId").handler(reviewHandler::deleteReview);
        router.get("/api/customers/search").handler(customerHandler::getByEmail);
        router.post("/api/customers").handler(customerHandler::save);
        router.put("/api/customers/:customerId").handler(customerHandler::update);
        router.get("/api/v1/product-search").handler(productSearchHandler::performSearch);
    }

    public Router getRouter() {
        return this.router;
    }

}
