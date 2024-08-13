/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 历史记录配置相关的 dto 对象
 *
 * @author 邬涨财
 * @since 2024-05-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryConfigDto {
    private Map<String, Object> initContext;
    private String instanceId;
    private String memory;
}
