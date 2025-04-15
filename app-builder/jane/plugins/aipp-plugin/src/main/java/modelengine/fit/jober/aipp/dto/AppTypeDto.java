/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 应用业务分类的传输对象。
 *
 * @author songyongtan
 * @since 2025/1/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppTypeDto {
    @Property(description = "应用业务分类唯一标识")
    private String id;

    @Property(description = "名字")
    private String name;
}