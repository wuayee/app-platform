/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import lombok.Data;

/**
 * 表示类目。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-23
 */
@Data
public class CategoryEntity {
    private String id;

    private String name;

    private String group;
}
