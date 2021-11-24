package com.zq.config;

import io.vertx.core.Context;
import io.vertx.core.spi.VertxTracerFactory;
import io.vertx.core.spi.tracing.SpanKind;
import io.vertx.core.spi.tracing.TagExtractor;
import io.vertx.core.spi.tracing.VertxTracer;
import io.vertx.core.tracing.TracingOptions;
import io.vertx.core.tracing.TracingPolicy;

/**
 * @Title:
 * @author: zhaoqiang
 * @date: 2021/11/24 5:36 下午
 * @Description:
 */
public class CustomerVertxTracerFactory implements VertxTracerFactory {
    @Override
    public VertxTracer tracer(TracingOptions options) {
        return new VertxTracer() {
            @Override
            public Object receiveRequest(Context context, SpanKind kind, TracingPolicy policy, Object request, String operation, Iterable headers, TagExtractor tagExtractor) {
                return VertxTracer.super.receiveRequest(context, kind, policy, request, operation, headers, tagExtractor);
            }
        };
    }
}
