/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository;

import com.huawei.jade.store.repository.pgsql.entity.TagDo;

import java.util.List;
import java.util.Set;

/**
 * 标签的仓库。
 *
 * @author 鲁为 l00839724
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
}
