/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Hugging Face 管道初始化请求体。
 *
 * @author 张庭怿
 * @since 2024-06-12
 */
@AllArgsConstructor
@Data
public class PipelineStartUpRequest {
    private String name;

    private String task;

    @JsonProperty("image_name")
    private String imageName;

    @JsonProperty("node_port")
    private int nodePort;
}
