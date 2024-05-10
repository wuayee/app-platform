/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql.condition;

import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;

/**
 * 为判定指定文本列以指定文本开始提供条件。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-12
 */
public class StartsWithCondition extends AbstractLikeCondition {
    public StartsWithCondition(ColumnRef column, String value) {
        super(column, value);
    }

    @Override
    protected String wrapValue(String value) {
        return value + "%";
    }
}
