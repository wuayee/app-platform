/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 模型信息。
 *
 * @author 张庭怿
 * @since 2024-05-20
 */
@Data
public class ModelInfo {
    private String model;

    private String organization;

    private String type;

    private int health = 1;

    private int requests;

    private int responses;

    private int exceptions;

    private int throughput;

    @JsonProperty(value = "total_input_tokens")
    private int totalInputTokens;

    @JsonProperty(value = "total_output_tokens")
    private int totalOutputTokens;

    private int latency;

    private int speed;
}
