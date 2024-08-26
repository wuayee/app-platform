/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;

import modelengine.fitframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 为 {@link Condition} 提供默认实现。
 *
 * @author 梁济时
 * @since 2024-01-27
 */
public class DefaultCondition implements Condition {
    private final String conditionSql;

    private final Collection<?> conditionArgs;

    public DefaultCondition(String conditionSql, Collection<?> conditionArgs) {
        this.conditionSql = nullIf(conditionSql, StringUtils.EMPTY);
        this.conditionArgs = nullIf(conditionArgs, Collections.emptyList());
    }

    @Override
    public void toSql(SqlBuilder sql, List<Object> args) {
        sql.append(this.conditionSql);
        args.addAll(this.conditionArgs);
    }
}
