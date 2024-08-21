/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import modelengine.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代码节点执行结果详细信息
 *
 * @author 方誉州
 * @since 2024-07-10
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeExecuteResDto {
    @Property(description = "代码节点是否正常执行")
    @JsonProperty("isOk")
    private Boolean isOk;

    @Property(description = "代码节点执行返回值")
    @JsonProperty("value")
    private Object value;

    @Property(description = "代码节点执行的错误信息")
    @JsonProperty("msg")
    private String msg;
}
