/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolFactory;

/**
 * 表示创建参数工具的工厂。
 *
 * @author 王攀博
 * @since 2024-04-23
 */
@Component
public class FitToolFactory implements ToolFactory {
    private final BrokerClient brokerClient;
    private final ObjectSerializer serializer;

    /**
     * 创建 FIT 工厂工厂的实例。
     *
     * @param brokerClient 表示服务调用代理客户端的 {@link BrokerClient}。
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @throws IllegalArgumentException 当 {@code brokerClient}、{@code serializer} 为 {@code null} 时。
     */
    public FitToolFactory(BrokerClient brokerClient, ObjectSerializer serializer) {
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Override
    public String type() {
        return FitTool.TYPE;
    }

    @Override
    public Tool create(Tool.Info itemInfo, Tool.Metadata metadata) {
        return new FitTool(this.brokerClient, this.serializer, itemInfo, metadata);
    }
}
