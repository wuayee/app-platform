/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.mapper;

import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.repository.pgsql.model.entity.ToolDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Tool 接口。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/11
 */
public interface ToolMapper {
    /**
     * 增加工具。
     *
     * @param toolDo 表示待增加的工具信息的 {@link ToolDo}。
     */
    void addTool(ToolDo toolDo);

    /**
     * 删除工具。
     *
     * @param uniqueName 表示工具名的 {@link String}。
     */
    void deleteTool(String uniqueName);

    /**
     * 删除工具的某一个版本。
     *
     * @param uniqueName 表示待删除工具的唯一标识的 {@link String}。
     * @param version 表示待删除工具的版本的 {@link String}。
     */
    void deleteToolByVersion(String uniqueName, String version);

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具信息的 {@link ToolDo}。
     */
    ToolDo getToolByUniqueName(String uniqueName);

    /**
     * 根据动态条件准确获取所有的工具。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 所有工具列表 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    List<ToolDo> getTools(ToolQuery toolQuery);

    /**
     * 根据动态条件模糊获取所有的工具。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 所有工具列表 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    List<ToolDo> searchTools(ToolQuery toolQuery);

    /**
     * 根据动态条件模糊获取工具总数。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示工具总数的 {@code int}。
     */
    int getToolsCount(ToolQuery toolQuery);

    /**
     * 根据动态条件模糊获取所有的工具。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示工具总数的 {@code int}。
     */
    int searchToolsCount(ToolQuery toolQuery);

    /**
     * 将工具的最新版本设置为不是最新。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     */
    void setNotLatest(String toolUniqueName);

    /**
     * 查询工具的某一个版本。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示工具信息的 {@link ToolDo}。
     */
    ToolDo getToolByVersion(String toolUniqueName, String version);

    /**
     * 查询一个工具的所有版本。
     *
     * @param toolQuery toolQuery 表示工具的唯一标识的 {@link ToolQuery}。
     * @return 表示工具版本列表的 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    List<ToolDo> getAllToolVersions(ToolQuery toolQuery);

    /**
     * 查询工具的所有版本的总数。
     *
     * @param toolQuery toolQuery 表示查询条件的 {@link ToolQuery}。
     * @return 表示工具版本总数的 {@code int}。
     */
    int getAllToolVersionsCount(ToolQuery toolQuery);
}
