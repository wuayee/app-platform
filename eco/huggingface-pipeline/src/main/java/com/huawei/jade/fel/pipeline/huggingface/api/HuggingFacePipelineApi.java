/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface.api;

import com.huawei.jade.fel.pipeline.huggingface.entity.HuggingFacePipelineRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Hugging Face 管道客户端接口。
 *
 * @author 张庭怿
 * @since 2024-06-03
 */
public interface HuggingFacePipelineApi {
    /**
     * 启动 Hugging Face 管道。
     *
     * @param url 管道地址。
     * @param request {@link HuggingFacePipelineRequest}
     * @return 管道调用结果响应。
     */
    @POST
    Call<Object> callPipeline(@Url String url, @Body HuggingFacePipelineRequest request);
}
