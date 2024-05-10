/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

/**
 * 为可命名的对象提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2023-12-11
 */
public class AbstractNameable implements Nameable {
    private final String name;

    private final String alias;

    public AbstractNameable(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String alias() {
        return this.alias;
    }
}
