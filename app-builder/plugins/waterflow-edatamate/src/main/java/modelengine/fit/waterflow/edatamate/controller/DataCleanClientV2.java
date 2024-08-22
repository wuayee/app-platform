/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.edatamate.controller;

import modelengine.fit.waterflow.edatamate.client.QueryCriteria;
import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 替换原有optional返回值的接口
 *
 * @author 宋永坦
 * @since 2024/3/20
 */
public interface DataCleanClientV2 {
    /**
     * 获取所有的数据清洗流程信息
     *
     * @param queryCriteria 查询条件
     * @param dataCleanTaskId 数据清洗任务id
     * @return 返回所有的数据清洗流程信息
     */
    @Genericable(id = "be67dcba0d684d6fa75b0b56c3d9e70d")
    Map<String, Object> getAllFlowsV2(QueryCriteria queryCriteria, String dataCleanTaskId);

    /**
     * 根据流程id获取流程配置信息
     *
     * @param flowId 流程id
     * @param version 版本信息
     * @param dataCleanTaskId 数据清洗任务id
     * @return 返回流程配置信息
     */
    @Genericable(id = "37059ff2e0e44c0bb89e7b3bc4847cd8")
    Map<String, Object> getFlowConfigByIdV2(String flowId, String version, String dataCleanTaskId);
}
