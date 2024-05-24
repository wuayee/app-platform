/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool;

import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.support.FitToolFactory;

/**
 * 表示创建工具的工厂。
 *
 * @author 王攀博
 * @since 2024-04-23
 */
public interface ToolFactory {
    /**
     * 获取工厂支持的工具类型。
     * <p>工具类型通过工具的标签体现。</p>
     *
     * @return 表示工厂支持的工具类型的 {@link String}。
     */
    String type();

    /**
     * 创建一个工具。
     *
     * @param itemInfo 表示工具的基本信息的 {@link Tool.Info}。
     * @param metadata 表示工具元数据信息的 {@link Tool.Metadata}。
     * @return 表示创建的工具的 {@link Tool}。
     * @throws IllegalArgumentException 当 {@code itemInfo} 或 {@code metadata} 为 {@code null} 时。
     */
    Tool create(Tool.Info itemInfo, Tool.Metadata metadata);

    /**
     * 创建一个 FIT 调用的工具工厂。
     *
     * @param brokerClient 表示 FIT 调用的客户端代理的 {@link BrokerClient}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @return 表示创建出来的 FIT 调用的工具工厂的 {@link ToolFactory}。
     */
    static ToolFactory fit(BrokerClient brokerClient, ObjectSerializer serializer) {
        return new FitToolFactory(brokerClient, serializer);
    }
}
