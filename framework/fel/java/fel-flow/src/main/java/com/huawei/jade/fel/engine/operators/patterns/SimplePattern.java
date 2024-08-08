/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.pattern.Pattern;

import java.util.function.Function;

/**
 * 委托单元的简单实现。
 *
 * @author 刘信宏
 * @since 2024-06-11
 */
public class SimplePattern<I, O> implements Pattern<I, O> {
    private final Function<I, O> func;

    public SimplePattern(Function<I, O> func) {
        this.func = Validation.notNull(func, "The action function cannot be null.");
    }

    @Override
    public O invoke(I input) {
        Validation.notNull(input, "The input data cannot be null.");
        return this.func.apply(input);
    }
}
