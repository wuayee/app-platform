/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop.stub;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

/**
 * 用于测试使用WithSpan注解。
 *
 * @author 马朝阳
 * @since 2024-08-06
 */
@Component
public class SpanDemo {
    /**
     * 用于测试的属性键。
     */
    public static final String SPAN_ATTRIBUTE_KEY = "player";

    /**
     * 用于测试的异常信息。
     */
    public static final String EXCEPTION_MESSAGE = " exception message.";

    /**
     * 用于测试的父级span名称。
     */
    public static final String PARENT_SPAN_NAME = "operation.handle.nested";

    private static final Logger log = Logger.get(SpanDemo.class);

    private final NestedSpanTestService nestedService;

    public SpanDemo(NestedSpanTestService nestedService) {
        this.nestedService = nestedService;
    }

    /**
     * 成功操作。
     *
     * @param player 表示操作参数的 {@link String}。
     */
    @WithSpan(value = "operation.handle.success")
    @GetMapping("/span-demo-success")
    public void handleSuccess(@RequestParam("player_req") @SpanAttribute(SPAN_ATTRIBUTE_KEY) String player) {
        log.debug("input param: {}", player);
    }

    /**
     * 嵌套操作。
     *
     * @param player 表示操作参数的 {@link String}。
     */
    @WithSpan(value = PARENT_SPAN_NAME)
    @GetMapping("/span-demo-nested")
    public void handleNested(@RequestParam("player_req") @SpanAttribute(SPAN_ATTRIBUTE_KEY) String player) {
        this.nestedService.invoke(player);
    }

    /**
     * 异常操作。
     *
     * @param player 表示操作参数的 {@link String}。
     */
    @WithSpan(value = "operation.handle.exception")
    @GetMapping("/span-demo-exception")
    public void handleException(@RequestParam("player_req") @SpanAttribute(SPAN_ATTRIBUTE_KEY) String player) {
        throw new IllegalStateException(player + EXCEPTION_MESSAGE);
    }
}
