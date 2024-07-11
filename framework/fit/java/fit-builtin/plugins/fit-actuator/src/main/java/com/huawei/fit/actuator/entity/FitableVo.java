/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.actuator.entity;

import lombok.Data;

import java.util.Set;

/**
 * 表示服务实现信息。
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@Data
public class FitableVo {
    private String id;
    private String version;
    private Set<String> aliases;
    private Set<String> tags;
    private String degradation;
}
