/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.eco.langchain;

import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.eco.AbstractKvTool;

/**
 * 表示 {@link com.huawei.jade.carver.tool.Tool} 的
 * <a href="https://python.langchain.com/v0.2/docs/integrations/tools/">LangChain</a> 的实现。
 *
 * @author 刘信宏
 * @since 2024-06-19
 */
public class LangChainTool extends AbstractKvTool {
    /**
     * 通过 Json 序列化器、工具的基本信息和工具元数据来初始化 {@link LangChainTool} 的新实例。
     *
     * @param brokerClient 表示服务调用的代理客户端的 {@link BrokerClient}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param itemInfo 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    public LangChainTool(BrokerClient brokerClient, ObjectSerializer serializer, Info itemInfo, Metadata metadata) {
        super(brokerClient, serializer, itemInfo, metadata);
    }

    @Override
    protected String type() {
        return "LangChain";
    }
}
