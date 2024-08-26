/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.validators;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.FlowRule;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 流程定义校验类
 *
 * @author 高诗意
 * @since 2023/08/29
 */
@Component
@RequiredArgsConstructor
public class FlowValidator implements Validator {
    private static final Logger log = Logger.get(FlowValidator.class);

    private final List<FlowRule> rules;

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void validate(FlowDefinition flowDefinition) {
        this.rules.forEach(rule -> rule.apply(flowDefinition));
    }

    /**
     * 校验分页参数的合法性
     *
     * @param offset 分页参数：偏移
     * @param limit 分页参数：条数
     */
    @Override
    public void validatePagination(int offset, int limit) {
        if (offset < 0) {
            log.error("The offset of pagination out of range. Input offset is {}", offset);
            throw new BadRequestException(ErrorCodes.PAGINATION_OFFSET_INVALID);
        }
        if (limit < 0 || limit > 200) {
            log.error("The limit of pagination out of range. Input limit is {}", limit);
            throw new BadRequestException(ErrorCodes.PAGINATION_LIMIT_INVALID);
        }
    }
}
