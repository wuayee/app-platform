/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package modelengine.fitframework.parameterization;

import java.util.List;
import java.util.Map;

/**
 * 为参数解析提供结果。
 *
 * @author 梁济时
 * @since 1.0
 */
public interface ParameterizedString {
    /**
     * 获取解析该参数化字符串的解析器。
     *
     * @return 表示参数化字符串解析器的 {@link ParameterizedStringResolver}。
     */
    ParameterizedStringResolver getResolver();

    /**
     * 获取进行变量解析的源字符串。
     *
     * @return 表示源字符串的 {@link String}。
     */
    String getOriginalString();

    /**
     * 获取解析到的参数信息的列表。
     *
     * @return 表示参数信息列表的 {@link List}{@code <}{@link ResolvedParameter}{@code >}。
     */
    List<ResolvedParameter> getParameters();

    /**
     * 使用指定的参数映射格式化字符串。
     *
     * @param args 表示参数映射的 {@link Map}{@code <?, ?>}。
     * @return 表示格式化后的字符串的 {@link String}。
     * @throws StringFormatException 当需要但是未提供指定名称的参数时。
     */
    String format(Map<?, ?> args);

    /**
     * 使用指定的参数映射格式化字符串。
     *
     * @param args 表示参数映射的 {@link Map}{@code <?, ?>}。
     * @param isStrict 表示格式化严格模式的 {@code boolean} ，若为 {@code true} 时，{@code args}
     * 的参数映射数量必须与格式化需要的参数量相等，否则允许参数映射数量大于格式化需要的参数量。
     * @return 表示格式化后的字符串的 {@link String}。
     * @throws StringFormatException 当需要但是未提供指定名称的参数时。
     */
    String format(Map<?, ?> args, boolean isStrict);
}
