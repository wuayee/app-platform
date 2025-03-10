/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.repository;

import modelengine.jade.store.repository.pgsql.entity.TagDo;

import java.util.List;
import java.util.Set;

/**
 * 标签的仓库。
 *
 * @author 鲁为
 * @since 2024-07-20
 */
public interface TagRepository {
    /**
     * 添加标签。
     *
     * @param tags 表示标签的集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     */
    void addTags(Set<String> tags, String uniqueName);

    /**
     * 获取标签。
     *
     * @param uniqueName 表示工具唯一标识的 {@link String}。
     * @return 标签信息列表的 {@link List}{@code <}{@link TagDo}{@code >}。
     */
    List<TagDo> getTags(String uniqueName);

    /**
     * 根据工具唯一标识删除标签。
     *
     * @param toolUniqueName 表示工具唯一标识的 {@link String}。
     */
    void deleteTagByUniqueName(String toolUniqueName);

    /**
     * 批量添加标签。
     *
     * @param tagsList 表示标签的集合的列表的 {@link List}{@code <}{@link Set}{@code <}{@link String}{@code >}{@code >}。
     * @param uniqueNameList 表示工具的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void addTagsList(List<Set<String>> tagsList, List<String> uniqueNameList);

    /**
     * 更新应用标签。
     *
     * @param appTag 表示工具标签的 {@link String}。
     * @param uniqueName 表示工具唯一标识的 {@link String}。
     */
    void updateAppTag(String appTag, String uniqueName);
}
