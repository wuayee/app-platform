/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.retriever;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示检索服务的超参数。
 *
 * @author 刘信宏
 * @since 2024-05-31
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieverOptions {
    private String embeddingModelUrl;
    private String type;
    private Integer topK;
    private Float scoreThreshold;
}
