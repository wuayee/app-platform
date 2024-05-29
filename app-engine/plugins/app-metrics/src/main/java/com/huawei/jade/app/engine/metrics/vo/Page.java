/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Page类消息处理策略
 *
 * @author c00819987
 * @since 2024/05/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {
    private long total;
    private int pageIndex;
    private int pageSize;
    private List<T> data;
}
