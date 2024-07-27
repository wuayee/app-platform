/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.filter;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jober.taskcenter.domain.util.Filter;
import com.huawei.fit.jober.taskcenter.domain.util.FilterParser;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fitframework.util.ParsingResult;
import com.huawei.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 表示判断在由最小值和最大值限定的有效值域内的过滤器。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-12
 */
public class BetweenFilter implements Filter {
    private static final String KEY = "between";

    private final Object minimum;

    private final Object maximum;

    public BetweenFilter(Object minimum, Object maximum) {
        this.minimum = notNull(minimum, "The minimum value of between filter cannot be null.");
        this.maximum = notNull(maximum, "The maximum value of between filter cannot be null.");
    }

    @Override
    public boolean indexable() {
        return true;
    }

    @Override
    public Condition toCondition(ColumnRef column) {
        return Condition.between(column, this.minimum, this.maximum);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            BetweenFilter that = (BetweenFilter) obj;
            return this.minimum.equals(that.minimum) && this.maximum.equals(that.maximum);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.minimum, this.maximum});
    }

    @Override
    public String toString() {
        return KEY + '(' + this.minimum + ", " + this.maximum + ')';
    }

    /**
     * BetweenFilter解析器
     */
    @FilterParser.Declare(KEY)
    public static class Parser implements FilterParser {
        @Override
        public Filter parse(PropertyDataType dataType, String text) {
            String[] parts = StringUtils.split(text, ',');
            if (parts.length != 2) {
                return null;
            }
            ParsingResult<Object> parsedMinimum = dataType.parse(parts[0]);
            if (!parsedMinimum.isParsed()) {
                return Filter.alwaysFalse();
            }
            ParsingResult<Object> parsedMaximum = dataType.parse(parts[1]);
            if (!parsedMaximum.isParsed()) {
                return Filter.alwaysFalse();
            }
            return new BetweenFilter(parsedMinimum.getResult(), parsedMaximum.getResult());
        }
    }
}
