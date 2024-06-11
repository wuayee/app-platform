/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.rerank;

import com.huawei.jade.fel.rag.protocol.FlatChunk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 重排序服务相关参数。
 *
 * @since 2024-06-03
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RerankOptions {
    private String query;
    private List<FlatChunk> data;
}
