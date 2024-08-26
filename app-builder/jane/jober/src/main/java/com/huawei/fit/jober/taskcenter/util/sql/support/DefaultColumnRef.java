/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 为 {@link ColumnRef} 提供默认实现。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public class DefaultColumnRef implements ColumnRef {
    private final String table;

    private final String name;

    public DefaultColumnRef(String table, String name) {
        this.table = nullIf(StringUtils.trim(table), StringUtils.EMPTY);
        this.name = StringUtils.trim(notBlank(name, "The name of a column cannot be blank."));
    }

    @Override
    public String table() {
        return this.table;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultColumnRef that = (DefaultColumnRef) obj;
            return this.table().equals(that.table()) && this.name().equals(that.name());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.table(), this.name()});
    }

    @Override
    public String toString() {
        if (this.table().isEmpty()) {
            return this.name();
        } else {
            return this.table() + '.' + this.name();
        }
    }

    @Override
    public void appendTo(SqlBuilder sql) {
        if (!this.table().isEmpty()) {
            sql.appendIdentifier(this.table()).append('.');
        }
        sql.appendIdentifier(this.name());
    }
}
