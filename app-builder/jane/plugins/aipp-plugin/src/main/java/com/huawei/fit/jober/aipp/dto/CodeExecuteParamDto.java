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

import java.util.Map;

/**
 * 代码节点执行参数详细信息
 *
 * @author 方誉州
 * @since 2024-07-10
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeExecuteParamDto {
    @Property(description = "代码节点引用的参数")
    @JsonProperty("args")
    private Map<String, Object> args;

    @Property(description = "代码节点需执行的用户代码")
    @JsonProperty("code")
    private String code;

    @Property(description = "用户代码编写语言")
    @JsonProperty("language")
    private String language;
}
