/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.query;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 向量查询参数。
 *
 * @since 2024-05-07
 */
@Getter
@Setter
@Builder
public class VectorQuery {
    private List<Float> embedding;
    private int topK = 2;
    private Expression expr;
    private double threadHold;
    private String extraParam;
}
