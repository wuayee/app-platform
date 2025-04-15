/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 应用构建器提示类别Dto
 *
 * @author 姚江
 * @since 2024-04-26
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderPromptCategoryDto {
    private String title;
    private String id;
    private String parent;
    private Boolean disable;
    private List<AppBuilderPromptCategoryDto> children;
}