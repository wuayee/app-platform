/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.query;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Range;

import lombok.Data;

/**
 * 表示分页查询参数的实体。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Data
public class PageQueryParam {
    @Property(description = "页码", example = "1")
    @Range(min = 1, max = Integer.MAX_VALUE, message = "页码从1开始")
    private Integer pageIndex;

    @Property(description = "页面大小", example = "10")
    @Range(min = 1, max = 100, message = "每页尺寸范围[1, 100]")
    private Integer pageSize;

    /**
     * 获取查询偏移量。
     *
     * @return 表示查询偏移量的 {@code int}。
     */
    public int getOffset() {
        return (this.pageIndex - 1) * this.pageSize;
    }

    /**
     * 获取查询限定数量大小。
     *
     * @return 表示查询限定数量大小的 {@code int}。
     */
    public int getLimit() {
        return this.pageSize;
    }
}