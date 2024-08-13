/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

/**
 * 为 {@link SqlValue} 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-10-24
 */
abstract class SqlValues implements SqlValue {
    private final Object value;

    SqlValues(Object value) {
        this.value = value;
    }

    @Override
    public Object get() {
        return this.value;
    }

    static class JsonSqlValue extends SqlValues {
        JsonSqlValue(Object value) {
            super(value);
        }

        @Override
        public String wrapPlaceholder(String placeholder) {
            return placeholder + "::JSON";
        }
    }
}
