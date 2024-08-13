/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.model.Range;
import com.huawei.fitframework.util.StringUtils;

/**
 * 批量返回相关统计信息。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class RangeResult {
    private final long offset;

    private final int limit;

    private final long total;

    public RangeResult(long offset, int limit, long total) {
        this.offset = offset;
        this.limit = limit;
        this.total = total;
    }

    static RangeResult create(Range range, long total) {
        Validation.notNull(range, "The range for result cannot be null.");
        return create(range.getOffset(), range.getLimit(), total);
    }

    static RangeResult create(long offset, int limit, long total) {
        return new RangeResult(offset, limit, total);
    }

    public long getOffset() {
        return this.offset;
    }

    public int getLimit() {
        return this.limit;
    }

    public long getTotal() {
        return this.total;
    }

    @Override
    public String toString() {
        return StringUtils.format("[offset={0}, limit={1}, total={2}]", this.getOffset(), this.getLimit(),
                this.getTotal());
    }
}
