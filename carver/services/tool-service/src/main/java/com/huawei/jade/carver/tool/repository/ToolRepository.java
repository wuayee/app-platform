/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository;

import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.model.query.ToolQuery;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 表示工具的仓库。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
public interface ToolRepository {
    /**
     * 添加工具。
     *
     * @param info 表示待增加的工具信息的 {@link Tool.Info}。
     */
    void addTool(Tool.Info info);

    /**
     * 删除工具。
     *
     * @param uniqueName 表示待删除工具唯一标识的 {@link String}。
     */
    void deleteTool(String uniqueName);

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具详细信息的 {@link Optional}{@code <}{@link Tool}{@code >}。
     */
    Optional<Tool.Info> getTool(String uniqueName);

    /**
     * 根据动态条件准确查询所有的工具信息。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示所有工具详细信息的列表的 {@link List}{@code <}{@link Tool.Info>}{@code >}。
     */
    List<Tool.Info> getTools(ToolQuery toolQuery);

    /**
     * 根据动态条件准确查询所有的工具信息。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示所有工具详细信息的列表的 {@link List}{@code <}{@link Tool.Info>}{@code >}。
     */
    List<Tool.Info> searchTools(ToolQuery toolQuery);

    /**
     * 根据动态条件准确查询所有的工具信息。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示所有工具详细信息的列表的 {@link List}{@code <}{@link Tool.Info>}{@code >}。
     */
    int getToolsCount(ToolQuery toolQuery);

    /**
     * 根据动态条件准确查询所有的工具信息。
     *
     * @param toolQuery 表示动态查询条件的 {@link ToolQuery}。
     * @return 表示所有工具详细信息的列表的 {@link List}{@code <}{@link Tool.Info>}{@code >}。
     */
    int searchToolsCount(ToolQuery toolQuery);

    /**
     * 添加工具标签。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @param tag 表示工具的标签的 {@link String}。
     */
    void addTag(String uniqueName, String tag);

    /**
     * 删除工具标签。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @param tagName 表示工具的标签的 {@link String}。
     */
    void deleteTag(String uniqueName, String tagName);

    /**
     * 根据工具唯一标识删除标签。
     *
     * @param uniqueName 表示商品的唯一标识的 {@link String}。
     */
    void deleteTagByUniqueName(String uniqueName);

    /**
     * 通过工具唯一标识名获取工具的标签列表。
     *
     * @param uniqueName 表示待删除商品信息的 {@link String}。
     * @return 表示商品标签列表的 {@link List}。
     */
    Set<String> getTags(String uniqueName);
}
