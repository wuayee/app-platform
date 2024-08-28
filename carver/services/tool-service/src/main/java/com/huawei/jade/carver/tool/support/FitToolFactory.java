/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;

/**
 * 表示创建参数工具的工厂。
 *
 * @author 王攀博
 * @since 2024-04-23
 */
public class FitToolFactory implements ToolFactory {
    private final BrokerClient brokerClient;
    private final ObjectSerializer serializer;

    public FitToolFactory(BrokerClient brokerClient, ObjectSerializer serializer) {
        this.brokerClient = notNull(brokerClient, "The broker client cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Nonnull
    @Override
    public String type() {
        return FitTool.TYPE;
    }

    @Override
    public Tool create(Tool.Info itemInfo, Tool.Metadata metadata) {
        return new FitTool(this.brokerClient, this.serializer, itemInfo, metadata);
    }
}
