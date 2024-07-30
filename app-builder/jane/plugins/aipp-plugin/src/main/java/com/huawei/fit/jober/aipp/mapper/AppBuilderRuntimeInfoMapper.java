/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderRuntimeInfoPo;

import java.util.List;

/**
 * 运行时信息相关的数据库操作
 *
 * @author 张越 z00559346
 * @since 2024-07-29
 */
public interface AppBuilderRuntimeInfoMapper {
    /**
     * 根据traceId查询所有的运行时信息.
     *
     * @param traceId 追踪实例运行的唯一标识.
     * @return {@link List}{@code <}{@link AppBuilderRuntimeInfoPo}{@code >} 运行时信息列表.
     */
    List<AppBuilderRuntimeInfoPo> selectByTraceId(String traceId);

    /**
     * 插入一条数据.
     *
     * @param appBuilderRuntimeInfoPO {@link AppBuilderRuntimeInfoPo} 对象.
     */
    void insertOne(AppBuilderRuntimeInfoPo appBuilderRuntimeInfoPO);
}
