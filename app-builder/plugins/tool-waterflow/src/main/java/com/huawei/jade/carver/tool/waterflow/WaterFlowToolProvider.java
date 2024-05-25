/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.waterflow;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.carver.tool.service.ToolExecuteService;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.fel.chat.character.ToolMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolContext;
import com.huawei.jade.fel.tool.ToolProvider;

import java.lang.reflect.Type;
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
    private final ObjectSerializer objectSerializer;

    /**
     * 创建默认工具提供者。
     *
     * @param executeService 表示执行服务的 {@link ToolExecuteService}。
     * @param toolService 表示查询服务的 {@link ToolService}。
     * @param objectSerializer 表示对象序列化器的 {@link ObjectSerializer}。
     */
    public WaterFlowToolProvider(ToolExecuteService executeService, ToolService toolService,
            @Fit(alias = "json") ObjectSerializer objectSerializer) {
        this.executeService = executeService;
        this.toolService = toolService;
        this.objectSerializer = objectSerializer;
    }

    @Override
    @Fitable(id = "app-factory")
    public FlatChatMessage call(ToolCall toolCall, ToolContext toolContext) {
        this.addDynamicParams(toolCall, toolContext);
        return new FlatChatMessage(new ToolMessage(toolCall.getId(),
                this.executeService.executeTool(toolCall.getName(), toolCall.getParameters())));
    }

    private void addDynamicParams(ToolCall toolCall, ToolContext toolContext) {
        Map<String, Object> parameters = this.objectSerializer.deserialize(toolCall.getParameters(),
                TypeUtils.parameterized(Map.class, new Type[]{String.class, Object.class}));
        // 目前普通工具和工具流调用都会走到这里，先将校验去掉，等 ToolProvider 加上责任链后再加回去。
        if (!parameters.containsKey(WaterFlowToolConst.INPUT_PARAMS_KEY)) {
            return;
        }
        Map<String, Object> inputParams = ObjectUtils.cast(parameters.get(WaterFlowToolConst.INPUT_PARAMS_KEY));
        Map<String, String> context = toolContext.toMap();
        if (!context.containsKey(WaterFlowToolConst.TRACE_ID) || !context.containsKey(WaterFlowToolConst.CALLBACK_ID)) {
            throw new IllegalStateException("toolContext does not contain traceId or callbackId.");
        }
        inputParams.putAll(context);
        toolCall.setParameters(this.objectSerializer.serialize(parameters));
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
