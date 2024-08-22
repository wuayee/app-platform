/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

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
