/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserAccessVO类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccessVo {
    private String createUser;
    private int accessCount;
}
