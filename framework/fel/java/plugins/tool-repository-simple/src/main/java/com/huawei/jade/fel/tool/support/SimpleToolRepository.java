/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.core.tool.ToolInfo;
import com.huawei.jade.fel.tool.ToolEntity;
import com.huawei.jade.fel.tool.service.ToolRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 表示 {@link ToolRepository} 的简单实现。
 *
 * @author 易文渊
 * @since 2024-08-15
 */
@Component
public class SimpleToolRepository implements ToolRepository {
    private static final Logger log = Logger.get(SimpleToolRepository.class);

    private final Map<String, ToolEntity> toolCache = new ConcurrentHashMap<>();

    @Override
    public void addTool(ToolEntity tool) {
        if (tool == null) {
            return;
        }
        String uniqueName = ToolInfo.identify(tool);
        toolCache.put(uniqueName, tool);
        log.info("Register tool[uniqueName={}] success.", uniqueName);
    }

    @Override
    public void deleteTool(String namespace, String toolName) {
        if (StringUtils.isBlank(namespace) || StringUtils.isBlank(toolName)) {
            return;
        }
        String uniqueName = ToolInfo.identify(namespace, toolName);
        toolCache.remove(uniqueName);
        log.info("Unregister tool[uniqueName={}] success.", uniqueName);
    }

    @Override
    public ToolEntity getTool(String namespace, String toolName) {
        notBlank(namespace, "The namespace cannot be blank.");
        notBlank(toolName, "The toll name cannot be blank.");
        String uniqueName = ToolInfo.identify(namespace, toolName);
        return toolCache.get(uniqueName);
    }

    @Override
    public List<ToolEntity> listTool(String namespace) {
        notBlank(namespace, "The namespace cannot be blank.");
        return toolCache.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(namespace))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}