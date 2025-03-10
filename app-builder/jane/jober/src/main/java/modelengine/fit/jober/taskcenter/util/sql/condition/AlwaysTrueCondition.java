/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql.condition;

import modelengine.fit.jober.taskcenter.util.sql.Condition;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;

import java.util.List;

/**
 * 表示恒为真的查询条件。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public class AlwaysTrueCondition implements Condition {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final AlwaysTrueCondition INSTANCE = new AlwaysTrueCondition();

    private AlwaysTrueCondition() {
    }

    @Override
    public void toSql(SqlBuilder sql, List<Object> args) {
        sql.append("1 = 1");
    }
}
