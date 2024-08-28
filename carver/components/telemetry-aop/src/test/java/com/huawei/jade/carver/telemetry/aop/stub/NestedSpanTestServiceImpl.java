/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop.stub;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

/**
 * {@link NestedSpanTestService} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-07-29
 */
@Component
public class NestedSpanTestServiceImpl implements NestedSpanTestService {
    private static final Logger log = Logger.get(NestedSpanTestServiceImpl.class);

    @WithSpan(NestedSpanTestService.NESTED_SPAN_NAME)
    @Override
    public void invoke(@SpanAttribute(NestedSpanTestService.NESTED_ATTR_KEY) String arg) {
        log.debug("Nested argument: {}", arg);
    }
}
