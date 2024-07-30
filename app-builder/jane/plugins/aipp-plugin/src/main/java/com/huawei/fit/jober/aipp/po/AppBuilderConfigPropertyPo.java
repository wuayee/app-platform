/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AppBuilder配置属性结构体
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderConfigPropertyPo {
    private String id;
    private String nodeId;
    private String formPropertyId;
    private String configId;
}
