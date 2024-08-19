/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginCategory;
import com.huawei.fitframework.plugin.PluginStartedObserver;
import com.huawei.fitframework.plugin.PluginStoppingObserver;
import com.huawei.fitframework.resource.Resource;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.ArrayUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.fel.tool.ToolEntity;
import com.huawei.jade.fel.tool.ToolSchema;
import com.huawei.jade.fel.tool.service.ToolRepository;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示默认的工具方法自动装配器。
 * <p>当插件加载和卸载时，会根据插件暴露的工具方法，进行工具的加载和卸载。</p>
 *
 * @author 易文渊
 * @since 2024-08-15
 */
@Component
public class DefaultToolDiscoverer implements PluginStartedObserver, PluginStoppingObserver {
    private final ToolRepository toolRepository;
    private final ObjectSerializer serializer;
    private final int maxToolNum;

    /**
     * 构造工具自动装配器的实例。
     *
     * @param toolRepository 表示工具仓库的 {@link ToolRepository}。
     * @param serializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @param maxNum 表示插件最大工具数量的 {@code int}。
     * @throws IllegalArgumentException 当 {@code toolRepository}、{@code objectSerializer} 为 {@code null} 时。
     */
    public DefaultToolDiscoverer(ToolRepository toolRepository, ObjectSerializer serializer,
            @Value("${tool.max-num}") int maxNum) {
        this.toolRepository = notNull(toolRepository, "The tool repository cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.maxToolNum = maxNum;
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        if (shouldSkipPlugin(plugin)) {
            return;
        }
        this.scanTools(plugin).forEach(this.toolRepository::addTool);
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        if (shouldSkipPlugin(plugin)) {
            return;
        }
        List<ToolEntity> toolEntities = this.scanTools(plugin);
        isTrue(toolEntities.size() < this.maxToolNum, "The tool num in plugin must less than {}", this.maxToolNum);
        toolEntities.forEach(tool -> this.toolRepository.deleteTool(tool.namespace(), tool.name()));
    }

    private static boolean shouldSkipPlugin(Plugin plugin) {
        return plugin.metadata().category() != PluginCategory.USER;
    }

    private List<ToolEntity> scanTools(Plugin plugin) {
        try {
            Resource[] resources = plugin.resolverOfResources().resolve(ToolSchema.TOOL_MANIFEST);
            if (ArrayUtils.isEmpty(resources)) {
                return Collections.emptyList();
            }
            return Arrays.stream(resources)
                    .flatMap(resource -> this.parseTools(resource).stream())
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            return Collections.emptyList();
        }
    }

    private List<ToolEntity> parseTools(Resource resource) {
        try (InputStream in = resource.read()) {
            return serializer.deserialize(in, TypeUtils.parameterized(List.class, new Type[] {ToolEntity.class}));
        } catch (IOException exception) {
            return Collections.emptyList();
        }
    }
}