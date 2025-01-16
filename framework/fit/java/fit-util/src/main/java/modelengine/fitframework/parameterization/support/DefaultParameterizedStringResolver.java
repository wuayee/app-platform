/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.parameterization.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.parameterization.ParameterizedString;
import modelengine.fitframework.parameterization.ParameterizedStringResolver;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link ParameterizedStringResolver} 提供默认实现。
 *
 * @author 梁济时
 * @since 1.0
 */
public class DefaultParameterizedStringResolver implements ParameterizedStringResolver {
    private final String prefix;
    private final String suffix;
    private final char escapeCharacter;
    private final boolean isStrict;

    /**
     * 使用变量的前缀、后缀及转义字符初始化 {@link DefaultParameterizedStringResolver} 类的新实例。
     *
     * @param prefix 表示变量前缀的 {@link String}。
     * @param suffix 表示变量后缀的 {@link String}。
     * @param escapeCharacter 表示转义字符的 {@code char}。
     * @param isStrict 表示是否采用严格校验模式的 {@code boolean}。
     * @throws IllegalArgumentException 当变量前缀为 {@code null} 或空字符串时。
     * @throws IllegalArgumentException 当变量后缀为 {@code null} 或空字符串时。
     * @throws IllegalArgumentException 当变量前缀中包含了转义字符时。
     * @throws IllegalArgumentException 当变量后缀中包含了转义字符时。
     */
    public DefaultParameterizedStringResolver(String prefix, String suffix, char escapeCharacter, boolean isStrict) {
        this.prefix = notBlank(prefix, "The prefix to resolve variables cannot be null or empty.");
        this.suffix = notBlank(suffix, "The suffix to resolve variables cannot be null or empty.");
        validateEscapeCharacter(prefix,
                escapeCharacter,
                "The prefix cannot contains the escape character. [prefix={0}, escape={1}, position={2}]");
        validateEscapeCharacter(suffix,
                escapeCharacter,
                "The suffix cannot contains the escape character. [suffix={0}, escape={1}, position={2}]");
        this.escapeCharacter = escapeCharacter;
        this.isStrict = isStrict;
    }

    /**
     * 检查前缀或后缀中是否包含转义字符。
     *
     * @param xFix 表示待检查的前缀或者后缀的 {@link String}。
     * @param escapeCharacter 表示转义字符。
     * @param errorFormat 表示错误提示信息的格式化字符串的 {@link String}。需要保留3个参数填充：前后缀字符串、转义字符、转义字符所在位置。
     */
    private static void validateEscapeCharacter(String xFix, char escapeCharacter, String errorFormat) {
        int index = xFix.indexOf(escapeCharacter);
        if (index >= 0) {
            throw new IllegalArgumentException(StringUtils.format(errorFormat, xFix, escapeCharacter, index));
        }
    }

    @Override
    public String getParameterPrefix() {
        return this.prefix;
    }

    @Override
    public String getParameterSuffix() {
        return this.suffix;
    }

    @Override
    public char getEscapeCharacter() {
        return this.escapeCharacter;
    }

    @Override
    public ParameterizedString resolve(String originalString) {
        Validation.notNull(originalString, "The string to resolve as a parameterized string cannot be null.");
        return DefaultParameterizedString.resolve(this, originalString, this.isStrict);
    }
}
