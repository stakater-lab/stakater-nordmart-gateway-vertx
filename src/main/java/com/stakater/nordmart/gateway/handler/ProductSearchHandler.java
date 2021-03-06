package com.stakater.nordmart.gateway.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Future;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductSearchHandler extends NordmartHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(ProductSearchHandler.class);

    public void performSearch(RoutingContext rc)
    {
        String criteria = rc.request().getParam("criteria");

        LOG.info("Going to send request to product search with criteria {}", criteria);
        circuit.executeWithFallback(
            future -> {
                client.get("/api/v1/product-search")
                    .addQueryParam("criteria", criteria)
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
            LOG.error("Request to product search succeeded {}", response.result());
            rc.response().end(response.result().body().toString());
            future.complete();
        }
        else
        {
            LOG.error("Request to product search did not succeeded {} and cause {}", response.result(), response.cause());
            if (response.result() != null) {
                LOG.info("Failed response body {}", response.result().body());
            }
            rc.response().end(new JsonObject().toString());
            future.fail(response.cause());
        }
    }
}
