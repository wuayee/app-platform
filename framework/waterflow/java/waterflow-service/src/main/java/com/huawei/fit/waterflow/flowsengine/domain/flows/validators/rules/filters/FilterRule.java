/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.filters;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.Rules;

/**
 * 流程过滤器校验接口
 *
 * @author g00564732
 * @since 2023/08/29
 */
public interface FilterRule extends Rules {
    /**
     * 校验不同过滤器的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowFilter 流程过滤器
     */
    void apply(FlowFilter flowFilter);
}
