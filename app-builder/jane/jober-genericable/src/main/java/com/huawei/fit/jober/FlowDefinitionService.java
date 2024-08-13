/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.entity.FlowDefinitionResult;
import com.huawei.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 流程定义相关Genericable
 *
 * @author 杨祥宇
 * @since 2023/12/11
 */
public interface FlowDefinitionService {
    /**
     * 创建流程定义
     *
     * @param definitionData 流程定义json字符串
     * @param context 操作人上下文信息
     * @return 流程定义相关信息
     */
    @Genericable(id = "626a0cc105ee4582b7768e1b56587240")
    FlowDefinitionResult createFlowDefinition(String definitionData, OperationContext context);

    /**
     * 更新流程定义
     *
     * @param flowDefinitionId 流程定义唯一Id
     * @param definitionData 流程定义json字符串
     * @param context 操作人上下文信息
     * @return 流程定义相关信息
     */
    @Genericable(id = "e4ecfd1fc8f54d378e8837e8a494a075")
    FlowDefinitionResult updateFlowDefinition(String flowDefinitionId, String definitionData, OperationContext context);

    /**
     * 根据 metaId 和部分 version 获取流程定义
     *
     * @param metaId 表示元数据 id
     * @param version 表示版本
     * @param context 操作人上下文信息
     * @return 流程定义相关信息
     */
    @Genericable(id = "e4ecfd1fc8f54d378e8837e8a494a070")
    List<FlowDefinitionResult> getFlowDefinitionByMetaIdAndPartVersion(String metaId, String version,
            OperationContext context);

    /**
     * 解析流程定义的 graph
     *
     * @param flowViewData 表示流程定义的 graph
     * @param version 表示版本
     * @return 解析后的 graph
     */
    @Genericable(id = "e4ecfd1fc8f54d378e8837e8a494a071")
    String getParsedGraphData(String flowViewData, String version);

    /**
     * 删除流程定义
     *
     * @param flowId 流程定义唯一Id
     * @param context 操作人上下文信息
     */
    @Genericable(id = "34cfd4e429b18382a666e49a1f345ad8")
    void deleteFlows(String flowId, OperationContext context);

    /**
     * 根据metaid和version删除流程定义
     *
     * @param metaId 流程metaId
     * @param version 流程版本
     * @param context 操作人上下文信息
     */
    @Genericable(id = "6512435789d7111dfe44778e2b00f138")
    void deleteFlows(String metaId, String version, OperationContext context);
}
