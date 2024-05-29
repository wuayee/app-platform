/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.dto;

import com.huawei.fitframework.annotation.Property;
import com.huawei.jade.app.engine.base.po.UsrAppInfoAndCollectionPo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 收藏应用消息体
 *
 * @since 2024-5-29
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionAppInfoDto {
    @Property(description = "收藏记录列表")
    List<UsrAppInfoAndCollectionPo> collectionPoList;

    @Property(description = "默认应用")
    UsrAppInfoAndCollectionPo defaultApp;
}
