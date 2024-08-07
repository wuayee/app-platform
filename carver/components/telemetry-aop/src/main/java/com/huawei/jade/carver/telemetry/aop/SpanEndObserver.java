/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import io.opentelemetry.api.trace.Span;

import java.lang.reflect.Method;

/**
 * 观察者接口，在 span 结束前对 span 进行处理。
 *
 * @author 方誉州
 * @since 2024-08-06
 */
public interface SpanEndObserver {
    /**
     * 在 span 结束前根据上下文对其处理。
     *
     * @param span 表示待设置属性的{@link Span}。
     * @param method 表示被拦截的方法的{@link Method}。
     * @param args 表示被拦截方法的参数列表的{@link Object}{@code []}。
     * @param result 表示被拦截方法的返回结果的{@link Object}。
     */
    void onSpanEnd(Span span, Method method, Object[] args, Object result);
}
