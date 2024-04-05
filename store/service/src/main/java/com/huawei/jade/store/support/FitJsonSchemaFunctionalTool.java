/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.broker.client.BrokerClient;

import java.util.Map;

/**
 * 表示函数工具的带规范描述的 FIT 服务实现。
 *
 * @author 季聿阶
 * @since 2024-04-06
 */
public class FitJsonSchemaFunctionalTool extends AbstractSchemaFunctionalTool {
    private final BrokerClient brokerClient;
    private final String genericableId;

    /**
     * 通过 FIT 调用客户端和本地方法来初始化 {@link AbstractTool} 的新实例。
     *
     * @param brokerClient 表示 FIT 调用客户端的 {@link BrokerClient}。
     * @param toolSchema 表示工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public FitJsonSchemaFunctionalTool(BrokerClient brokerClient, Map<String, Object> toolSchema) {
        super(toolSchema);
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
        notNull(toolSchema, "The tool schema cannot be null.");
        this.genericableId = notBlank(cast(toolSchema.get("genericableId")), "The genericable id cannot be blank.");
    }

    @Override
    public Object call(Object... args) {
        return this.brokerClient.getRouter(this.genericableId).route().invoke(args);
    }
}
