/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop.observers;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.carver.telemetry.aop.SpanAttributeParser;
import com.huawei.jade.carver.telemetry.aop.SpanAttributeParserRepository;
import com.huawei.jade.carver.telemetry.aop.SpanAttributesObserver;
import com.huawei.jade.carver.telemetry.aop.SpanEndObserver;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 向 span 中注入 {@link SpanAttribute} 注解的属性键值对。
 *
 * @author 方誉州
 * @since 2024-08-06
 */
@Component(name = "ParamSpanAttributeInjector")
public class ParamSpanAttributeInjector implements SpanEndObserver, SpanAttributesObserver {
    private final SpanAttributeParserRepository repository;

    public ParamSpanAttributeInjector(SpanAttributeParserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onSpanEnd(Span span, Method method, Object[] args, Object result) {
        onAddingSpanAttribute(span, method, args, result);
    }

    private void setAttribute(Span span, String expression, Object paramValue) {
        List<SpanAttributeParser> parsers = this.repository.get();
        Map<String, String> attributeMap = parsers.stream()
                .filter(parser -> parser.match(expression))
                .findFirst()
                .map(parser -> parser.parse(expression, paramValue))
                .orElseGet(Collections::emptyMap);
        attributeMap.forEach(span::setAttribute);
    }

    @Override
    public void onAddingSpanAttribute(Span span, Method method, Object[] args, Object result) {
        if (span == null) {
            return;
        }
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int index = 0; index < parameterAnnotations.length; index++) {
            int currentIndex = index;
            Arrays.stream(parameterAnnotations[index])
                    .filter(annotation -> annotation.annotationType() == SpanAttribute.class)
                    .map(ObjectUtils::<SpanAttribute>cast)
                    .forEach(annotation -> this.setAttribute(span, annotation.value(), args[currentIndex]));
        }
    }
}