/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.waterflow;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;
import com.huawei.jade.carver.tool.service.ToolExecuteService;
import com.huawei.jade.carver.tool.service.ToolService;
import modelengine.fel.chat.character.ToolMessage;
import modelengine.fel.chat.protocol.FlatChatMessage;
import modelengine.fel.tool.Tool;
import modelengine.fel.tool.ToolCall;
import modelengine.fel.tool.ToolProvider;

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
    private static final String WATER_FLOW_ASYNC_KEY = "isAsync";

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
    public FlatChatMessage call(ToolCall toolCall, Map<String, Object> toolContext) {
        this.addDynamicParams(toolCall, toolContext);
        return FlatChatMessage.from(new ToolMessage(toolCall.getId(),
                this.executeService.executeTool(toolCall.getName(), toolCall.getParameters())));
    }

    private void addDynamicParams(ToolCall toolCall, Map<String, Object> toolContext) {
        Map<String, Object> parameters = this.objectSerializer.deserialize(toolCall.getParameters(),
                TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class}));
        // 目前普通工具和工具流调用都会走到这里，先将校验去掉，等 ToolProvider 加上责任链后再加回去。
        if (!parameters.containsKey(WaterFlowToolConst.INPUT_PARAMS_KEY)) {
            return;
        }
        Map<String, Object> inputParams = cast(parameters.get(WaterFlowToolConst.INPUT_PARAMS_KEY));
        if (!toolContext.containsKey(WaterFlowToolConst.TRACE_ID)
                || !toolContext.containsKey(WaterFlowToolConst.CALLBACK_ID) || !toolContext.containsKey(
                WaterFlowToolConst.USER_ID)) {
            throw new IllegalStateException("toolContext does not contain traceId or callbackId or userId.");
        }
        inputParams.putAll(toolContext);
        toolCall.setParameters(this.objectSerializer.serialize(parameters));
    }

    @Override
    @Fitable(id = "app-factory")
    public List<Tool> getTool(List<String> name) {
        return name.stream().map(this.toolService::getTool).filter(Objects::nonNull).map(toolData -> {
            Set<String> tags = toolData.getTags();
            Map<String, Object> schema = new HashMap<>(toolData.getSchema());
            if (tags.contains(TAG_TYPE_WATER_FLOW)) {
                schema = DefaultValueFilterToolInfo.getFilterSchema(schema);
            }
            schema.put(SCHEMA_NAME_KEY, toolData.getUniqueName());
            Map<String, Object> runnables = toolData.getRunnables();
            Object waterflowRunnable = runnables.get(TAG_TYPE_WATER_FLOW);
            Map<String, Object> waterflowContext;
            if (waterflowRunnable == null) {
                waterflowContext = new HashMap<>();
            } else if (!(waterflowRunnable instanceof Map)) {
                throw new IllegalStateException(StringUtils.format("Incorrect tool runnable info. [toolUniqueName={0}]",
                        toolData.getUniqueName()));
            } else {
                waterflowContext = cast(waterflowRunnable);
            }
            if (tags.contains(TAG_TYPE_WATER_FLOW)) {
                waterflowContext.put(WATER_FLOW_ASYNC_KEY, "true");
            }
            Tool tool = new Tool();
            tool.setSchema(schema);
            tool.setContext(waterflowContext);
            return tool;
        }).collect(Collectors.toList());
    }
}
