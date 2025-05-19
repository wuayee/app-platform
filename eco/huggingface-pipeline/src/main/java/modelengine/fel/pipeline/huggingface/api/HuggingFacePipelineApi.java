/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.pipeline.huggingface.api;

import modelengine.fel.pipeline.huggingface.entity.HealthCheckRequest;
import modelengine.fel.pipeline.huggingface.entity.HuggingFacePipelineRequest;
import modelengine.fel.pipeline.huggingface.entity.PipelineStartUpRequest;

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
     * 调用 Hugging Face 管道执行指定任务。
     *
     * @param url 管道地址。
     * @param request {@link HuggingFacePipelineRequest}
     * @return 管道调用结果响应。
     */
    @POST
    Call<Object> callPipeline(@Url String url, @Body HuggingFacePipelineRequest request);

    /**
     * 初始化 Hugging Face 管道。
     *
     * @param url model-io-manager 地址。
     * @param request {@link PipelineStartUpRequest}
     * @return 管道初始化结果。
     */
    @POST
    Call<Object> startUpPipeline(@Url String url, @Body PipelineStartUpRequest request);

    /**
     * 管道健康检测接口。
     *
     * @param url model-io-manager 地址。
     * @param request {@link HealthCheckRequest}
     * @return 健康检测结果。
     */
    @POST
    Call<Object> pipelineHealthCheck(@Url String url, @Body HealthCheckRequest request);
}
