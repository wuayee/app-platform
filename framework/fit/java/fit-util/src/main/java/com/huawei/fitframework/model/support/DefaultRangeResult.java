/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.model.support;

import com.huawei.fitframework.model.RangeResult;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link RangeResult} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2020-07-24
 */
public class DefaultRangeResult extends DefaultRange implements RangeResult {
    private final int total;

    /**
     * 使用偏移量、限定长度和结果总数量初始化 {@link DefaultRangeResult} 类的新实例。
     *
     * @param offset 表示便宜量的32位整数。
     * @param limit 表示限定长度的32位整数。
     * @param total 表示结果总数量的32位整数。
     */
    public DefaultRangeResult(int offset, int limit, int total) {
        super(offset, limit);
        this.total = total;
    }

    @Override
    public int getTotal() {
        return this.total;
    }

    @Override
    public String toString() {
        return StringUtils.format("[offset={0}, limit={1}, total={2}]", this.getOffset(), this.getLimit(),
                this.getTotal());
    }
}
