/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.localemessage;

/**
 * 国际化消息处理接口。
 * 用于获取国际化信息，支持错误码、默认错误信息和参数的动态替换。
 *
 * @author 张雪彬
 * @since 2024-8-16
 */
public interface LocaleMessageHandler {
    /**
     * 用于获取国际化信息.
     *
     * @param code 表示错误码 {@link String}。
     * @param defaultMsg 表示默认错误信息 {@link String}，当无法找到对应的国际化信息时使用。
     * @param params 表示用于动态替换消息中的占位符 {@link Object}{@code []}。
     * @return 国际化信息，如果没有找到对应的国际化信息，则返回系统默认错误信息。
     */
    String getLocaleMessage(String code, String defaultMsg, Object... params);

    /**
     * 用于获取系统默认的异常信息
     *
     * @return 返回系统默认的错误信息。
     */
    String getDefaultMessage();
}
