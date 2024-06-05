/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.carver.tool.model.query.ToolTagQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;

import java.util.List;

/**
 * 提供工具的通用服务。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-16
 */
public interface ToolService {
    /**
     * 添加工具。
     *
     * @param tool 表示待增加的工具信息的 {@link ToolData}。
     * @return 表示添加后的工具的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.addTool")
    String addTool(ToolData tool);

    /**
     * 删除工具。
     *
     * @param toolUniqueName 表示待删除工具唯一标识的 {@link String}。
     * @return 表示删除工具的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.deleteTool")
    String deleteTool(String toolUniqueName);

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具详细信息的 {@link ToolData}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.getTool.byUniqueName")
    ToolData getTool(String toolUniqueName);

    /**
     * 根据动态条件准确查询工具列表。
     *
     * @param toolTagQuery 表示动态查询条件的 {@link ToolTagQuery}
     * @return 表示工具列表的 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.getTools.byToolTagQuery")
    List<ToolData> getTools(ToolTagQuery toolTagQuery);

    /**
     * 根据动态条件模糊查询工具列表。
     *
     * @param toolTagQuery 表示动态查询条件的 {@link ToolTagQuery}
     * @return 表示工具列表的 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.searchTools.byToolTagQuery")
    List<ToolData> searchTools(ToolTagQuery toolTagQuery);

    /**
     * 添加工具标签。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param tag 表示待添加的工具标签的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.addTag")
    void addTag(String toolUniqueName, String tag);

    /**
     * 删除工具标签。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param tagName 表示待删除的工具标签的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.deleteTag")
    void deleteTag(String toolUniqueName, String tagName);
}
