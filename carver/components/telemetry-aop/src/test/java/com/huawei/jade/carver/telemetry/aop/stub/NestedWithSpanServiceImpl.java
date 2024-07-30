/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop.stub;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

/**
 * {@link NestedWithSpanService} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-07-29
 */
@Component
public class NestedWithSpanServiceImpl implements NestedWithSpanService {
    private static final Logger log = Logger.get(NestedWithSpanServiceImpl.class);

    @WithSpan(NestedWithSpanService.NESTED_SPAN_NAME)
    @Override
    public void invoke(@SpanAttribute(NestedWithSpanService.NESTED_ATTR_KEY) String arg) {
        log.debug("Nested argument: {}", arg);
    }
}
