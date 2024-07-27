/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.filter;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jober.taskcenter.domain.util.Filter;
import com.huawei.fit.jober.taskcenter.domain.util.FilterParser;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fitframework.util.ParsingResult;

import java.util.Arrays;

/**
 * 表示判断不等于指定值的过滤器。
 *
 * @author lWX1291633
 * @since 2024-04-25
 */
public class NotEqualsFilter implements Filter {
    private static final String KEY = "notEq";

    private final Object value;

    public NotEqualsFilter(Object value) {
        this.value = nullIf(value, "The value of not equals filter cannot be null.");
    }

    @Override
    public boolean indexable() {
        return true;
    }

    @Override
    public Condition toCondition(ColumnRef column) {
        return Condition.expectNotEqual(column, this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            NotEqualsFilter that = (NotEqualsFilter) obj;
            return this.value.equals(that.value);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return KEY + '(' + this.value + ')';
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.value});
    }

    /**
     * NotEqualsFilter解析器
     */
    @FilterParser.Declare(KEY)
    public static class Parser implements FilterParser {
        @Override
        public Filter parse(PropertyDataType dataType, String text) {
            ParsingResult<Object> result = dataType.parse(text);
            if (!result.isParsed()) {
                return Filter.alwaysFalse();
            } else {
                return new NotEqualsFilter(result.getResult());
            }
        }
    }
}
