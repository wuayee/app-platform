/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.po;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aipp用户应用收藏持久化类
 *
 * @since 2024-5-25
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsrAppCollectionPo {
    @Property(description = "collection id")
    private Long id;

    @Property(description = "app id")
    private String appId;

    @Property(description = "usr info")
    private String usrInfo;
}
