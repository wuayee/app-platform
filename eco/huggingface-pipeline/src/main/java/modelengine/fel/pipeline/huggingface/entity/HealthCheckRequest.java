/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.pipeline.huggingface.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 管道健康检查请求体（用于网关匹配路由）。
 *
 * @author 张庭怿
 * @since 2024-06-13
 */
@AllArgsConstructor
@Data
public class HealthCheckRequest {
    private String model;

    private String task;
}
