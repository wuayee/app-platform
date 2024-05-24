/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.aiflow;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.support.AbstractTool;

/**
 * 表示 {@link Tool} 的通过 FEL 编写出的 AI-Flow 的工具实现。
 *
 * @author 季聿阶
 * @since 2024-05-15
 */
public class AiFlowTool extends AbstractTool {
    /**
     * 通过 Json 序列化器、工具的基本信息和工具元数据来初始化 {@link AbstractTool} 的新实例。
     *
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param itemInfo 表示工具的基本信息的 {@link Tool.Info}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    protected AiFlowTool(ObjectSerializer serializer, Tool.Info itemInfo, Metadata metadata) {
        super(serializer, itemInfo, metadata);
    }

    @Override
    public Object call(Object... args) {
        return null;
    }

    @Override
    public String callByJson(String jsonArgs) {
        return null;
    }
}
