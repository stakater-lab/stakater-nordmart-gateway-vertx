package com.stakater.nordmart.gateway.handler;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Future;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerHandler extends NordmartHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerHandler.class);

    public void getByEmail(RoutingContext rc) {
        LOG.debug("Handling getByEmail request API");
        HttpServerRequest request = rc.request();
        String customerEmail = request.getParam("email");
        String authorization = request.getHeader("authorization");

        circuit.executeWithFallback(
                future -> {
                    getWithAuth("/api/customers/search", authorization)
                            .addQueryParam("email", customerEmail != null ? customerEmail : "")
                            .as(BodyCodec.jsonObject()).rxSend()
                            .subscribe(resp -> onSuccess(rc, future, resp), error -> onError(rc, future, error));
                }
                , v -> new JsonObject());
    }

    public void save(RoutingContext rc) {
        LOG.debug("Handling save customer request API");
        String authorization = rc.request().getHeader("authorization");

        circuit.executeWithFallback(
                future -> postWithAuth("/api/customers", authorization)
                        .as(BodyCodec.jsonObject())
                        .rxSendJson(rc.getBodyAsJson())
                        .subscribe(resp -> onSuccess(rc, future, resp), error -> onError(rc, future, error))
                , v -> new JsonObject());
    }

    public void update(RoutingContext rc) {
        LOG.debug("Handling update customer request API");
        HttpServerRequest request = rc.request();
        String customerId = request.getParam("customerId");
        String authorization = request.getHeader("authorization");

        circuit.executeWithFallback(
                future -> putWithAuth("/api/customers/" + customerId, authorization)
                        .as(BodyCodec.jsonObject())
                        .rxSendJson(rc.getBodyAsJson())
                        .subscribe(resp -> onSuccess(rc, future, resp), error -> onError(rc, future, error))
                , v -> new JsonObject());

    }

    private void onError(RoutingContext rc, Future<JsonObject> future, Throwable error) {
        LOG.error("Customer error : {}", error.getMessage());
        rc.fail(error);
        future.fail(error);
    }

    private void onSuccess(RoutingContext rc, Future<JsonObject> future, HttpResponse<JsonObject> resp) {
        LOG.debug("Customer success response : {}", resp.statusCode());
        rc.response().setStatusCode(resp.statusCode()).end(Json.encodePrettily(resp.body()));
        future.complete();
    }

}
