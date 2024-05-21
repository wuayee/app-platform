/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存入数据库的标签的实体类。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDo {
    /**
     * 表示标签的自增主键。
     */
    private Long id;

    /**
     * 表示工具的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 表示标签的名字。
     */
    private String name;

    /**
     * 构造 TagDo。
     *
     * @param uniqueName 表示工具唯一标识名的 {@link String}。
     * @param tagName 表示标签名的 {@link String}。
     */
    public TagDo(String uniqueName, String tagName) {
        this.toolUniqueName = uniqueName;
        this.name = tagName;
    }
}
