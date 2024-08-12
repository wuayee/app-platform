/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.runtime.entity.RuntimeData;

import java.util.Optional;

/**
 * 流程运行时服务接口.
 *
 * @author 张越
 * @since 2024-05-25
 */
public interface AippFlowRuntimeInfoService {
    /**
     * 获取流程运行时数据.
     *
     * @param aippId 实例id.
     * @param version 版本号.
     * @param instanceId 实例id.
     * @param context 用户上下文.
     * @return {@link RuntimeData} 运行时数据.
     */
    Optional<RuntimeData> getRuntimeData(String aippId, String version, String instanceId, OperationContext context);
}
