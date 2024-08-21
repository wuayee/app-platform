/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface.tts;

import modelengine.fitframework.annotation.Property;
import com.huawei.jade.fel.pipeline.PipelineInput;

import lombok.Data;

import java.util.Map;

/**
 * 表示语音合成任务的输入参数。
 *
 * @author 易文渊
 * @since 2024-06-05
 */
@Data
public class TtsInput implements PipelineInput {
    /**
     * 表示输入文本的 {@link String}。
     */
    @Property(name = "text_inputs")
    private String textInputs;

    /**
     * 表示底层模型推理参数的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Property(name = "forward_params")
    private Map<String, Object> forwardParams;

    /**
     * 表示音频模型推理参数的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Property(name = "generate_kwargs")
    private Map<String, Object> generateKwargs;
}