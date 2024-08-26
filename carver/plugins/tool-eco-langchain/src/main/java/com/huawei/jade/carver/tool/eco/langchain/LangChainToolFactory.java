/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.eco.langchain;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;

/**
 * 表示 {@link ToolFactory} 的 LangChain 的实现。
 *
 * @author 刘信宏
 * @since 2024-06-19
 */
@Component
public class LangChainToolFactory implements ToolFactory {
    private final ObjectSerializer serializer;
    private final BrokerClient brokerClient;

    public LangChainToolFactory(@Fit(alias = "json") ObjectSerializer serializer,
            BrokerClient brokerClient) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.brokerClient = Validation.notNull(brokerClient, "The broker client cannot be null.");
    }

    @Nonnull
    @Override
    public String type() {
        return "LangChain";
    }

    @Override
    public Tool create(Tool.Info itemInfo, Tool.Metadata metadata) {
        return new LangChainTool(this.brokerClient, this.serializer, itemInfo, metadata);
    }
}
