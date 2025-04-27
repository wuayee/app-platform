/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;

import java.util.List;

/**
 * runtimeInfo 相关数据库操作对象
 *
 * @author 张越
 * @since 2024-07-29
 */
public interface AppBuilderRuntimeInfoRepository {
    /**
     * 根据 traceId 获取运行信息.
     *
     * @param traceId {@link String} 用于追踪实例运行情况的唯一标识。
     * @return {@link List}{@code <}{@link AppBuilderRuntimeInfo}{@code >} 运行信息列表.
     */
    List<AppBuilderRuntimeInfo> selectByTraceId(String traceId);

    /**
     * 插入一条数据.
     *
     * @param info {@link AppBuilderRuntimeInfo} 运行时信息.
     */
    void insertOne(AppBuilderRuntimeInfo info);
}
