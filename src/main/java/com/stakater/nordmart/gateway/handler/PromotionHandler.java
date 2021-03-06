package com.stakater.nordmart.gateway.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Future;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PromotionHandler extends NordmartHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(PromotionHandler.class);

    public void getPromotions(RoutingContext rc)
    {
        String itemId = rc.request().getParam("itemId");

        circuit.executeWithFallback(
                future -> {
                    client.get("/promotions/" + itemId).as(BodyCodec.jsonObject())
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
