/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.pipeline.huggingface.text2img;

import modelengine.fel.pipeline.PipelineInput;
import modelengine.fitframework.annotation.Property;

import lombok.Data;

/**
 * 表示文生图任务的输入参数。
 *
 * @author 易文渊
 * @since 2024-06-06
 */
@Data
public class Text2ImageInput implements PipelineInput {
    private String prompt;

    @Property(name = "negative_prompt")
    private String negativePrompt;

    private Integer height;

    private Integer width;

    @Property(name = "num_images_per_prompt")
    private Integer numImagesPerPrompt;

    @Property(name = "num_inference_steps")
    private Integer numInferenceSteps;
}