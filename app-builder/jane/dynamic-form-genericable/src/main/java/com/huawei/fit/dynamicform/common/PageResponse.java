/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.dynamicform.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PageResponse 分页返回
 *
 * @author 熊以可
 * @since 2023/10/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private Long total;

    private List<T> items;
}
