/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http.header.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.http.header.ParameterCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 {@link ParameterCollection} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-04
 */
public class DefaultParameterCollection implements ParameterCollection {
    /**
     * 消息头中键值对的分隔符。
     */
    public static final String SEPARATOR = "=";

    private final Map<String, String> parameters = new HashMap<>();

    @Override
    public List<String> keys() {
        return new ArrayList<>(this.parameters.keySet());
    }

    @Override
    public Optional<String> get(String key) {
        notBlank(key, "The parameter key cannot be blank.");
        return Optional.ofNullable(this.parameters.get(key));
    }

    @Override
    public ParameterCollection set(String key, String value) {
        notBlank(key, "The parameter key cannot be blank.");
        this.parameters.put(key, value);
        return this;
    }

    @Override
    public int size() {
        return this.parameters.size();
    }

    @Override
    public String toString() {
        return this.parameters.entrySet()
                .stream()
                .map(entry -> entry.getKey() + SEPARATOR + entry.getValue())
                .collect(Collectors.joining(DefaultHeaderValue.SEPARATOR));
    }
}
