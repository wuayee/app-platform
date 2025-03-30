/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.jade.waterflow;

import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Genericable;

/**
 * 流程保存及发布相关能力
 *
 * @author 夏斐
 * @since 2023/12/13
 */
public interface FlowsService {
    /**
     * 创建流程定义
     *
     * @param configData 流程定义图json字符串  必填
     * @param context 操作人上下文信息
     * @return 流程信息
     */
    @Genericable(id = "cb8e09f30a50465e8cb3ae59c0b8757d")
    FlowInfo createFlows(String configData, OperationContext context);

    /**
     * 更新一个流程定义
     *
     * @param flowId 流程id 必填
     * @param version 流程版本 必填
     * @param configData 流程定义图json字符串 必填
     * @param context 操作人上下文信息
     * @return 流程信息
     */
    @Genericable(id = "bc8c9799b7b141ae8ec7058d4610be44")
    FlowInfo updateFlows(String flowId, String version, String configData, OperationContext context);

    /**
     * 升级一个流程定义
     *
     * @param flowId 流程id 必填
     * @param newVersion 流程新版本 必填
     * @param configData 流程定义图json字符串 必填
     * @param context 操作人上下文信息
     * @return 流程信息
     */
    @Genericable(id = "da67602ef44942449da1b1c9b2ee9e70")
    FlowInfo upgradeFlows(String flowId, String newVersion, String configData, OperationContext context);

    /**
     * 发布一个流程
     * 流程只有在发布后才能运行，并且返回值包含definitionId和nodes节点信息
     *
     * @param flowId 流程id  必填
     * @param version 流程版本 必填
     * @param configData 流程定义图json字符串
     * @param context 操作人上下文信息
     * @return 流程信息 只有发布时才包含definitionId和nodes节点信息
     */
    @Genericable(id = "e990bf64322c4511807891ba373b1faf")
    FlowInfo publishFlows(String flowId, String version, String configData, OperationContext context);

    /**
     * 发布一个不包含elsa的流程
     * 流程只有在发布后才能运行，并且返回值包含definitionId和nodes节点信息
     *
     * @param flowId 流程id  必填
     * @param version 流程版本 必填
     * @param configData 流程定义图json字符串
     * @param context 操作人上下文信息
     * @return 流程信息 只有发布时才包含definitionId和nodes节点信息
     */
    @Genericable(id = "ca21166e4a47433693d4be6b0f9c7179")
    FlowInfo publishFlowsWithoutElsa(String flowId, String version, String configData, OperationContext context);

    /**
     * 查询一条指定的id和版本的flow
     *
     * @param flowId 流程id 必填
     * @param version 流程版本 必填
     * @param context 操作人上下文信息
     * @return 查询到的flow信息，包含configData
     */
    @Genericable(id = "2a010e3afb2e4702a4bd4caa843a2895")
    FlowInfo getFlows(String flowId, String version, OperationContext context);

    /**
     * 根据definition id查询flow
     *
     * @param definitionId 流程id 必填
     * @param context 操作人上下文信息
     * @return 查询到的flow信息 目前不包含flowgraph数据
     */
    @Genericable(id = "8fd2852a6d07b94a86375fa28abc3fae")
    FlowInfo getFlows(String definitionId, OperationContext context);

    /**
     * 删除一条指定的id和版本的flow
     *
     * @param flowId 流程id 必填
     * @param version 流程版本 必填
     * @param context 操作人上下文信息
     * @return 0 代表删除成功， 其他代表失败
     */
    @Genericable(id = "81dc0ee4860149c884e2347d4ef26e23")
    int deleteFlows(String flowId, String version, OperationContext context);

    /**
     * 删除一条指定的id和版本的flow, 不删除elsa
     *
     * @param flowId 流程id 必填
     * @param version 流程版本 必填
     * @param context 操作人上下文信息
     */
    @Genericable(id = "27269e9e059d4e2da4c120684b04e110")
    void deleteFlowsWithoutElsa(String flowId, String version, OperationContext context);
}