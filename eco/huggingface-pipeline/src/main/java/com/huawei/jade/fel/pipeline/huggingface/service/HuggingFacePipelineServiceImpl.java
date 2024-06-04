/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.jade.fel.pipeline.huggingface.api.HuggingFacePipelineApi;
import com.huawei.jade.fel.pipeline.huggingface.entity.HuggingFacePipelineRequest;
import com.huawei.jade.fel.service.pipeline.HuggingFacePipelineService;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
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
    private static final String PIPELINE_ENDPOINT = "/v1/huggingface/pipeline";

    private static final int HTTP_CLIENT_TIMEOUT = 120;

    private final HuggingFacePipelineApi api;

    private final String baseUrl;

    /**
     * 管道服务构造方法，在此方法中初始化客户端。
     *
     * @param baseUrl 管道服务端地址（默认从配置文件中读取）。
     */
    public HuggingFacePipelineServiceImpl(@Value("${huggingface-pipeline-base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .build();
        this.api = retrofit.create(HuggingFacePipelineApi.class);
    }

    @Override
    @Fitable(id = "com.huawei.jade.fel.pipeline.huggingface.service.call")
    public Object call(String task, String model, Map<String, Object> args) {
        Object response;
        try {
            response = this.api.callPipeline(baseUrl + PIPELINE_ENDPOINT,
                    new HuggingFacePipelineRequest(task, model, args)).execute().body();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to call Hugging Face pipeline: " + e);
        }
        return response;
    }
}
