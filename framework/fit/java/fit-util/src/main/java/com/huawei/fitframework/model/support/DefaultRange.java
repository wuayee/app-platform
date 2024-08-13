/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.model.support;

import com.huawei.fitframework.model.Range;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link Range} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class DefaultRange implements Range {
    private final int offset;
    private final int limit;

    /**
     * 使用偏移量和限定长度初始化 {@link DefaultRange} 类的新实例。
     *
     * @param offset 表示便宜量的32位整数。
     * @param limit 表示限定长度的32位整数。
     */
    public DefaultRange(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public String toString() {
        return StringUtils.format("[offset={0}, limit={1}]", this.getOffset(), this.getLimit());
    }
}
