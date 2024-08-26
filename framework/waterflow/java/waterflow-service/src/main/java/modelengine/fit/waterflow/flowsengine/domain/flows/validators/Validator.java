/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.validators;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;

/**
 * 流程定义校验接口
 *
 * @author 高诗意
 * @since 2023/08/29
 */
public interface Validator {
    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    void validate(FlowDefinition flowDefinition);

    /**
     * 校验分页参数的合法性
     *
     * @param offset 分页参数：偏移
     * @param limit 分页参数：条数
     */
    void validatePagination(int offset, int limit);
}
