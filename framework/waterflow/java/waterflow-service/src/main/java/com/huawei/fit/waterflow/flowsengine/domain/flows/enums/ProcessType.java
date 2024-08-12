/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.enums;

/**
 * 节点事件处理类型
 * 分为前置处理和后置处理，PRE_PROCESS类型为发送人工任务通知，PROCESS类型为节点本身的任务处理
 *
 * @author 高诗意
 * @since 2023/11/16
 */
public enum ProcessType {
    PRE_PROCESS,
    PROCESS
}
