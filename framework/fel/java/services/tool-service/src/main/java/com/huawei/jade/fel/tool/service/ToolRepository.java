/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.fel.tool.ToolEntity;

import java.util.List;

/**
 * 提供工具的存储服务。
 *
 * @author 鲁为
 * @since 2024-04-16
 */
public interface ToolRepository {
    /**
     * 添加工具。
     *
     * @param tool 表示待增加的工具信息的 {@link ToolEntity}。
     */
    @Genericable(id = "com.huawei.jade.fel.tool.add")
    void addTool(ToolEntity tool);

    /**
     * 删除工具。
     *
     * @param namespace 表示工具命名空间的 {@link String}。
     * @param toolName 表示待删除工具名称的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.fel.tool.delete")
    void deleteTool(String namespace, String toolName);

    /**
     * 获取工具。
     *
     * @param namespace 表示工具命名空间的 {@link String}。
     * @param toolName 表示工具名称的 {@link String}。
     * @return 表示工具的 {@link ToolEntity}。
     */
    @Genericable(id = "com.huawei.jade.fel.tool.get")
    ToolEntity getTool(String namespace, String toolName);

    /**
     * 获取命名空间下的所有工具。
     *
     * @param namespace 表示工具命名空间的 {@link String}。
     * @return 表示工具的 {@link List}{@code <}{@link ToolEntity}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.fel.tool.list")
    List<ToolEntity> listTool(String namespace);
}
