/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.controller;

import com.huawei.fit.waterflow.edatamate.client.QueryCriteria;
import com.huawei.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 替换原有optional返回值的接口
 *
 * @author s00558940
 * @since 2024/3/20
 */
public interface DataCleanClientV2 {
    @Genericable(
            id = "be67dcba0d684d6fa75b0b56c3d9e70d"
    )
    Map<String, Object> getAllFlowsV2(QueryCriteria queryCriteria, String dataCleanTaskId);

    @Genericable(
            id = "37059ff2e0e44c0bb89e7b3bc4847cd8"
    )
    Map<String, Object> getFlowConfigByIdV2(String flowId, String version, String dataCleanTaskId);
}
