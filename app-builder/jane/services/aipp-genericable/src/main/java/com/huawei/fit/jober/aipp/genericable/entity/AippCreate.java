/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.genericable.entity;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建Aipp响应体实体类对象
 *
 * @author 邬涨财
 * @since 2024-05-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AippCreate {
    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "aipp version")
    private String version;

    @Property(description = "tool unique name")
    private String toolUniqueName;
}
