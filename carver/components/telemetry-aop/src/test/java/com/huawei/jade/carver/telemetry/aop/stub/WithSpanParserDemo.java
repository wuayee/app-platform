/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop.stub;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.carver.telemetry.aop.WithSpanAspect;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

/**
 * 为 {@link WithSpanAspect} 测试创建的类，测试复杂对象调用流程。
 *
 * @author 马朝阳
 * @since 2024-07-31
 */
@Component
public class WithSpanParserDemo {
    private static final Logger log = Logger.get(WithSpanParserDemo.class);

    private static final String BASE_ATTRIBUTE_KEY = "player:k1";
    private static final String KV_ATTRIBUTE_KEY = "player:k1.k2,player2:k11.k22";
    private static final String OBJECT_ATTRIBUTE_KEY = "player:k1,player2:$.k1.k2,player3:k1.k2";
    private static final String LIST_ATTRIBUTE_KEY = "player:[0].k1,player2:$[0].k1.k2,player3:[0].k2";

    /**
     * 基本数据类型解析操作。
     *
     * @param player 表示复杂操作参数的 {@link Object}。
     */
    @WithSpan(value = "operation.primitive.parser")
    public void handlePrimitiveParser(@SpanAttribute(BASE_ATTRIBUTE_KEY) Object player) {
        log.debug("input param: {}", player);
    }

    /**
     * 键值对解析操作。
     *
     * @param player 表示复杂操作参数的 {@link Object}。
     */
    @WithSpan(value = "operation.kv.parser")
    public void handleKVParser(@SpanAttribute(KV_ATTRIBUTE_KEY) Object player) {
        log.debug("input param: {}", player);
    }

    /**
     * 对象解析操作。
     *
     * @param player 表示复杂操作参数的 {@link Object}。
     */
    @WithSpan(value = "operation.object.parser")
    public void handleObjectParser(@SpanAttribute(OBJECT_ATTRIBUTE_KEY) Object player) {
        log.debug("input param: {}", player);
    }

    /**
     * 列表解析操作。
     *
     * @param player 表示复杂操作参数的 {@link Object}。
     */
    @WithSpan(value = "operation.list.parser")
    public void handleListParser(@SpanAttribute(LIST_ATTRIBUTE_KEY) Object player) {
        log.debug("input param: {}", player);
    }
}
