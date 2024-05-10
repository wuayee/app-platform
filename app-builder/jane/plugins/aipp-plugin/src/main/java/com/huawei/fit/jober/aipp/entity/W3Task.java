/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.entity;

import com.huawei.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * w3待办任务
 *
 * @author l00611472
 * @since 2024-01-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class W3Task {
    @Property(description = "owner")
    private String owner;

    @Property(description = "任务title")
    private String title;

    @Property(description = "任务详情")
    @JsonProperty("task_detail")
    private String taskDetail;
}
