/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql.condition;

import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;

/**
 * 为判定指定文本列以指定文本结束提供条件。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public class EndsWithCondition extends AbstractLikeCondition {
    public EndsWithCondition(ColumnRef column, String value) {
        super(column, value);
    }

    @Override
    protected String wrapValue(String value) {
        return "%" + value;
    }
}
