/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 应用历史记录的返回 Dto 对象。
 *
 * @author 邬涨财 w00575064
 * @since 2024-06-07
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishedAppResDto {
    @Property(description = "应用唯一标识")
    @JsonProperty("appId")
    private String appId;

    @Property(description = "应用版本")
    @JsonProperty("appVersion")
    private String appVersion;

    @Property(description = "发布时间")
    @JsonProperty("publishedAt")
    private LocalDateTime publishedAt;

    @Property(description = "发布人")
    @JsonProperty("publishedBy")
    private String publishedBy;

    // todo 后续需要加上发布描述功能
}
