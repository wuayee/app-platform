/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.Pattern;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 委托单元的简单实现。
 *
 * @author 刘信宏
 * @since 2024-06-11
 */
public class SimplePattern<I, O> implements Pattern<I, O> {
    private final BiFunction<I, Map<String, Object>, O> func;
    private final Map<String, Object> args;

    public SimplePattern(BiFunction<I, Map<String, Object>, O> func) {
        this(func, Collections.emptyMap());
    }

    private SimplePattern(BiFunction<I, Map<String, Object>, O> func, Map<String, Object> args) {
        this.func = Validation.notNull(func, "The action function cannot be null.");
        this.args = Validation.notNull(args, "The args cannot be null.");
    }

    @Override
    public O invoke(I input) {
        Validation.notNull(input, "The input data cannot be null.");
        return this.func.apply(input, this.args);
    }

    @Override
    public SimplePattern<I, O> bind(Map<String, Object> args) {
        return new SimplePattern<>(this.func, args);
    }
}
