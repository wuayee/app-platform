/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;

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
