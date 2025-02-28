/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql.condition;

import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;
import modelengine.fit.jober.taskcenter.util.sql.Condition;

/**
 * 为二元条件提供基类。
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
public abstract class AbstractBinaryCondition implements Condition {
    private final ColumnRef column;

    private final Object value;

    public AbstractBinaryCondition(ColumnRef column, Object value) {
        this.column = column;
        this.value = value;
    }

    protected final ColumnRef column() {
        return this.column;
    }

    protected Object value() {
        return this.value;
    }
}
