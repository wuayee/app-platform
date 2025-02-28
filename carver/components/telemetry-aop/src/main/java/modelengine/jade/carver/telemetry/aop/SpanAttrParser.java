/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop;

import modelengine.jade.service.annotations.SpanAttr;

import java.util.Map;

/**
 * {@link SpanAttr} 表达式的解析器。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
public interface SpanAttrParser {
    /**
     * 解析器是否匹配。
     *
     * @param expression 表示注解表达式的 {@link String}。
     * @return 表示匹配结果的 {@code boolean}。
     */
    boolean match(String expression);

    /**
     * 解析属性的键值对。
     *
     * @param expression 表示注解表达式的 {@link String}。
     * @param paramValue 表示被注解的参数值的 {@link Object}。
     * @return 表示属性的键值对的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    Map<String, String> parse(String expression, Object paramValue);
}
