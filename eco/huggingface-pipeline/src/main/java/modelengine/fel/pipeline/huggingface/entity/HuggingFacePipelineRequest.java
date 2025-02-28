/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

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
