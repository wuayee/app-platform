/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolEntity;
import com.huawei.jade.fel.tool.ToolFactory;
import com.huawei.jade.fel.tool.ToolFactoryRepository;
import com.huawei.jade.fel.tool.service.ToolExecuteService;
import com.huawei.jade.fel.tool.service.ToolRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 表示 {@link ToolExecuteService} 得默认实现。
 *
 * @author 易文渊
 * @since 2024-08-15
 */
@Component
public class DefaultToolExecutor implements ToolExecuteService {
    private final ToolRepository toolRepository;
    private final ToolFactoryRepository toolFactoryRepository;
    private final ObjectSerializer serializer;

    /**
     * 构造一个 {@link DefaultToolExecutor} 的实例。
     *
     * @param toolRepository 表示工具仓库的 {@link ToolRepository}。
     * @param toolFactoryRepository 表示工具工厂仓库的 {@link ToolFactoryRepository}。
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @throws IllegalArgumentException 当 {@code toolRepository}、{@code toolFactoryRepository} 为 {@code null} 时。
     */
    public DefaultToolExecutor(ToolRepository toolRepository, ToolFactoryRepository toolFactoryRepository,
            ObjectSerializer serializer) {
        this.toolRepository = notNull(toolRepository, "The tool repository cannot be null.");
        this.toolFactoryRepository = notNull(toolFactoryRepository, "The tool factory repository cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Override
    @Fitable(id = "standard")
    public String execute(String group, String toolName, String jsonArgs) {
        Tool tool = this.getTool(group, toolName);
        Object output = tool.executeWithJson(jsonArgs);
        return this.convertOutput(group, tool.metadata().returnConverter(), output);
    }

    @Override
    @Fitable(id = "standard")
    public String execute(String group, String toolName, Map<String, Object> jsonObject) {
        Tool tool = this.getTool(group, toolName);
        Object output = tool.executeWithJsonObject(jsonObject);
        return this.convertOutput(group, tool.metadata().returnConverter(), output);
    }

    private Tool getTool(String group, String toolName) {
        ToolEntity tool = notNull(toolRepository.getTool(group, toolName),
                () -> new IllegalStateException(StringUtils.format("The tool cannot be found. [group={0}, tool={1}]",
                        group,
                        toolName)));
        Set<String> runnable = tool.runnable().keySet();
        Optional<ToolFactory> factory = this.toolFactoryRepository.match(runnable);
        if (!factory.isPresent()) {
            throw new IllegalStateException(StringUtils.format("No tool factory to create tool. [runnable={0}]",
                    runnable));
        }
        Tool.Metadata metadata = Tool.Metadata.from(tool.schema());
        return factory.get().create(tool, metadata);
    }

    private String convertOutput(String group, String convertor, Object output) {
        if (StringUtils.isBlank(convertor)) {
            return output.getClass() == String.class ? cast(output) : serializer.serialize(output);
        }
        Tool convertorTool = getTool(group, convertor);
        return convertorTool.execute(output).toString();
    }
}