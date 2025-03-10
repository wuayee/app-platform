/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.exception;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.common.exception.AippNotFoundException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 北向接口全局异常处理。
 *
 * @author 曹嘉美
 * @since 2024-12-20
 */
@Component
@RequiredArgsConstructor
public class AippExceptionHandler {
    /**
     * 默认支持语言
     */
    public static final List<Locale> LOCALES = Collections.unmodifiableList(Arrays.asList(new Locale("en"),
            new Locale("zh"),
            new Locale("en", "US"),
            new Locale("zh", "CN")));

    private static final Logger log = Logger.get(AippExceptionHandler.class);

    private final Plugin plugin;

    /**
     * {@link AippParamException} 处理器。
     *
     * @param exception 异常的 {@link AippParamException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippParamException.class)
    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    public Rsp<String> handleAippParamException(AippParamException exception) {
        log.error(exception.getClass().getName(), exception);
        return createErrRsp(exception);
    }

    /**
     * {@link AippNotFoundException} 处理器。
     *
     * @param exception 异常的 {@link AippNotFoundException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippNotFoundException.class)
    @ResponseStatus(HttpResponseStatus.NOT_FOUND)
    public Rsp<String> handleAippNotFoundException(AippNotFoundException exception) {
        log.error(exception.getClass().getName(), exception);
        return createErrRsp(exception);
    }

    /**
     * {@link AippForbiddenException} 处理器。
     *
     * @param exception 异常的 {@link AippForbiddenException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippForbiddenException.class)
    @ResponseStatus(HttpResponseStatus.FORBIDDEN)
    public Rsp<String> handleAippForbiddenException(AippForbiddenException exception) {
        log.error(exception.getClass().getName(), exception);
        return createErrRsp(exception);
    }

    /**
     * {@link AippException} 处理器。
     *
     * @param exception 异常的 {@link AippException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippException.class)
    @ResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
    public Rsp<String> handleAippException(AippException exception) {
        log.error(exception.getClass().getName(), exception);
        return createErrRsp(exception);
    }

    /**
     * {@link Throwable} 处理器。
     *
     * @param exception 异常的 {@link Throwable}。
     * @return Rsp<String> 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
    public Rsp<String> handleThrowable(Throwable exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(AippErrCode.UNKNOWN.getErrorCode(), AippErrCode.UNKNOWN.getMessage());
    }

    private Rsp<String> createErrRsp(AippException exception) {
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()),
                        exception.getMessage(),
                        exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * 获取国际化异常信息。
     *
     * @param code 表示异常码的 {@link String}。
     * @param defaultMsg 表示异常信息的 {@link String}。
     * @param params 表示异常参数的 {@link Object} 数组。
     * @param context 表示异常上下文的 {@link OperationContext}。
     * @return 表示国际化异常信息的 {@link String}。
     */
    private String getLocaleMessage(String code, String defaultMsg, Object[] params, OperationContext context) {
        if (context == null || StringUtils.isBlank(context.getLanguage())) {
            return defaultMsg;
        }
        String language = context.getLanguage();
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
        Locale locale = StringUtils.isNotEmpty(language) ? Locale.lookup(list, LOCALES) : Locale.getDefault();
        try {
            return plugin.sr().getMessageWithDefault(locale, code, defaultMsg, params);
        } catch (Exception e) {
            log.warn("Localized exception messageException occurred: {}, {}", code, params, e);
            return defaultMsg;
        }
    }
}
