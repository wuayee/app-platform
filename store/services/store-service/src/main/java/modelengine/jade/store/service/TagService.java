/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.service;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Set;

/**
 * 标签的服务接口类。
 *
 * @author 李金绪
 * @since 2024-09-13
 */
public interface TagService {
    /**
     * 获取标签集合。
     *
     * @param uniqueName 标识工具唯一标识的 {@link String}。
     * @return 表示工具的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "modelengine.jade.store.tag.getTags")
    Set<String> getTags(String uniqueName);

    /**
     * 添加标签。
     *
     * @param tags 标识工具标签的 {@link Set}{@code <}{@link String}{@code >}。
     * @param uniqueName 标识工具唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.store.tag.addTags")
    void addTags(Set<String> tags, String uniqueName);

    /**
     * 根据工具唯一标识删除标签。
     *
     * @param toolUniqueName 表示工具唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.store.tag.deleteTagByUniqueName")
    void deleteTagByUniqueName(String toolUniqueName);

    /**
     * 批量添加标签。
     *
     * @param tagsList 表示标签的集合的列表的 {@link List}{@code <}{@link Set}{@code <}{@link String}{@code >}{@code >}。
     * @param uniqueNameList 表示工具的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "modelengine.jade.store.tag.addTagsList")
    void addTagsList(List<Set<String>> tagsList, List<String> uniqueNameList);

    /**
     * 更新应用标签。
     *
     * @param appTag 表示工具标签的 {@link String}。
     * @param uniqueName 表示工具唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.store.tag.updateAppTag")
    void updateAppTag(String appTag, String uniqueName);
}
