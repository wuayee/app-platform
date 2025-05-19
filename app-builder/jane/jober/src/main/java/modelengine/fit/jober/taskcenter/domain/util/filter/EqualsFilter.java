/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util.filter;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.task.domain.PropertyDataType;
import modelengine.fit.jober.taskcenter.domain.util.Filter;
import modelengine.fit.jober.taskcenter.domain.util.FilterParser;
import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;
import modelengine.fit.jober.taskcenter.util.sql.Condition;
import modelengine.fitframework.util.ParsingResult;

import java.util.Arrays;

/**
 * 表示判断等于指定值的过滤器。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public class EqualsFilter implements Filter {
    private static final String KEY = "eq";

    private final Object value;

    public EqualsFilter(Object value) {
        this.value = nullIf(value, "The value of equals filter cannot be null.");
    }

    @Override
    public boolean indexable() {
        return true;
    }

    @Override
    public Condition toCondition(ColumnRef column) {
        return Condition.expectEqual(column, this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            EqualsFilter that = (EqualsFilter) obj;
            return this.value.equals(that.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.value});
    }

    @Override
    public String toString() {
        return KEY + '(' + this.value + ')';
    }

    /**
     * EqualsFilter解析器
     */
    @FilterParser.Declare(KEY)
    public static class Parser implements FilterParser {
        @Override
        public Filter parse(PropertyDataType dataType, String text) {
            ParsingResult<Object> result = dataType.parse(text);
            if (!result.isParsed()) {
                return Filter.alwaysFalse();
            } else {
                return new EqualsFilter(result.getResult());
            }
        }
    }
}
