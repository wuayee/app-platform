/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.form.exception;

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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 动态表单通用异常处理。 fit框架优先执行 Scope.PLUGIN，其次Scope.GLOBAL
 *
 * @author x00649642
 * @since 2024-02-06
 */
@Component
@RequiredArgsConstructor
public class FormExceptionHandler {
    public static final List<Locale> LOCALES =
            Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"), new Locale("zh", "CN"));
    private static final Logger log = Logger.get(FormExceptionHandler.class);
    private final Plugin plugin;

    /**
     * 所有Throwable的捕获
     *
     * @param exception exception
     * @return {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
    public Rsp<String> handleThrowable(Throwable exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(FormErrCode.UNKNOWN.getErrorCode(), FormErrCode.UNKNOWN.getMessage());
    }

    /**
     * FormParamException的捕获, 对应表单保存场景form_name为空
     *
     * @param e exception
     * @return {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(FormParamException.class)
    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    public Rsp<String> handleFormParamException(FormParamException e) {
        log.error("{} handler", e.getClass().getName(), e);
        return Rsp.err(e.getCode(), getLocaleMessage(e.getCodeString(), e.getMessage(), e.getArgs(), e.getContext()));
    }

    /**
     * FormNotFoundException的捕获
     *
     * @param e exception
     * @return {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(FormNotFoundException.class)
    @ResponseStatus(HttpResponseStatus.NOT_FOUND)
    public Rsp<String> handleFormNotFoundException(FormNotFoundException e) {
        log.error("{} handler", e.getClass().getName(), e);
        return Rsp.err(e.getCode(), getLocaleMessage(e.getCodeString(), e.getMessage(), e.getArgs(), e.getContext()));
    }

    /**
     * 获取国际化异常信息
     */
    private String getLocaleMessage(String code, String defaultMsg, Object[] params, OperationContext context) {
        if (Objects.isNull(context) || StringUtils.isEmpty(context.getLanguage())) {
            return defaultMsg;
        }
        final String language = context.getLanguage();
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
        Locale locale = StringUtils.isEmpty(language) ? Locale.getDefault() : Locale.lookup(list, LOCALES);
        try {
            return plugin.sr().getMessage(locale, code, defaultMsg, params);
        } catch (Exception exception) {
            log.warn("本地化异常消息发生异常: {}, {}", code, params);
            return defaultMsg;
        }
    }
}
