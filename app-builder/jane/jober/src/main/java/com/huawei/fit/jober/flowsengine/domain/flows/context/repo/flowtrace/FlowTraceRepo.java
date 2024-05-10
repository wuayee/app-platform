/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowtrace;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowTrace;

import java.util.List;

/**
 * 流程实例Trace repo核心类
 *
 * @author y00679285
 * @since 2023/8/30
 */
public interface FlowTraceRepo {
    /**
     * 保存flowTrace对象
     *
     * @param flowTrace flowTrace对象实体
     */
    void save(FlowTrace flowTrace);

    /**
     * 根据flowTrace id标识查询flowTrace对象
     *
     * @param traceId flowTrace id标识
     * @return flowTrace flowTrace对象实体
     */
    FlowTrace find(String traceId);

    /**
     * 根据 ids 查找FlowTrace
     *
     * @param ids traceId列表
     * @return List<FlowTrace>
     */
    List<FlowTrace> getByIds(List<String> ids);

    /**
     * 删除对应stream的所有trace对象
     *
     * @param streamId {@link String}
     */
    void delete(String streamId);

    /**
     * 批量更新trace
     *
     * @param flowTraces flowTraces
     */
    void batchUpdate(List<FlowTrace> flowTraces);

    /**
     * 批量保存trace
     *
     * @param flowTraces flowTraces
     */
    void batchCreate(List<FlowTrace> flowTraces);

    /**
     * 批量更新contextPool
     *
     * @param traceList 流程实例trace列表
     * @param contextList context列表
     */
    void updateContextPool(List<String> traceList, List<String> contextList);

    /**
     * 批量查询trace
     *
     * @param traceIds trace id列表
     * @return trace列表
     */
    List<FlowTrace> findTraceByIdList(List<String> traceIds);

    /**
     * updateStatus
     *
     * @param ids ids
     * @param status status
     */
    void updateStatus(List<String> ids, String status);

    /**
     * findRunningTrace
     *
     * @return List<String>
     */
    List<String> findRunningTrace();

    /**
     * 根据traceId删除trace
     *
     * @param traceIds traceId列表
     */
    void deleteByIdList(List<String> traceIds);
}
