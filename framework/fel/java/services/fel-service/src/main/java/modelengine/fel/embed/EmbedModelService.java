/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.embed;

import modelengine.fitframework.annotation.Genericable;

/**
 * 表示嵌入模型服务。
 *
 * @author 易文渊
 * @since 2024-04-13
 */
public interface EmbedModelService {
    /**
     * 调用嵌入模型一次性生成结果。
     *
     * @param request 表示嵌入请求的 {@link EmbedRequest}。
     * @return 表示模型生成嵌入响应的 {@link EmbedResponse}。
     */
    @Genericable(id = "com.huawei.jade.fel.embed.generate")
    EmbedResponse generate(EmbedRequest request);
}