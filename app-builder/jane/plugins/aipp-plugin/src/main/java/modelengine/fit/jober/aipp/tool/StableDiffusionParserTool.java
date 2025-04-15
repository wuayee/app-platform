/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.tool;

import modelengine.fit.jober.aipp.dto.image.StableDiffusionInput;
import modelengine.fitframework.annotation.Genericable;

/**
 * StableDiffusion模型json入参解析
 *
 * @author 易文渊
 * @since 2024-06-28
 */
public interface StableDiffusionParserTool {
    /**
     * 解析json字符串。
     *
     * @param input StableDiffusion模型json入参 {@link String}。
     * @return 解析后的SD多模态模型输入 {@link StableDiffusionInput}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.tool.sd.parse")
    StableDiffusionInput parse(String input);
}