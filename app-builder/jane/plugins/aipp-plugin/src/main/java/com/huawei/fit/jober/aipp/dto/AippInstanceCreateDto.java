/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fitframework.annotation.Property;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aipp创建实例响应
 *
 * @author l00611472
 * @since 2024-02-22
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippInstanceCreateDto {
    @Property(description = "Aipp实例ID")
    @JSONField(name = "instance_id")
    private String instanceId;

    @Property(description = "Aipp版本")
    @JSONField(name = "version")
    private String version;

    @Property(description = "Aipp版本ID")
    @JSONField(name = "version_id")
    private String versionId;
}
