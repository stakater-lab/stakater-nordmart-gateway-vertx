package com.stakater.nordmart.gateway.tracing;

import io.vertx.core.Handler;
import io.vertx.ext.web.client.impl.WebClientInternal;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TracingInterceptor
{
    private static final String X_TRACING_HEADERS = "X-Tracing-Headers";
    private static final List<String> FORWARDED_HEADER_NAMES = Arrays.asList(
            "x-request-id",
            "x-b3-traceid",
            "x-b3-spanid",
            "x-b3-parentspanid",
            "x-b3-sampled",
            "x-b3-flags",
            "x-ot-span-context",
            "b3"
    );

    public static Handler<RoutingContext> create() {
        return rc -> {
            Set<String> names = rc.request().headers().names();
            Map<String, List<String>> headers = names.stream()
                    .map(String::toLowerCase)
                    .filter(FORWARDED_HEADER_NAMES::contains)
                    .collect(Collectors.toMap(
                            Function.identity(),
                            h -> Collections.singletonList(rc.request().getHeader(h))
                    ));
            rc.put(X_TRACING_HEADERS, headers);
            rc.next();
        };
    }

    public static WebClient propagate(WebClient client, RoutingContext rc) {
        WebClientInternal clientInternal = (WebClientInternal) client.getDelegate();
        clientInternal.addInterceptor(ctx -> {
            Map<String, List<String>> headers = rc.get(X_TRACING_HEADERS);
            if (headers != null) {
                System.out.println("Propagating... " + headers);
                headers.forEach((s, l) -> l.forEach(v -> ctx.request().putHeader(s, v)));
            }
            ctx.next();
        });
        return client;
    }
}
