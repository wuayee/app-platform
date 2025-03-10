/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.validators.rules.filters;

import modelengine.fit.jade.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.definitions.nodes.filters.FlowFilter;
import modelengine.fit.waterflow.domain.validators.rules.Rules;

/**
 * 流程过滤器校验接口
 *
 * @author 高诗意
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
