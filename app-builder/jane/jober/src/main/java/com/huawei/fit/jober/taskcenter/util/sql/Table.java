/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

/**
 * 表示数据表。
 *
 * @author 梁济时
 * @since 2023-12-08
 */
public interface Table extends SqlElement, Nameable {
    /**
     * 创建数据表的新实例。
     *
     * @param name 表示数据表的名称的 {@link String}。
     * @param alias 表示数据表的别名的 {@link String}。
     * @return 表示新创建的数据表实例的 {@link Table}。
     */
    static Table of(String name, String alias) {
        return new DefaultTable(name, alias);
    }
}
