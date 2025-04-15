/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 创建应用工具DTO
 *
 * @author 邬涨财
 * @since 2024-05-21
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppCreateToolDto {
    private String name;
    private String description;
    private String icon;
    private String greeting;

    @Property(name = "app_type")
    private String appType;
    private String type;
    private String systemPrompt;
}
