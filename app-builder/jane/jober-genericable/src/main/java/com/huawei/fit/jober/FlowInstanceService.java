/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.entity.FlowInstanceResult;
import com.huawei.fit.jober.entity.FlowStartParameter;
import com.huawei.fit.jober.entity.JoberErrorInfo;

import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 流程1:1运行时流程实例相关Genericable
 *
 * @author 杨祥宇
 * @since 2023/12/11
 */
public interface FlowInstanceService {
    /**
     * 根据流程定义id启动流程实例
     *
     * @param flowDefinitionId 流程定义id
     * @param flowStartParameter 流程启动参数
     * @param context 操作人上下文信息
     * @return 流程实例id标识
     */
    @Genericable(id = "4026db8328a04abe8c2308e993a40499")
    FlowInstanceResult startFlow(String flowDefinitionId, FlowStartParameter flowStartParameter,
            OperationContext context);

    /**
     * 根据流程定义id恢复流程执行
     * 流程1:1运行时使用trace id进行恢复
     * request key为businessData和operator，后续可以补充passData
     *
     * @param flowDefinitionId 流程定义id
     * @param traceId 流程实例trace id
     * @param request 变更的上下文业务数据列集合
     * @param context 操作人上下文信息
     */
    @Genericable(id = "9bcdd0ee456f45838900acb2c39ced2b")
    void resumeFlow(String flowDefinitionId, String traceId, Map<String, Object> request, OperationContext context);

    /**
     * 根据traceId终止流程
     *
     * @param flowDefinitionId 流程定义id
     * @param traceId 流程实例id
     * @param filter 与业务相关的过滤条件，停止满足条件的部分context，目前不支持
     * @param operationContext 操作人上下文信息
     */
    @Genericable(id = "lwwza8xmojxhf0l0wiznpfikvok8pvd0")
    void terminateFlows(String flowDefinitionId, String traceId, Map<String, Object> filter,
            OperationContext operationContext);

    /**
     * 恢复异步任务
     *
     * @param flowDefinitionId 流程定义id
     * @param traceId 流程实例trace id
     * @param newBusinessData 新产生的businessData列表
     * @param operationContext 操作人，为null则默认为前一个操作人
     */
    @Genericable(id = "ac8d6b7590b747dca41bd3aeeb45db59")
    void resumeAsyncJob(String flowDefinitionId, String traceId, Map<String, Object> newBusinessData,
            OperationContext operationContext);

    /**
     * 异步jober异常信息回传
     *
     * @param flowDefinitionId 流程定义id
     * @param traceId 流程实例trace id
     * @param errorInfo 错误信息
     * @param operationContext 操作人，为null则默认为前一个操作人
     */
    @Genericable(id = "40f3acba4f904e8f98d02a0858ebdaf3")
    void failAsyncJob(String flowDefinitionId, String traceId, JoberErrorInfo errorInfo,
            OperationContext operationContext);
}
