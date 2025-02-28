/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.mapper;

import modelengine.jade.carver.tool.repository.pgsql.model.entity.DefinitionGroupDo;

/**
 * 表示用于 MyBatis 持久层引用的 DefinitionGroup 接口。
 *
 * @author 李金绪
 * @since 2024-12-09
 */
public interface DefinitionGroupMapper {
    /**
     * 添加定义组。
     *
     * @param defGroupDo 表示定义组的 {@link DefinitionGroupDo}。
     */
    void add(DefinitionGroupDo defGroupDo);

    /**
     * 获取定义组。
     *
     * @param name 表示定义组的名称的 {@link String}。
     * @return 表示定义组的 {@link DefinitionGroupDo}。
     */
    DefinitionGroupDo get(String name);

    /**
     * 删除定义组。
     *
     * @param name 表示定义组的名称的 {@link String}。
     */
    void delete(String name);
}
