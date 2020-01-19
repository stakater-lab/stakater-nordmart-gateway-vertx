package com.stakater.nordmart.gateway.handler;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Future;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.codec.BodyCodec;

public class CustomerHandler extends NordmartHandler {

    public void getByEmail(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        String customerEmail = request.getParam("email");
        String authorization = request.getHeader("authorization");

        circuit.executeWithFallback(
                future -> client.get("/api/customers/search").addQueryParam("email", customerEmail)
                        .putHeader("authorization", authorization)
                        .as(BodyCodec.jsonObject())
                        .rxSend()
                        .subscribe(resp -> onSuccess(rc, future, resp), error -> onError(rc, future, error))
                , v -> new JsonObject());
    }

    public void save(RoutingContext rc) {
        String authorization = rc.request().getHeader("authorization");

        circuit.executeWithFallback(
                future -> client.post("/api/customers").putHeader("authorization", authorization)
                        .as(BodyCodec.jsonObject())
                        .rxSendJson(rc.getBodyAsJson())
                        .subscribe(resp -> onSuccess(rc, future, resp), error -> onError(rc, future, error))
                , v -> new JsonObject());
    }

    public void update(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        String customerId = request.getParam("customerId");
        String authorization = request.getHeader("authorization");

        circuit.executeWithFallback(
                future -> client.put("/api/customers/" + customerId).putHeader("authorization", authorization)
                        .as(BodyCodec.jsonObject())
                        .rxSendJson(rc.getBodyAsJson())
                        .subscribe(resp -> onSuccess(rc, future, resp), error -> onError(rc, future, error))
                , v -> new JsonObject());

    }

    private void onError(RoutingContext rc, Future<JsonObject> future, Throwable error) {
        rc.fail(error);
        future.fail(error);
    }

    private void onSuccess(RoutingContext rc, Future<JsonObject> future, HttpResponse<JsonObject> resp) {
        rc.response().end(Json.encodePrettily(resp.body()));
        future.complete();
    }

}
