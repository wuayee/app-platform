/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Around;
import modelengine.fitframework.aop.annotation.Aspect;

import io.opentelemetry.api.trace.Span;

/**
 * {@link io.opentelemetry.instrumentation.annotations.AddingSpanAttributes} 的切面。
 *
 * @author 马朝阳
 * @since 2024-08-05
 */
@Aspect
@Component
public class AddingSpanAttributesAspect {
    private final SpanAttributesObserver observer;

    /**
     * 构造函数，用于注入{@link SpanEndObserver}。
     *
     * @param observer 观察者对象
     */
    public AddingSpanAttributesAspect(@Fit(alias = "ParamSpanAttributeInjector") SpanAttributesObserver observer) {
        this.observer = observer;
    }

    @Around("@annotation(io.opentelemetry.instrumentation.annotations.AddingSpanAttributes)")
    private Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        Span current = Span.current();
        Object proceedResult = null;
        try {
            proceedResult = joinPoint.proceed();
            return proceedResult;
        } finally {
            this.observer.onAddingSpanAttribute(current, joinPoint.getMethod(), joinPoint.getArgs(), proceedResult);
        }
    }
}