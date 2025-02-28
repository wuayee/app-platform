/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.pipeline.huggingface.entity;

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
