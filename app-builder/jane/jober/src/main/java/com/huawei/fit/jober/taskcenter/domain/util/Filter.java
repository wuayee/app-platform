/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jober.taskcenter.domain.util.filter.AlwaysFalseFilter;
import com.huawei.fit.jober.taskcenter.domain.util.filter.AlwaysTrueFilter;
import com.huawei.fit.jober.taskcenter.domain.util.filter.DefaultFilterParser;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;

/**
 * 为查询任务实例提供过滤器。
 *
 * @author 梁济时
 * @since 2024-01-12
 */
public interface Filter {
    /**
     * 获取一个值，该值指示过滤器是否支持索引。
     *
     * @return 若支持索引，则为 {@code true}，否则为 {@code false}。
     */
    boolean indexable();

    /**
     * 将当前的过滤器转为查询条件。
     *
     * @param column 表示待查询的数据列的引用的 {@link ColumnRef}。
     * @return 表示查询条件的 {@link Condition}。
     */
    Condition toCondition(ColumnRef column);

    /**
     * 获取 {@link Filter} 解析器的默认实例。
     *
     * @return 表示 {@link Filter} 解析器的唯一实例的 {@link FilterParser}。
     */
    static FilterParser parser() {
        return DefaultFilterParser.INSTANCE;
    }

    /**
     * 获取恒为真的过滤器。
     *
     * @return 表示恒为真的过滤器的 {@link Filter}。
     */
    static Filter alwaysTrue() {
        return AlwaysTrueFilter.INSTANCE;
    }

    /**
     * 获取恒为假的过滤器。
     *
     * @return 表示恒为假的过滤器的 {@link Filter}。
     */
    static Filter alwaysFalse() {
        return AlwaysFalseFilter.INSTANCE;
    }
}
