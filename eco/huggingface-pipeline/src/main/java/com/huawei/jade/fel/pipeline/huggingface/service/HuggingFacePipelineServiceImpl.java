/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.fel.pipeline.huggingface.api.HuggingFacePipelineApi;
import com.huawei.jade.fel.pipeline.huggingface.entity.HealthCheckRequest;
import com.huawei.jade.fel.pipeline.huggingface.entity.HuggingFacePipelineRequest;
import com.huawei.jade.fel.pipeline.huggingface.entity.PipelineStartUpRequest;
import com.huawei.jade.fel.service.pipeline.HuggingFacePipelineService;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Hugging Face 管道服务实现。
 *
 * @author 张庭怿
 * @since 2024-06-03
 */
@Component
public class HuggingFacePipelineServiceImpl implements HuggingFacePipelineService {
    private static final Logger LOGGER = Logger.get(HuggingFacePipelineServiceImpl.class);

    private static final String CALL_PIPELINE_ENDPOINT = "v1/huggingface/pipeline";

    private static final String START_UP_PIPELINE_ENDPOINT = "/v1/start_up_pipeline";

    private static final String HEALTH_CHECK_ENDPOINT = "/v1/health";

    private static final String DEFAULT_IMAGE_NAME = "model-io-pipeline:latest";

    private static final int DEFAULT_NODE_PORT = 9991;

    private static final int HTTP_CLIENT_TIMEOUT = 120;

    private static final int HTTP_NOT_FOUND = 404;

    private final HuggingFacePipelineApi api;

    private final String gatewayUrl;

    /**
     * 管道服务构造方法，在此方法中初始化客户端。
     *
     * @param gatewayUrl 管道服务端（网关）地址（默认从配置文件中读取）。
     */
    public HuggingFacePipelineServiceImpl(@Value("${gateway-url}") String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(gatewayUrl)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .build();
        this.api = retrofit.create(HuggingFacePipelineApi.class);
    }

    @Override
    @Fitable(id = "com.huawei.jade.fel.pipeline.huggingface.service.call")
    public Object call(String task, String model, Map<String, Object> args) {
        try {
            Response<Object> response = this.api.callPipeline(this.gatewayUrl + CALL_PIPELINE_ENDPOINT,
                    new HuggingFacePipelineRequest(task, model, args)).execute();

            if (response.code() != HTTP_NOT_FOUND) {
                if (!response.isSuccessful()) {
                    throw new IllegalStateException("Failed to call Hugging Face pipeline: " + response);
                }
                return response.body();
            }

            // 路由未匹配成功可能是因为首次请求试pipeline尚未注册到网关，无法匹配对应路由信息，需重试
            response = this.api.startUpPipeline(this.gatewayUrl + START_UP_PIPELINE_ENDPOINT,
                            new PipelineStartUpRequest(model, task, DEFAULT_IMAGE_NAME, DEFAULT_NODE_PORT)).execute();
            LOGGER.info("Can't locate route, try to start this pipeline, result={}", response);
            if (!response.isSuccessful()) {
                return response.body();
            }

            LOGGER.info("Start pipeline health check...");
            int steps = HTTP_CLIENT_TIMEOUT;
            while (steps > 0) {
                response = this.api.pipelineHealthCheck(this.gatewayUrl + HEALTH_CHECK_ENDPOINT,
                        new HealthCheckRequest(model, task)).execute();
                if (response.isSuccessful()) {
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Pipeline health check is interrupted: " + e);
                }
                steps--;
            }

            if (steps == 0) {
                LOGGER.error("Pipeline health check timeout: " + response);
                return response.body();
            }

            LOGGER.info("Health check success, retry task={}", task);
            response = this.api.callPipeline(this.gatewayUrl + CALL_PIPELINE_ENDPOINT,
                    new HuggingFacePipelineRequest(task, model, args)).execute();
            if (!response.isSuccessful()) {
                throw new IllegalStateException("Failed to call Hugging Face pipeline: " + response);
            }

            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to call Hugging Face pipeline: " + e);
        }
    }
}
