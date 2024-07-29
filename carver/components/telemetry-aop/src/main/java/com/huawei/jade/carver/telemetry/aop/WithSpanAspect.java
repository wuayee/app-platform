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
    @Around("@annotation(io.opentelemetry.instrumentation.annotations.WithSpan)")
    private Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        Tracer tracer = CarverGlobalOpenTelemetry.get().getTracer(joinPoint.getSignature().toString());
        WithSpan withSpanAnnotation = Validation.notNull(joinPoint.getMethod().getAnnotation(WithSpan.class),
                "The @WithSpan annotation cannot be null.");
        Span span = tracer.spanBuilder(withSpanAnnotation.value()).startSpan();
        try (Scope scope = span.makeCurrent()) {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            span.setStatus(StatusCode.ERROR, throwable.getMessage());
            span.recordException(throwable);
            throw throwable;
        } finally {
            setParamSpanAttribute(joinPoint, span);
            span.end();
        }
    }

    private void setParamSpanAttribute(ProceedingJoinPoint joinPoint, Span span) {
        Annotation[][] parameterAnnotations = joinPoint.getMethod().getParameterAnnotations();
        AtomicReference<Integer> indexRef = new AtomicReference<>();
        for (int index = 0; index < parameterAnnotations.length; index++) {
            indexRef.set(index);
            Arrays.stream(parameterAnnotations[index])
                    .filter(annotation -> annotation.annotationType() == SpanAttribute.class)
                    .map(ObjectUtils::<SpanAttribute>cast)
                    .forEach(annotation -> this.setAttribute(span,
                            annotation.value(),
                            joinPoint.getArgs()[indexRef.get()]));
        }
    }

    private void setAttribute(Span span, String expression, Object paramValue) {
        List<SpanAttributeParser> parsers = SpanAttributeParserFactory.create().build();
        Map<String, String> attributeMap = parsers.stream()
                .filter(parser -> parser.match(expression))
                .findFirst()
                .map(parser -> parser.parse(expression, paramValue))
                .orElseGet(Collections::emptyMap);
        attributeMap.forEach(span::setAttribute);
    }
}
