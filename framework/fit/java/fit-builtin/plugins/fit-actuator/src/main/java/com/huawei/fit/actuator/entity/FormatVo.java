/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.actuator.entity;

import lombok.Data;

/**
 * 表示序列化协议的信息。
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@Data
public class FormatVo {
    private String name;
    private int code;
}
