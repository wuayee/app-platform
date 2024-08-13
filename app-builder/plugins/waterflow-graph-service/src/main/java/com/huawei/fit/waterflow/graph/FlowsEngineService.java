/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.graph;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.jane.flow.graph.entity.FlowSaveEntity;
import com.huawei.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fitframework.model.RangedResultSet;

import java.util.List;

/**
 * 流程bff封装service接口
 * 添加flowsService所有接口, 三个来源：webService、a3000Service、flowsService
 *
 * @author 杨祥宇
 * @since 2023/10/30
 */
public interface FlowsEngineService {
    /**
     * 保存流程定义
     *
     * @param flowSaveEntity 流程入参信息
     * @param context 操作人上下文信息
     * @return 流程返回信息
     */
    FlowInfo createFlows(FlowSaveEntity flowSaveEntity, OperationContext context);

    /**
     * 发布流程定义
     *
     * @param flowSaveEntity 流程入参信息
     * @param context 操作人上下文信息
     * @return 流程返回信息
     */
    FlowInfo publishFlows(FlowSaveEntity flowSaveEntity, OperationContext context);

    /**
     * 只发布流程定义, 不发布到elsa
     *
     * @param flowSaveEntity 流程入参信息
     * @param context 操作人上下文信息
     * @return 流程返回信息
     */
    FlowInfo publishFlowsWithoutElsa(FlowSaveEntity flowSaveEntity, OperationContext context);

    /**
     * 更新一个流程定义
     *
     * @param flowSaveEntity 流程入参信息
     * @param context 操作人上下文信息
     * @return 流程信息
     */
    FlowInfo updateFlows(FlowSaveEntity flowSaveEntity, OperationContext context);

    /**
     * 查询一条指定的id和版本的flow
     *
     * @param flowId 流程id 必填
     * @param version 流程版本 必填
     * @param context 操作人上下文信息
     * @return 查询到的flow信息，包含configData
     */
    FlowInfo getFlows(String flowId, String version, OperationContext context);

    /**
     * 删除一条指定的id和版本的flow
     *
     * @param flowId 流程id 必填
     * @param version 流程版本 必填
     * @param context 操作人上下文信息
     * @return 0 代表删除成功， 其他代表失败
     */
    int deleteFlows(String flowId, String version, OperationContext context);

    /**
     * 删除一条指定的id和版本的flow, 不删除elsa的
     *
     * @param flowId 流程id 必填
     * @param version 流程版本 必填
     * @param context 操作人上下文信息
     */
    void deleteFlowsWithoutElsa(String flowId, String version, OperationContext context);

    /**
     * 强制删除一条指定的id和版本的flow
     *
     * @param flowId 流程id 必填
     * @param version 流程版本 必填
     * @param context 操作人上下文信息
     * @return 0 代表删除成功， 其他代表失败
     */
    int forceDeleteFlows(String flowId, String version, OperationContext context);

    /**
     * 查询流程列表
     *
     * @param createdBy 创建人
     * @param tag 标签列表
     * @param context context
     * @param limit limit
     * @param offset offset
     * @return 流程定义列表
     */
    RangedResultSet<FlowGraphDefinition> findFlowList(String createdBy, List<String> tag, int offset, int limit,
            OperationContext context);

    /**
     * 根据flow_id去获取流程定义版本列表
     *
     * @param flowId 流程定义id
     * @return 版本列表
     */
    List<FlowGraphDefinition> findFlowsByFlowId(String flowId);

    /**
     * 查询流程列表
     *
     * @param data 查询条件
     * @param tenantId 租户id
     * @return 流程定义列表
     */
    GetPageResponse findFlowDefinitionsPage(String data, String tenantId);

    /**
     * 根据流程定义id获取流程定义
     *
     * @param definitionId 流程定义id
     * @param context 操作人上下文信息
     * @return 流程定义信息
     */
    FlowInfo getFlowDefinitionById(String definitionId, OperationContext context);
}