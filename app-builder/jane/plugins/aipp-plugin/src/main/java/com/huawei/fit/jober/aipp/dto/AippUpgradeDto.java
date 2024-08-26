/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.annotation.Property;

/**
 * Aipp升级参数
 *
 * @author 刘信宏
 * @since 2024-01-26
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippUpgradeDto extends AippDto {
    @Property(description = "aipp upgrade version")
    @JsonProperty("upgrade_version")
    private String upgradeVersion;
}
