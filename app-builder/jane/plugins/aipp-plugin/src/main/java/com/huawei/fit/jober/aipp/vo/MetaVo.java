/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.vo;

import modelengine.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来向前端展示aippId和version
 *
 * @author 姚江
 * @since 2024-08-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaVo {
    @Property(description = "meta的Id，即aippId")
    @JsonProperty("aipp_id")
    private String id;

    @Property(description = "aipp的版本")
    @JsonProperty("version")
    private String version;
}
