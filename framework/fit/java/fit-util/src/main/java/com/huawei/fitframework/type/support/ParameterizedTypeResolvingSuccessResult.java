/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.type.support;

import com.huawei.fitframework.type.ParameterizedTypeResolvingResult;
import com.huawei.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link ParameterizedTypeResolvingResult} 的成功的解析结果。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
public class ParameterizedTypeResolvingSuccessResult implements ParameterizedTypeResolvingResult {
    private final List<Type> parameters;

    public ParameterizedTypeResolvingSuccessResult(List<Type> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean resolved() {
        return true;
    }

    @Override
    public List<Type> parameters() {
        return this.parameters;
    }

    @Override
    public String toString() {
        return this.parameters.stream()
                .map(parameter -> ObjectUtils.mapIfNotNull(parameter, Type::getTypeName))
                .collect(Collectors.joining(", ", "<", ">"));
    }
}
