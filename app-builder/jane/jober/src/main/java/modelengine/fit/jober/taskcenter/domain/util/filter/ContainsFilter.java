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
 * 为指定文本的值包含指定文本提供过滤器。
 *
 * @author 梁济时
 * @since 2024-01-15
 */
public class ContainsFilter implements Filter {
    private final String value;

    public ContainsFilter(String value) {
        this.value = value;
    }

    @Override
    public boolean indexable() {
        return false;
    }

    @Override
    public Condition toCondition(ColumnRef column) {
        return Condition.contains(column, this.value);
    }
}
