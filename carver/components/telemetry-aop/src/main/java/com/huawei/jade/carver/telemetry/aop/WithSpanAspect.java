/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.aop.ProceedingJoinPoint;
import com.huawei.fitframework.aop.annotation.Around;
import com.huawei.fitframework.aop.annotation.Aspect;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.service.CarverGlobalOpenTelemetry;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.context.ContextStorage;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Around("@annotation(io.opentelemetry.instrumentation.annotations.WithSpan)")
    private Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        AtomicReference<Span> spanRef = new AtomicReference<>();
        try (Scope scope = this.makeCurrentScope(joinPoint, spanRef)) {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            this.recordException(throwable, spanRef.get());
            throw throwable;
        } finally {
            this.setParamSpanAttribute(joinPoint, spanRef.get());
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

    private void setParamSpanAttribute(ProceedingJoinPoint joinPoint, Span span) {
        if (span == null) {
            return;
        }
        Annotation[][] parameterAnnotations = joinPoint.getMethod().getParameterAnnotations();
        for (int index = 0; index < parameterAnnotations.length; index++) {
            int currentIndex = index;
            Arrays.stream(parameterAnnotations[index])
                    .filter(annotation -> annotation.annotationType() == SpanAttribute.class)
                    .map(ObjectUtils::<SpanAttribute>cast)
                    .forEach(annotation -> this.setAttribute(span,
                            annotation.value(), joinPoint.getArgs()[currentIndex]));
        }
    }

    private void setAttribute(Span span, String expression, Object paramValue) {
        List<SpanAttributeParser> parsers = SpanAttributeParserRepository.get();
        Map<String, String> attributeMap = parsers.stream()
                .filter(parser -> parser.match(expression))
                .findFirst()
                .map(parser -> parser.parse(expression, paramValue))
                .orElseGet(Collections::emptyMap);
        attributeMap.forEach(span::setAttribute);
    }
}
