/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.jade.carver.tool.model.transfer.ToolData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 组合 tool 和 default model 的类。
 *
 * @author 李金绪 l00878072
 * @since 2024/6/12
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolModelDto {
    private String creator;
    private String modifier;
    private String name;
    private String description;
    private String uniqueName;
    private Map<String, Object> schema;
    private Map<String, Object> runnables;
    private String source;
    private String icon;
    private Set<String> tags;
    private String defaultModel;

    public static ToolModelDto combine2ToolModelDto(ToolData toolData, String defaultModel) {
        return ToolModelDto.builder()
                .creator(toolData.getCreator())
                .modifier(toolData.getModifier())
                .name(toolData.getName())
                .description(toolData.getDescription())
                .uniqueName(toolData.getUniqueName())
                .schema(toolData.getSchema())
                .runnables(toolData.getRunnables())
                .source(toolData.getSource())
                .icon(toolData.getIcon())
                .tags(toolData.getTags())
                .defaultModel(defaultModel)
                .build();
    }
}
