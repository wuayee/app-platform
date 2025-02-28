/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql.condition;

import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;

/**
 * 表示检查指定文本列包含指定文本值的条件。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public class ContainsCondition extends AbstractLikeCondition {
    public ContainsCondition(ColumnRef column, String value) {
        super(column, value);
    }

    @Override
    protected String wrapValue(String value) {
        return "%" + value + "%";
    }
}
