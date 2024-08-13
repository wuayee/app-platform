/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存放基础信息类
 *
 * @author 邬涨财
 * @since 2024-05-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreBasicNodeInfoDto {
    private String name;
    private String type;
    private String uniqueName;
}
