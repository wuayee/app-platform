/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * The PageResponse
 *
 * @author Varlamova Natalia
 * @since 2023-10-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    @Property(description = "总量")
    private Long total;

    @Property(description = "标签等拓展含义")
    private String label;

    @Property(description = "列表数据")
    private List<T> items;
}
