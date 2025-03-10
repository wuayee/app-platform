/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.mapper;

import modelengine.jade.carver.tool.repository.pgsql.model.entity.ToolGroupDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 ToolGroup 接口。
 *
 * @author 李金绪
 * @since 2024-12-09
 */
public interface ToolGroupMapper {
    /**
     * 添加工具组。
     *
     * @param toolGroupDo 表示工具组的 {@link ToolGroupDo}。
     */
    void add(ToolGroupDo toolGroupDo);

    /**
     * 获取工具组。
     *
     * @param definitionGroupName 表示定义组的名称的 {@link String}。
     * @param name 表示工具组的名称的 {@link String}。
     * @return 表示工具组的 {@link ToolGroupDo}。
     */
    ToolGroupDo get(String definitionGroupName, String name);

    /**
     * 获取工具组集合。
     *
     * @param definitionGroupName 表示定义组的名称的 {@link String}。
     * @return 表示工具组集合的 {@link List}{@link <}{@link ToolGroupDo}{@link >}。
     */
    List<ToolGroupDo> getByDefGroupName(String definitionGroupName);

    /**
     * 删除工具组。
     *
     * @param definitionGroupName 表示定义组的名称的 {@link String}。
     * @param name 表示工具组的名称的 {@link String}。
     */
    void delete(String definitionGroupName, String name);

    /**
     * 删除工具组集合。
     *
     * @param definitionGroupName 表示定义组的名称的 {@link String}。
     */
    void deleteByDefGroupName(String definitionGroupName);
}
