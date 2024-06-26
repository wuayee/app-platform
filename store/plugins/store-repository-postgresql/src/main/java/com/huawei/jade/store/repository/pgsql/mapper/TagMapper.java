/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.mapper;

import com.huawei.jade.store.repository.pgsql.entity.TagDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Tag 接口。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/11
 */
public interface TagMapper {
    /**
     * 给工具增加标签。
     *
     * @param tag 表示标签名的 {@link String}。
     */
    void addTag(TagDo tag);

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
     * @param uniqueName 表示工具的数据库自增主键的 {@link String}。
     * @return 标签的列表的 {@link List}{@code <}{@link TagDo}{@code >}。
     */
    List<TagDo> getTags(String uniqueName);

    /**
     * 删除工具的所有标签。
     *
     * @param uniqueName 表示工具的唯一表示名 {@link String}。
     */
    void deleteTagByUniqueName(String uniqueName);
}
