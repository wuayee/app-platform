/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.model.support;

import com.huawei.fitframework.model.RangeResult;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;

/**
 * 为 {@link RangedResultSet} 提供默认实现。
 *
 * @param <T> 表示结果集中数据的类型。
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2020-07-24
 */
public class DefaultRangedResultSet<T> implements RangedResultSet<T> {
    private final List<T> results;
    private final RangeResult range;

    /**
     * 使用被限定的结果集和限定结果初始化 {@link DefaultRangedResultSet} 类的新实例。
     *
     * @param results 表示被限定的结果集的 {@link List}。
     * @param range 表示限定结果的 {@link RangeResult}。
     */
    public DefaultRangedResultSet(List<T> results, RangeResult range) {
        this.results = results;
        this.range = range;
    }

    @Override
    public List<T> getResults() {
        return this.results;
    }

    @Override
    public RangeResult getRange() {
        return this.range;
    }

    @Override
    public String toString() {
        return StringUtils.format("[range={0}, results={1}]", this.getRange(), this.getResults());
    }
}
