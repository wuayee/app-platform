/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.globalization;

import java.util.Locale;

/**
 * 获取国际化日志的类。
 *
 * @author 鲁为
 * @since 2024-09-04
 */
public interface LocaleService {
    /**
     * 根据错误码获取国际化信息。
     *
     * @param code 表示错误码 {@link String}。
     * @param params 表示用于动态替换消息中的占位符 {@link Object}{@code []}。
     * @return 国际化信息的 {@link String}。
     */
    String localize(String code, Object... params);

    /**
     * 根据错误码获取国际化信息，入参传入系统语言。
     *
     * @param locale 表示系统语言的 {@link Locale}。
     * @param code 表示错误码 {@link String}。
     * @param params 表示用于动态替换消息中的占位符 {@link Object}{@code []}。
     * @return 国际化信息的 {@link String}。
     */
    String localize(Locale locale, String code, Object... params);

    /**
     * 根据错误码获取国际化信息。如果获取失败，返回根据默认码获取的消息。比如调用工具，工具返回一个自定义的code，bundle中没有定义这个。
     *
     * @param code 表示错误码 {@link String}。
     * @param defaultCode 表示默认码的 {@link String}。
     * @param params 表示用于动态替换消息中的占位符 {@link Object}{@code []}。
     * @return 国际化信息的 {@link String}。
     */
    String localizeOrDefault(String code, String defaultCode, Object... params);

    /**
     * 根据错误码获取国际化信息。如果获取失败，返回根据默认码获取的消息，入参传入系统语言。
     *
     * @param locale 表示系统语言的 {@link Locale}。
     * @param code 表示错误码 {@link String}。
     * @param defaultCode 表示默认码的 {@link String}。
     * @param params 表示用于动态替换消息中的占位符 {@link Object}{@code []}。
     * @return 国际化信息的 {@link String}。
     */
    String localizeOrDefault(Locale locale, String code, String defaultCode, Object... params);
}
