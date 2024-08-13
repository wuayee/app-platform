/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库表公有字段。
 *
 * @author 鲁为
 * @since 2024-06-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonDo {
    /**
     * 表示数据库表的自增主键。
     */
    private String id;

    /**
     * 表示创建时间。
     */
    private String createdTime;

    /**
     * 表示更新时间。
     */
    private String updatedTime;

    /**
     * 表示创建者。
     */
    private String creator;

    /**
     * 表示修改者。
     */
    private String modifier;
}
