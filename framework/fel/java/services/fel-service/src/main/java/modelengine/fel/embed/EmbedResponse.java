/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.embed;

import lombok.Data;

import java.util.List;

/**
 * 表示生成嵌入响应的实体。
 *
 * @author 易文渊
 * @since 2024-04-13
 */
@Data
public class EmbedResponse {
    /**
     * 生成嵌入向量列表。
     */
    private List<List<Float>> embeddings;

    /**
     * 输入字符串耗费token数。
     */
    private Integer inputTokens;
}