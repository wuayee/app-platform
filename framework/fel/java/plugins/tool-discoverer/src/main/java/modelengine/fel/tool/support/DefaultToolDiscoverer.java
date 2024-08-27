/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.tool.support;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.tool.ToolEntity;
import modelengine.fel.tool.ToolSchema;
import modelengine.fel.tool.service.ToolRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginCategory;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ArrayUtils;
import modelengine.fitframework.util.TypeUtils;

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