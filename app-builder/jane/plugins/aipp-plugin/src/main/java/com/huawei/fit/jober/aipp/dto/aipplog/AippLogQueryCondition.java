/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.aipplog;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * aipp实例历史记录查询条件
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippLogQueryCondition {
    @JsonProperty("aipp_id")
    private String aippId;
    @JsonProperty("instance_id")
    private String instanceId;
    @JsonProperty("after_at")
    private LocalDateTime afterAt;
}
