/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.callbacks;

import com.huawei.fit.waterflow.flowsengine.biz.service.entity.FlowTransCompletionInfo;

/**
 * 流程事件回调类
 *
 * @author 杨祥宇
 * @since 2024/2/28
 */
public interface FlowEventCallback {
    /**
     * 流程trans状态完成回调函数
     *
     * @param info 回调所需信息
     */
    void onFlowTransCompleted(FlowTransCompletionInfo info);
}
