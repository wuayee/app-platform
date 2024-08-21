/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Around;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.inspection.Validation;
import com.huawei.jade.service.CarverGlobalOpenTelemetry;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.context.ContextStorage;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link io.opentelemetry.instrumentation.annotations.WithSpan} 的切面。
 *
 * @author 刘信宏
 * @since 2024-07-23
 */
@Aspect
@Component
public class WithSpanAspect {
    private static final ContextKey<String> TRACE_CONTEXT_KEY = ContextKey.named("carver-trace-scope-name");

    private final SpanEndObserverRepository repository;

    public WithSpanAspect(SpanEndObserverRepository repository) {
        this.repository = repository;
    }

    @Around("@annotation(io.opentelemetry.instrumentation.annotations.WithSpan)")
    private Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        AtomicReference<Span> spanRef = new AtomicReference<>();
        Object proceedResult = null;
        try (Scope scope = this.makeCurrentScope(joinPoint, spanRef)) {
            proceedResult = joinPoint.proceed();
            return proceedResult;
        } catch (Throwable throwable) {
            this.recordException(throwable, spanRef.get());
            throw throwable;
        } finally {
            this.notifyAllSpanEndObserver(spanRef.get(), joinPoint.getMethod(), joinPoint.getArgs(), proceedResult);
            this.finishSpan(spanRef.get());
        }
    }

    private void finishSpan(Span span) {
        if (span == null) {
            return;
        }
        span.end();
    }

    private void recordException(Throwable throwable, Span span) {
        if (span == null) {
            return;
        }
        span.setStatus(StatusCode.ERROR, throwable.getMessage());
        span.recordException(throwable);
    }

    private Scope makeCurrentScope(ProceedingJoinPoint joinPoint, AtomicReference<Span> spanRef) {
        String traceScopeName = Context.current().get(TRACE_CONTEXT_KEY);
        if (traceScopeName == null) {
            traceScopeName = joinPoint.getSignature().toString();
            Span span = this.getSpan(joinPoint, traceScopeName);
            Context withContext = Context.current().with(TRACE_CONTEXT_KEY, traceScopeName).with(span);
            spanRef.set(span);
            return ContextStorage.get().attach(withContext);
        }
        Span span = this.getSpan(joinPoint, traceScopeName);
        spanRef.set(span);
        return span.makeCurrent();
    }

    private Span getSpan(ProceedingJoinPoint joinPoint, String traceScopeName) {
        Tracer tracer = CarverGlobalOpenTelemetry.get().getTracer(traceScopeName);
        WithSpan withSpanAnnotation = Validation.notNull(joinPoint.getMethod().getAnnotation(WithSpan.class),
                "The @WithSpan annotation cannot be null.");
        return tracer.spanBuilder(withSpanAnnotation.value()).startSpan();
    }

    private void notifyAllSpanEndObserver(Span span, Method method, Object[] args, Object result) {
        this.repository.get().forEach(observer -> observer.onSpanEnd(span, method, args, result));
    }
}
