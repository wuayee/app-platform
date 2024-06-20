/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 姚江 yWX1299574
 * @since 2024-04-25
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderPromptDto {
    private List<AppBuilderPromptCategoryDto> categories;
    private List<AppBuilderInspirationDto> inspirations;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppBuilderInspirationDto {
        private String name;
        private String id;
        private String prompt;
        private String promptTemplate;
        private String category;
        private String description;
        private Boolean auto;
        private List<AppBuilderPromptVarDataDto> promptVarData;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppBuilderPromptVarDataDto {
        private String key;
        private String var;
        private String varType;
        private String sourceType;
        private String sourceInfo;
        private Boolean multiple;
    }
}
