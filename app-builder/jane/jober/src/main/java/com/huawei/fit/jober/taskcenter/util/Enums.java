/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fitframework.util.StringUtils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 为枚举提供工具方法。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
public final class Enums {
    private static final Map<Class<?>, Map<String, Object>> VALUES = new HashMap<>();

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Enums() {
    }

    private static <T extends Enum<T>> Map<String, Object> getValues(Class<T> enumClass) {
        return VALUES.computeIfAbsent(enumClass, key -> buildValues(enumClass));
    }

    private static <T extends Enum<T>> Map<String, Object> buildValues(Class<T> enumClass) {
        EnumSet<T> values = EnumSet.allOf(enumClass);
        Map<String, Object> results = new HashMap<>(values.size());
        for (T value : values) {
            String key = StringUtils.toUpperCase(value.name());
            results.put(key, value);
        }
        return results;
    }

    /**
     * 获取指定文本在枚举中定义的枚举项。
     * <p>不区分大小写。</p>
     *
     * @param enumClass 表示枚举类型的 {@link Class}。
     * @param value 表示枚举项的文本值的 {@link String}。
     * @param <T> 表示枚举的类型。
     * @return 表示枚举项的 {@link T}。
     */
    public static <T extends Enum<T>> T parse(Class<T> enumClass, String value) {
        Map<String, Object> values = getValues(enumClass);
        String key = StringUtils.toUpperCase(value);
        @SuppressWarnings("unchecked") T result = (T) values.get(key);
        if (result == null) {
            throw new IllegalArgumentException(
                    StringUtils.format("Undefined value of enum. [enumClass={0}, value={1}", enumClass.getName(),
                            value));
        }
        return result;
    }

    /**
     * 解析指定枚举的值。
     *
     * @param enumClass 表示枚举类型的 {@link Class}。
     * @param value 表示枚举值的字符串表现形式的 {@link String}。
     * @param defaultValue 当值未提供时使用的默认值的 {@link T}。（当 {@code value} 为空白字符串时使用的值。）
     * @param <T> 表示枚举的实际类型的 {@link T}。
     * @return 表示解析到的值的 {@link T}。
     * @throws BadRequestException {@code value} 的值不能被解析为枚举类型。
     */
    public static <T extends Enum<T>> T parse(Class<T> enumClass, String value, T defaultValue, ErrorCodes errorCode) {
        String actual = StringUtils.trim(value);
        if (StringUtils.isEmpty(actual)) {
            return defaultValue;
        }
        Map<String, Object> values = getValues(enumClass);
        actual = StringUtils.toUpperCase(actual);
        Object result = values.get(actual);
        if (result == null) {
            throw new BadRequestException(errorCode);
        }
        return cast(result);
    }

    /**
     * 返回一个字符串，用以表示指定的枚举项。
     *
     * @param value 表示待转为字符串表现形式的枚举项的 {@link T}。
     * @param <T> 表示枚举的类型。
     * @return 表示该枚举项的字符串的 {@link String}。
     */
    public static <T extends Enum<T>> String toString(T value) {
        if (value == null) {
            return null;
        } else {
            return value.name();
        }
    }

    /**
     * hasValue
     *
     * @param enumClass enumClass
     * @param value value
     * @return boolean
     */
    public static <T extends Enum<T>> boolean hasValue(Class<T> enumClass, String value) {
        Map<String, Object> values = getValues(enumClass);
        String key = StringUtils.toUpperCase(value);
        return values.containsKey(key);
    }

    /**
     * validate
     *
     * @param enumClass enumClass
     * @param value value
     * @param exceptionSupplier exceptionSupplier
     * @return String
     */
    public static <T extends Enum<T>> String validate(Class<T> enumClass, String value,
            Supplier<RuntimeException> exceptionSupplier) {
        Map<String, Object> values = getValues(enumClass);
        String key = StringUtils.toUpperCase(value);
        @SuppressWarnings("unchecked") T result = (T) values.get(key);
        if (result == null) {
            throw exceptionSupplier.get();
        }
        return toString(result);
    }
}
