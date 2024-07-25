/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.constraint;

import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.validation.ConstraintValidator;

import java.util.List;

/**
 * 表示 {@link ValidListIds} 约束的校验器。
 *
 * @author 何嘉斌
 * @since 2024-07-24
 */
public class ValidListIdsValidator implements ConstraintValidator<ValidListIds, List<Long>> {
    @Override
    public boolean isValid(List<Long> ids) {
        return CollectionUtils.isNotEmpty(ids) && ids.stream().allMatch(id -> id >= 1);
    }
}


