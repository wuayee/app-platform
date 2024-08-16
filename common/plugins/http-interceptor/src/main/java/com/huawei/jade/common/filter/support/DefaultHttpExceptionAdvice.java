/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import com.huawei.fit.http.annotation.ExceptionHandler;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Scope;
import com.huawei.fitframework.exception.FitException;
import com.huawei.jade.common.filter.HttpResult;
import com.huawei.jade.common.localemessage.LocaleMessageHandler;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * 默认全局异常处理器。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Component
public class DefaultHttpExceptionAdvice {
    private final LocaleMessageHandler localeMessageHandler;

    public DefaultHttpExceptionAdvice(LocaleMessageHandler localeMessageHandler) {
        this.localeMessageHandler = localeMessageHandler;
    }

    /**
     * 处理 {@link Throwable} 异常。
     *
     * @param exception 表示异常的 {@link Throwable}。
     * @return 表示统一返回结果的 {@link HttpResult}{@code <}{@link Void}{@code >}。
     */
    @ExceptionHandler(value = Throwable.class, scope = Scope.GLOBAL)
    public HttpResult<Void> handleException(Throwable exception) {
        if (exception instanceof FitException) {
            return this.handleFitException((FitException) exception);
        }
        return HttpResult.error(HttpResponseStatus.INTERNAL_SERVER_ERROR.statusCode(), exception.getMessage());
    }

    private HttpResult<Void> handleFitException(FitException exception) {
        List<Object> params = Arrays.asList(new TreeMap<>(exception.getProperties()).values().toArray());
        String localeMessage = this.localeMessageHandler.getLocaleMessage(String.valueOf(exception.getCode()),
            exception.getMessage(), params.toArray());
        return HttpResult.error(exception.getCode(), localeMessage);
    }
}