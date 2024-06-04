/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.dto;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息传输类
 *
 * @since 2024-5-30
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    @Property(description = "用户信息 id")
    private Long id;

    @Property(description = "用户名")
    private String userName;

    @Property(description = "默认应用")
    private String defaultApp;
}
