/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.filters;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.filters.FlowFilter;
import com.huawei.fit.waterflow.domain.validators.rules.Rules;

/**
 * 流程过滤器校验接口
 *
 * @author g00564732
 * @since 1.0
 */
public interface FilterRule extends Rules {
    /**
     * 校验不同过滤器的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowFilter 流程过滤器
     */
    void apply(FlowFilter flowFilter);
}
