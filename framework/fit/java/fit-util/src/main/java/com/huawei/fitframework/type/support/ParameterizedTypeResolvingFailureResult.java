/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.type.support;

import com.huawei.fitframework.type.ParameterizedTypeResolvingResult;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link ParameterizedTypeResolvingResult} 的失败的解析结果。
 *
 * @author 梁济时 l00815032
 * @since 2020-10-29
 */
public class ParameterizedTypeResolvingFailureResult implements ParameterizedTypeResolvingResult {
    public static final ParameterizedTypeResolvingResult INSTANCE = new ParameterizedTypeResolvingFailureResult();

    private ParameterizedTypeResolvingFailureResult() {}

    @Override
    public boolean resolved() {
        return false;
    }

    @Override
    public List<Type> parameters() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "unresolvable";
    }
}
