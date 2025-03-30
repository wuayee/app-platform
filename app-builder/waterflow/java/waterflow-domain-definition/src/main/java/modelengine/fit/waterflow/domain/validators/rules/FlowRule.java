/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.validators.rules;

import modelengine.fit.jade.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.definitions.FlowDefinition;

/**
 * 流程定义校验规则
 *
 * @author 高诗意
 * @since 1.0
 */
public interface FlowRule extends Rules {
    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    void apply(FlowDefinition flowDefinition);
}
