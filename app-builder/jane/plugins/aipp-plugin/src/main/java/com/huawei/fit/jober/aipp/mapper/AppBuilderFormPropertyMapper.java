/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderFormPropertyPO;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
public interface AppBuilderFormPropertyMapper {
    List<AppBuilderFormPropertyPO> selectWithFormId(String formId);

    AppBuilderFormPropertyPO selectWithId(String id);

    void insertOne(AppBuilderFormPropertyPO insert);

    void insertMore(List<AppBuilderFormPropertyPO> jadeFormProperties);

    void updateOne(AppBuilderFormPropertyPO update);

    int deleteMore(List<String> ids);
}
