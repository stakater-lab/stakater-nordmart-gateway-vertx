package com.stakater.nordmart.gateway.handler;

import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.circuitbreaker.CircuitBreaker;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.client.HttpRequest;
import io.vertx.rxjava.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NordmartHandler {
    WebClient client;
    CircuitBreaker circuit;
    private static final Logger LOG = LoggerFactory.getLogger(NordmartHandler.class);

    public void setCircuit(CircuitBreaker circuit) {
        this.circuit = circuit;
    }

    public void setClient(WebClient client) {
        this.client = client;
    }

    protected HttpRequest<Buffer> getWithAuth(String uri, String authorization) {
        return requestWithAuth(uri, HttpMethod.GET, authorization);
    }

    protected HttpRequest<Buffer> postWithAuth(String uri, String authorization) {
        return requestWithAuth(uri, HttpMethod.POST, authorization);
    }

    protected HttpRequest<Buffer> putWithAuth(String uri, String authorization) {
        return requestWithAuth(uri, HttpMethod.PUT, authorization);
    }

    protected HttpRequest<Buffer> deleteWithAuth(String uri, String authorization) {
        return requestWithAuth(uri, HttpMethod.DELETE, authorization);
    }

    private HttpRequest<Buffer> requestWithAuth(String uri, HttpMethod method, String authorization) {
        HttpRequest<Buffer> bufferHttpRequest = client.request(method, uri);
        if (authorization == null || authorization.trim().equals("")) {
            LOG.warn("Authorization token is null");
            return bufferHttpRequest;
        }
        return bufferHttpRequest.putHeader("authorization", authorization);
    }
}
