/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service.entity;

import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 流程结束后回调所需信息
 *
 * @author 杨祥宇
 * @since 2024/2/28
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowTransCompletionInfo {
    /**
     * 流程定义meta id
     */
    private String flowMetaId;

    /**
     * 流程版本
     */
    private String flowVersion;

    /**
     * 流程transId
     */
    private String flowTransId;

    /**
     * 流程trace id列表
     */
    private List<String> flowTraceIds;

    /**
     * 流程trans的状态
     */
    private FlowTraceStatus status;
}
