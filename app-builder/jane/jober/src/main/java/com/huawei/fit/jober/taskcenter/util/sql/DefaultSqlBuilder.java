/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

/**
 * Todo
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-30
 */
class DefaultSqlBuilder implements SqlBuilder {
    private final StringBuilder builder;

    DefaultSqlBuilder() {
        this.builder = new StringBuilder();
    }

    @Override
    public SqlBuilder appendIdentifier(String identifier) {
        this.builder.append('"').append(identifier).append('"');
        return this;
    }

    @Override
    public SqlBuilder append(String value) {
        this.builder.append(value);
        return this;
    }

    @Override
    public SqlBuilder append(char value) {
        this.builder.append(value);
        return this;
    }

    @Override
    public SqlBuilder backspace(int count) {
        this.builder.setLength(this.builder.length() - count);
        return this;
    }

    @Override
    public SqlBuilder appendRepeatedly(String value, int count) {
        for (int i = 0; i < count; i++) {
            this.builder.append(value);
        }
        return this;
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
