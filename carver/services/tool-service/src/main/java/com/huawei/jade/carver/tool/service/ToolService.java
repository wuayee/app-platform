/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.service;

import modelengine.fitframework.annotation.Genericable;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;

import java.util.List;

/**
 * 提供工具的通用服务。
 *
 * @author 鲁为
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
     * 注册工具列表。
     *
     * @param toolDataList 表示待注册的工具信息的 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.addTools")
    void addTools(List<ToolData> toolDataList);

    /**
     * 删除工具。
     *
     * @param toolUniqueName 表示待删除工具唯一标识的 {@link String}。
     * @return 表示删除工具的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.deleteTool")
    String deleteTool(String toolUniqueName);

    /**
     * 删除工具列表。
     *
     * @param uniqueNames 表示待删除工具唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.deleteTools")
    void deleteTools(List<String> uniqueNames);

    /**
     * 删除工具的某一个版本。
     *
     * @param uniqueName 表示待删除工具的唯一标识的 {@link String}。
     * @param version 表示待删除工具的版本的 {@link String}。
     * @return 表示删除工具的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.deleteToolByVersion")
    String deleteToolByVersion(String uniqueName, String version);

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
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}
     * @return 表示工具列表的 {@link ListResult}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.getTools.byToolQuery")
    ListResult<ToolData> getTools(ToolQuery toolQuery);

    /**
     * 根据动态条件模糊查询工具列表。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}
     * @return 表示工具列表的 {@link ListResult}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.searchTools.byToolQuery")
    ListResult<ToolData> searchTools(ToolQuery toolQuery);

    /**
     * 将工具的最新版本设置为不是最新。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.setNotLatest")
    void setNotLatest(String toolUniqueName);

    /**
     * 将工具的某一个版本置为最新。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param version 表示工具的版本的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.setLatest")
    void setLatest(String toolUniqueName, String version);

    /**
     * 查询工具的某一个版本。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示工具详细信息的 {@link ToolData}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.getToolByVersion")
    ToolData getToolByVersion(String toolUniqueName, String version);

    /**
     * 查询一个工具的所有版本。
     *
     * @param toolQuery 表示查询条件的 {@link ToolQuery}。
     * @return 表示工具版本列表的 {@link ListResult}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.carver.tool.getAllToolVersions")
    ListResult<ToolData> getAllToolVersions(ToolQuery toolQuery);
}
