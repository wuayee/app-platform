/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.eco.llamaindex;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;

/**
 * 表示 {@link ToolFactory} 的 LlamaIndex 的实现。
 *
 * @author 刘信宏
 * @since 2024-06-24
 */
@Component
public class LlamaIndexToolFactory implements ToolFactory {
    private final ObjectSerializer serializer;
    private final BrokerClient brokerClient;

    public LlamaIndexToolFactory(@Fit(alias = "json") ObjectSerializer serializer, BrokerClient brokerClient) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.brokerClient = Validation.notNull(brokerClient, "The broker client cannot be null.");
    }

    @Override
    public String type() {
        return "LlamaIndex";
    }

    @Override
    public Tool create(Tool.Info itemInfo, Tool.Metadata metadata) {
        return new LlamaIndexTool(this.brokerClient, this.serializer, itemInfo, metadata);
    }
}
