/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

import com.huawei.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 为 {@link Table} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-12-08
 */
class DefaultTable extends AbstractNameable implements Table {
    DefaultTable(String name, String alias) {
        super(name, alias);
    }

    @Override
    public void appendTo(SqlBuilder sql) {
        if (StringUtils.isEmpty(this.name())) {
            throw new IllegalStateException("The name of table cannot be blank.");
        }
        sql.appendIdentifier(this.name());
        Optional.ofNullable(this.alias()).map(StringUtils::trim).filter(StringUtils::isNotEmpty)
                .ifPresent(value -> sql.append(" AS ").appendIdentifier(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultTable that = (DefaultTable) obj;
            return Objects.equals(this.name(), that.name()) && Objects.equals(this.alias(), that.alias());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.name(), this.alias()});
    }

    @Override
    public String toString() {
        SqlBuilder sql = SqlBuilder.custom();
        this.appendTo(sql);
        return sql.toString();
    }
}
