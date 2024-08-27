/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.community.model.openai.entity.embed;

import modelengine.fel.core.embed.Embedding;

import java.util.List;

/**
 * 表示 OpenAi 格式的嵌入向量。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
public class OpenAiEmbedding implements Embedding {
    private List<Float> embedding;

    @Override
    public List<Float> embedding() {
        return this.embedding;
    }
}
