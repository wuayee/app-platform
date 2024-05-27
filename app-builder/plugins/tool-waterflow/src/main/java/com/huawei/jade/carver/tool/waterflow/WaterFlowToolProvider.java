/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.waterflow;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.jade.carver.tool.service.ToolExecuteService;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.fel.chat.character.ToolMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolContext;
import com.huawei.jade.fel.tool.ToolProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link ToolProvider} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-4-17
 */
@Component
public class WaterFlowToolProvider implements ToolProvider {
    private static final String SCHEMA_NAME_KEY = "name";
    private static final String TAG_TYPE_WATER_FLOW = "WATERFLOW";

    private final ToolExecuteService executeService;
    private final ToolService toolService;

    /**
     * 创建默认工具提供者。
     *
     * @param executeService 表示执行服务的 {@link ToolExecuteService}。
     * @param toolService 表示查询服务的 {@link ToolService}。
     */
    public WaterFlowToolProvider(ToolExecuteService executeService, ToolService toolService) {
        this.executeService = executeService;
        this.toolService = toolService;
    }

    @Override
    @Fitable(id = "app-factory")
    public FlatChatMessage call(ToolCall toolCall, ToolContext toolContext) {
        return new FlatChatMessage(new ToolMessage(toolCall.getId(),
                this.executeService.executeTool(toolCall.getName(), toolCall.getParameters())));
    }

    @Override
    @Fitable(id = "app-factory")
    public List<Tool> getTool(List<String> name) {
        return name.stream().map(this.toolService::getTool).filter(Objects::nonNull).map(tool -> {
            Set<String> tags = tool.getTags();
            Map<String, Object> schema = new HashMap<>(tool.getSchema());
            if (tags.contains(TAG_TYPE_WATER_FLOW)) {
                schema = DefaultValueFilterToolInfo.getFilterSchema(schema);
            }
            schema.put(SCHEMA_NAME_KEY, tool.getUniqueName());
            return new Tool(tags.contains(TAG_TYPE_WATER_FLOW), schema);
        }).collect(Collectors.toList());
    }
}
