/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.pipeline.huggingface.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Hugging Face 管道请求体。
 *
 * @author 张庭怿
 * @since 2024-06-03
 */
@AllArgsConstructor
@Data
public class HuggingFacePipelineRequest {
    private String task;

    private String model;

    private Map<String, Object> args;
}
