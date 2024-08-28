/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.constraint;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.validation.ConstraintValidator;

import java.util.List;

/**
 * 表示 {@link ValidList} 约束的校验器。
 *
 * @author 何嘉斌
 * @since 2024-07-24
 */
public class ValidListValidator implements ConstraintValidator<ValidList, List<Long>> {
    private long min;
    private long max;

    @Override
    public void initialize(ValidList constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }
    @Override
    public boolean isValid(List<Long> ids) {
        return CollectionUtils.isNotEmpty(ids) && ids.stream().allMatch(id -> id >= this.min && id <= this.max);
    }
}


