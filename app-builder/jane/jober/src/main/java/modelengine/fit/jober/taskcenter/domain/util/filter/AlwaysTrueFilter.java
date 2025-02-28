/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util.filter;

import modelengine.fit.jober.taskcenter.domain.util.Filter;
import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;
import modelengine.fit.jober.taskcenter.util.sql.Condition;

/**
 * 为 {@link Filter} 提供恒为真的实现。
 *
 * @author 梁济时
 * @since 2024-01-15
 */
public class AlwaysTrueFilter implements Filter {
    /**
     * 表示当前类型的唯一实例。
     */
    public static final AlwaysTrueFilter INSTANCE = new AlwaysTrueFilter();

    private AlwaysTrueFilter() {
    }

    @Override
    public boolean indexable() {
        return true;
    }

    @Override
    public Condition toCondition(ColumnRef column) {
        return Condition.alwaysTrue();
    }
}
