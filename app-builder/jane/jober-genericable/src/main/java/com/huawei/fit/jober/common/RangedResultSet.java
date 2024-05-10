/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common;

import com.huawei.fitframework.model.Range;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 批量返回结构体。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-29
 */
public class RangedResultSet<T> {
    private List<T> results;

    private RangeResult range;

    public RangedResultSet() {
    }

    public RangedResultSet(List<T> results, RangeResult range) {
        this.results = results;
        this.range = range;
    }

    /**
     * create
     *
     * @param results results
     * @param offset offset
     * @param limit limit
     * @param total total
     * @return RangedResultSet
     */
    public static <T> RangedResultSet<T> create(List<T> results, long offset, int limit, long total) {
        return create(results, RangeResult.create(offset, limit, total));
    }

    /**
     * create
     *
     * @param results results
     * @param range range
     * @param total total
     * @return RangedResultSet
     */
    public static <T> RangedResultSet<T> create(List<T> results, Range range, long total) {
        return create(results, RangeResult.create(range, total));
    }

    /**
     * create
     *
     * @param results results
     * @param range range
     * @return RangedResultSet
     */
    public static <T> RangedResultSet<T> create(List<T> results, RangeResult range) {
        return new RangedResultSet(results, range);
    }

    public List<T> getResults() {
        return this.results;
    }

    public RangeResult getRange() {
        return this.range;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public void setRange(RangeResult range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return StringUtils.format("[range={0}, results={1}]", this.getRange(), this.getResults());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RangedResultSet<?> that = (RangedResultSet<?>) o;
        return Objects.equals(results, that.results) && Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results, range);
    }
}
