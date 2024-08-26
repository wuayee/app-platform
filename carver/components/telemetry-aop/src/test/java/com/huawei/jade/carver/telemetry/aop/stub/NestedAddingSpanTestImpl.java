/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop.stub;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import io.opentelemetry.instrumentation.annotations.AddingSpanAttributes;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;

import java.util.Objects;

/**
 * 嵌套操作测试服务，用于测试 @AddingSpanAttributes 注解。
 *
 * @author 马朝阳
 * @since 2024-08-06
 */
@Component
public class NestedAddingSpanTestImpl implements NestedSpanTestService {
    private static final Logger log = Logger.get(NestedAddingSpanTestImpl.class);

    /**
     * 嵌套正常和异常操作。
     *
     * @param arg 表示嵌套操作参数的 {@link String}。
     */
    @AddingSpanAttributes
    @Override
    public void invoke(@SpanAttribute(NestedSpanTestService.NESTED_ATTR_KEY) String arg) {
        if (Objects.equals(arg, "exception")) {
            throw new IllegalStateException("exception message.");
        }
        log.debug("Nested argument: {}", arg);
    }
}
