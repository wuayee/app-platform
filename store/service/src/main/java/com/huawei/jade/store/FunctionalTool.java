/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 表示工具的函数实现。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public interface FunctionalTool extends Tool {
    /**
     * 表示函数工具的类型。
     */
    String TYPE = "function";

    /**
     * 获取函数工具的参数类型列表。
     *
     * @return 表示函数工具的参数类型列表的 {@link List}{@code <}{@link Type}{@code >}。
     */
    List<Type> parameters();

    /**
     * 获取函数工具的参数名字列表。
     *
     * @return 表示函数工具的参数名字列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> parameterNames();

    /**
     * 获取函数工具参数列表中指定名字的参数的位置下标。
     *
     * @param name 表示指定参数名字的 {@link String}。
     * @return 表示指定名字参数的位置下标的 {@code int}。
     */
    int parameterIndex(String name);

    /**
     * 获取函数工具的参数列表中，必须的参数名字列表。
     *
     * @return 表示必须的参数名字列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> requiredParameterNames();

    /**
     * 获取函数工具的返回值类型。
     *
     * @return 表示函数工具的返回值类型的 {@link Type}。
     */
    Type returnType();

    /**
     * 调用函数工具。
     *
     * @param args 表示调用函数工具的参数列表的 {@link Object}{@code []}。
     * @return 表示调用函数工具的结果的 {@link Object}。
     */
    Object call(Object... args);
}
