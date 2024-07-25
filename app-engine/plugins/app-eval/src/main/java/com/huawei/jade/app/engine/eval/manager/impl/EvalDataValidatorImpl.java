/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.eval.manager.EvalDataValidator;

import java.util.List;

/**
 * 表示 {@link EvalDataValidator} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
@Component
public class EvalDataValidatorImpl implements EvalDataValidator {
    @Override
    public void verify(Long datasetId, List<String> contents) {}

    @Override
    public void verify(Long datasetId, String contents) {}
}