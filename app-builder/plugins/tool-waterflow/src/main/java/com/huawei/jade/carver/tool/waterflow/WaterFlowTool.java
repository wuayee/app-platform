/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.waterflow;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.jade.carver.tool.Tool;

/**
 * 表示 {@link WaterFlowTool} 的自定义实现。
 *
 * @author 王攀博
 * @since 2024-04-18
 */
public class WaterFlowTool implements Tool {
    private final Tool tool;
    private final Info toolInfo;
    private final Metadata metadata;

    /**
     * 通过 Json 处理工具和工具元数据来初始化 {@link WaterFlowTool} 的新实例。
     *
     * @param tool 表示工作流工具入口调用的真实工具的 {@link Tool}。
     * @param info 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工作流工具的元数据信息的 {@link Metadata}。
     * @throws IllegalArgumentException 当 {@code argsTool} 或 {@code metadata} 为 {@code null} 时。
     */
    public WaterFlowTool(Tool tool, Info info, Tool.Metadata metadata) {
        this.tool = notNull(tool, "The tool cannot be null.");
        this.toolInfo = new DefaultValueFilterToolInfo(info);
        this.metadata = notNull(metadata, "The tool metadata cannot be null.");
    }

    @Override
    public Info info() {
        return this.toolInfo;
    }

    @Override
    public Metadata metadata() {
        return this.metadata;
    }

    @Override
    public Object call(Object... args) {
        return this.tool.call(args);
    }

    @Override
    public String callByJson(String jsonArgs) {
        return this.tool.callByJson(jsonArgs);
    }
}
