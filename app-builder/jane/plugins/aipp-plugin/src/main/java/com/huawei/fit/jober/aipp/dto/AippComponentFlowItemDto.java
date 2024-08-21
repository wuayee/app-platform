/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import modelengine.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

/**
 * 组件流程项DTO
 *
 * @author 夏斐
 * @since 2023/12/22
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippComponentFlowItemDto extends AippComponentItemDto {
    @Property(description = "触发模式")
    @JsonProperty("triggerMode")
    private String triggerMode;

    @Property(description = "标签")
    @JsonProperty("tags")
    private List<String> tags;

    @Property(description = "触发模式")
    @JsonProperty("jober")
    private Map<String, Object> jober;

    /**
     * 构造函数
     *
     * @param type 类型
     * @param name 名称
     * @param icon 图标
     * @param description 描述
     * @param group 分组
     * @param triggerMode 触发模式
     * @param jober Jober对象
     */
    public AippComponentFlowItemDto(String type, String name, String icon, String description, List<String> group,
            String triggerMode, Map<String, Object> jober) {
        super(type, name, icon, description, group);
        this.triggerMode = triggerMode;
        this.jober = jober;
    }
}
