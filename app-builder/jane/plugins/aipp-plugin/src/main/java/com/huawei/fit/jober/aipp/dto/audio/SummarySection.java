/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.audio;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SummarySection
 *
 * @author 易文渊
 * @since 2024/1/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummarySection {
    @Property(description = "音频坐标, 格式HH:mm:ss")
    private String position;
    @Property(description = "摘要标题")
    private String title;
    @Property(description = "片段摘要")
    private String text;
}