/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.domain;

import com.huawei.fit.runtime.entity.Parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * runtimeInfo 领域对象
 *
 * @author 张越
 * @since 2024-07-29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class AppBuilderRuntimeInfo extends BaseDomain {
    private Long id;
    private String traceId;
    private String flowDefinitionId;
    private String instanceId;
    private String nodeId;
    private String nodeType;
    private long startTime;
    private long endTime;
    private String status;
    private boolean published;
    private String errorMsg;
    private List<Parameter> parameters;

    /**
     * 获取节点执行时间.
     *
     * @return {@link Long} 执行时间.
     */
    public long getExecutionCost() {
        return this.endTime - this.startTime;
    }
}
