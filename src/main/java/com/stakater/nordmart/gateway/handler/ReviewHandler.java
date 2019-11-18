package com.stakater.nordmart.gateway.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Future;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewHandler extends NordmartHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(ReviewHandler.class);

    /*public void getReviews(RoutingContext rc)
    {
        String productId = rc.request().getParam("productId");

        circuit.executeWithFallback(
                future -> {
                    client.get("/api/review/" + productId).as(BodyCodec.jsonObject())
                            .send(ar -> {
                                handleResponse(ar, rc, future);
                            });
                }, v -> new JsonObject());
    }*/

    public void getReviews(RoutingContext rc)
    {
        String productId = rc.request().getParam("productId");
        // Retrieve reviews
        client.get("/api/review/" + productId).as(BodyCodec.jsonArray()).rxSend()
                .map(resp -> {
                    if (resp.statusCode() != 200)
                    {
                        throw new RuntimeException("Invalid response from the review service: " + resp.statusCode());
                    }
                    return resp.body();
                });
    }

    public void addReview(RoutingContext rc)
    {

        String productId = rc.request().getParam("productId");
        String customerName = rc.request().getParam("customerName");
        String rating = rc.request().getParam("rating");
        String text = rc.request().getParam("text");

        circuit.executeWithFallback(
            future -> {
                client.post("/api/review/" + productId + "/" + customerName + "/" + rating + "/" + text)
                    .as(BodyCodec.jsonObject())
                    .send(ar -> {
                        handleResponse(ar, rc, future);
                    });
            }, v -> new JsonObject());
    }

    public void deleteReview(RoutingContext rc)
    {
        String reviewId = rc.request().getParam("reviewId");

        circuit.executeWithFallback(
            future -> {
                client.delete("/api/review/" + reviewId)
                    .as(BodyCodec.jsonObject())
                    .send(ar -> {
                        handleResponse(ar, rc, future);
                    });
            }, v -> new JsonObject());
    }

    private void handleResponse(AsyncResult<HttpResponse<JsonObject>> response, RoutingContext rc, Future<JsonObject>
        future)
    {
        if (response.succeeded())
        {
            rc.response().end(response.result().body().toString());
            future.complete();
        }
        else
        {
            rc.response().end(new JsonObject().toString());
            future.fail(response.cause());
        }
    }
}
