/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 *
 * This class is used to create a new application.
 * 应用创建Dto
 *
 * @author 姚江
 * @since 2024-04-24
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppCreateDto {
    private String name;
    private String description;
    private String icon;
    private String greeting;

    @Property(name = "app_type")
    private String appType;
    private String type;

    @Property(name = "store_id")
    private String storeId;
    @Property(name = "app_built_type")
    private String appBuiltType;

    @Property(name = "app_category")
    private String appCategory;
}
