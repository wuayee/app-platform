/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.util;

import com.huawei.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 为 {@link PaginationResult} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-12-12
 */
class DefaultPaginationResult implements PaginationResult {
    private final Pagination pagination;

    private final long total;

    DefaultPaginationResult(Pagination pagination, long total) {
        this.pagination = pagination;
        this.total = total;
    }

    @Override
    public long offset() {
        return this.pagination.offset();
    }

    @Override
    public int limit() {
        return this.pagination.limit();
    }

    @Override
    public long total() {
        return this.total;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultPaginationResult that = (DefaultPaginationResult) obj;
            return this.offset() == that.offset() && this.limit() == that.limit() && this.total() == that.total();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new long[] {this.getClass().hashCode(), this.offset(), this.limit(), this.total()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[offset={0}, limit={1}, total={2}]", this.offset(), this.limit(), this.total());
    }
}
