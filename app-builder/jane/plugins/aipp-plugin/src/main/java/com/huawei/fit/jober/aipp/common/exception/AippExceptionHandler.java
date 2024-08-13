/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common.exception;

import com.huawei.fit.http.annotation.ExceptionHandler;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * aipp通用异常处理。 fit框架优先执行 Scope.PLUGIN，其次Scope.GLOBAL
 *
 * @author 刘信宏
 * @since 2024-01-30
 */
@Component
@RequiredArgsConstructor
public class AippExceptionHandler {
    /**
     * 默认支持语言
     */
    public static final List<Locale> LOCALES = Collections.unmodifiableList(
            Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"), new Locale("zh", "CN")));

    private static final Logger log = Logger.get(AippExceptionHandler.class);

    private final Plugin plugin;

    /**
     * handleThrowable
     *
     * @param exception exception
     * @return Rsp<String>
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleThrowable(Throwable exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(AippErrCode.UNKNOWN.getErrorCode(), AippErrCode.UNKNOWN.getMessage());
    }

    /**
     * AippParamException 处理器。
     *
     * @param exception 异常的 {@link AippParamException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippParamException.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleAippParamException(AippParamException exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()),
                        exception.getMessage(),
                        exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * AippException 处理器。
     *
     * @param exception 异常的 {@link AippException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippException.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleAippException(AippException exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()),
                        exception.getMessage(),
                        exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * AippNotFoundException 处理器。
     *
     * @param exception 异常的 {@link AippNotFoundException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippNotFoundException.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleAippNotFoundException(AippNotFoundException exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()),
                        exception.getMessage(),
                        exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * AippForbiddenException 处理器。
     *
     * @param exception 异常的 {@link AippForbiddenException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippForbiddenException.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleAippForbiddenException(AippForbiddenException exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()),
                        exception.getMessage(),
                        exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * 获取国际化异常信息
     *
     * @param code 异常码
     * @param defaultMsg 默认信息
     * @param params 参数
     * @param context 上下文
     * @return String 本地异常信息
     */
    private String getLocaleMessage(String code, String defaultMsg, Object[] params, OperationContext context) {
        if (context == null || StringUtils.isEmpty(context.getLanguage())) {
            return defaultMsg;
        }
        String language = context.getLanguage();
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
        Locale locale = StringUtils.isNotEmpty(language) ? Locale.lookup(list, LOCALES) : Locale.getDefault();
        try {
            return plugin.sr().getMessage(locale, code, defaultMsg, params);
        } catch (NullPointerException | UnsupportedOperationException | ClassCastException e) {
            log.warn("Localized exception messageException occurred: {}, {}", code, params);
            return defaultMsg;
        }
    }
}
