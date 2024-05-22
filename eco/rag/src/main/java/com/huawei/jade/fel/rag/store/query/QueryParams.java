/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.query;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * QueryParam 向量数据库查询参数
 *
 * @author YangPeng
 * @since 2024-05-22
 */
@Setter
@Getter
@Builder
public class QueryParams {
    private List<String> outFields;

    private String expr;

    private long offset = 0L;

    private long limit = 12L;
}
