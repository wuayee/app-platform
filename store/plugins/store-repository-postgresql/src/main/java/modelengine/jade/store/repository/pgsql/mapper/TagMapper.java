/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.mapper;

import modelengine.jade.store.repository.pgsql.entity.TagDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Tag 接口。
 *
 * @author 李金绪
 * @since 2024-05-11
 */
public interface TagMapper {
    /**
     * 给工具增加一个标签。
     *
     * @param tag 表示标签名的 {@link TagDo}。
     */
    void addTag(TagDo tag);

    /**
     * 给工具增加一系列标签。
     *
     * @param tags 表示标签名的 {@link List}{@code <}{@link TagDo}{@code >}。
     */
    void addTags(List<TagDo> tags);

    /**
     * 删除工具的一条标签。
     *
     * @param uniqueName 表示工具的唯一表示名 {@link String}。
     * @param tagName 表示标签名的 {@link String}。
     */
    void deleteTag(String uniqueName, String tagName);

    /**
     * 获取工具的所有标签。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @return 标签的列表的 {@link List}{@code <}{@link TagDo}{@code >}。
     */
    List<TagDo> getTags(String uniqueName);

    /**
     * 删除工具的所有标签。
     *
     * @param uniqueName 表示工具的唯一表示名 {@link String}。
     */
    void deleteTagByUniqueName(String uniqueName);

    /**
     * 更新应用标签。
     *
     * @param appTag 表示工具标签的 {@link String}。
     * @param uniqueName 表示工具唯一标识的 {@link String}。
     */
    void updateAppTag(String appTag, String uniqueName);
}
