/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.dto;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户收藏应用信息传输类
 *
 * @since 2024-5-25
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsrAppCollectionDto {
    @Property(description = "收藏记录 id")
    private Long id;

    @Property(description = "应用 id")
    private String appId;

    @Property(description = "用户信息")
    private String usrInfo;
}
