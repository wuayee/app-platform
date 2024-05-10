/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.entity;

import com.huawei.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * aipp实例历史记录json数据
 *
 * @author l00611472
 * @since 2024-01-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippLogData {
    @Property(description = "表单id，log_type为FORM时填充")
    @JsonProperty("form_id")
    private String formId;

    @Property(description = "表单id，log_type为FORM时填充")
    @JsonProperty("form_version")
    private String formVersion;

    @Property(description = "表单参数，log_type为FORM时填充")
    @JsonProperty("form_args")
    private String formArgs;

    @Property(description = "提示信息，log_type为MSG时填充")
    private String msg;
}
