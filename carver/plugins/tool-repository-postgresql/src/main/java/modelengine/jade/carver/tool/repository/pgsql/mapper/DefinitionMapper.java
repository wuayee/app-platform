/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.mapper;

import modelengine.jade.carver.tool.repository.pgsql.model.entity.DefinitionDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Definition 接口。
 *
 * @author 王攀博
 * @since 2024-10-29
 */
public interface DefinitionMapper {
    /**
     * 注册工具定义。
     *
     * @param definitionDo 表示待注册的工具定义信息的 {@link DefinitionDo}。
     * @return 表示持久化后返回的定义索引的 {@link Long}。
     */
    Long add(DefinitionDo definitionDo);

    /**
     * 注册工具定义列表。
     *
     * @param definitionDoList 表示待注册的工具定义列表的 {@link List}{@code <}{@link DefinitionDo}{@code >}。
     */
    void addDefinitions(List<DefinitionDo> definitionDoList);

    /**
     * 删除指定的定义。
     *
     * @param groupName 表示定义组名称的 {@link String}。
     * @param name 表示定义名称的 {@link String}。
     */
    void deleteByName(String groupName, String name);

    /**
     * 删除定义组下所有的定义。
     *
     * @param groupName 表示定义组名称的 {@link String}。
     */
    void deleteByGroup(String groupName);

    /**
     * 获取指定定义的详细信息。
     *
     * @param groupName 表示定义组名称的 {@link String}。
     * @param name 表示定义名称的 {@link String}。
     * @return 表示定义详细信息的 {@link DefinitionDo}。
     */
    DefinitionDo getByName(String groupName, String name);

    /**
     * 获取定义组下所有定义的详细信息。
     *
     * @param groupName 表示定义组名称的 {@link String}。
     * @return 表示定义详细信息的 {@link List}{@code <}{@link DefinitionDo}{@code >}。
     */
    List<DefinitionDo> getByGroup(String groupName);
}
