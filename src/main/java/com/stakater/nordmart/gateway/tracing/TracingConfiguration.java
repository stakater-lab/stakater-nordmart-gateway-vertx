package com.stakater.nordmart.gateway.tracing;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import com.uber.jaeger.Tracer.Builder;
import com.uber.jaeger.metrics.Metrics;
import com.uber.jaeger.metrics.NullStatsReporter;
import com.uber.jaeger.metrics.StatsFactoryImpl;
import com.uber.jaeger.propagation.b3.B3TextMapCodec;
import com.uber.jaeger.reporters.RemoteReporter;
import com.uber.jaeger.samplers.ConstSampler;
import com.uber.jaeger.senders.HttpSender;
import io.opentracing.propagation.Format.Builtin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.okhttp3.OkHttpSender;

@org.springframework.context.annotation.Configuration
public class TracingConfiguration
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TracingConfiguration.class);

    @Autowired
    private Environment env;

    @Bean
    public io.opentracing.Tracer jaegerTracer()
    {
        String jaegerServiceName = env.getProperty("opentracing.servicename");
        String jaegerEndpoint = env.getProperty("jaeger.collector.endpoint");

        LOGGER.info("Service name for jaeger tracing {}, jaeger endpoint {}", jaegerServiceName, jaegerEndpoint);

        Builder builder = new Builder(jaegerServiceName,
                new RemoteReporter(new HttpSender(jaegerEndpoint), 10,
                        65000, new Metrics(new StatsFactoryImpl(new NullStatsReporter()))),
                new ConstSampler(true))
                .registerInjector(Builtin.HTTP_HEADERS, new B3TextMapCodec())
                .registerExtractor(Builtin.HTTP_HEADERS, new B3TextMapCodec());
        return builder.build();
    }

//    @Bean
    public io.opentracing.Tracer zipkinTracer()
    {
        OkHttpSender okHttpSender = OkHttpSender.create("http://zipkin.istio-system:9411/api/v1/spans");
        AsyncReporter<Span> reporter = AsyncReporter.builder(okHttpSender).build();
        Tracing braveTracer = Tracing.newBuilder().localServiceName("spring-boot").reporter(reporter).build();
        return BraveTracer.create(braveTracer);
    }
}
