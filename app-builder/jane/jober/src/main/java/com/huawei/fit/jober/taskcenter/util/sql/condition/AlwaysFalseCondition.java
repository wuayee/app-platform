/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;

import java.util.List;

/**
 * 表示恒为假的查询条件。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public class AlwaysFalseCondition implements Condition {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final AlwaysFalseCondition INSTANCE = new AlwaysFalseCondition();

    private AlwaysFalseCondition() {
    }

    @Override
    public void toSql(SqlBuilder sql, List<Object> args) {
        sql.append("1 <> 1");
    }
}
