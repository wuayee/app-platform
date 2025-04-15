/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto.image;

import lombok.Data;

/**
 * StableDiffusion pipeline模型的输入
 *
 * @author 易文渊
 * @since 2024-06-28
 */
@Data
public class StableDiffusionInput {
    private String prompt;
    private String negativePrompt;
}