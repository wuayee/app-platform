/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存入数据库的收藏关系的实体类。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDo extends CommonDo {
    private String collector;

    private String toolUniqueName;
}
