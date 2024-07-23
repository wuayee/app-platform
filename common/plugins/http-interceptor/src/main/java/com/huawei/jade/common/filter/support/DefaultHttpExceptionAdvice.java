/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import com.huawei.fit.http.annotation.ExceptionHandler;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Scope;
import com.huawei.fitframework.exception.FitException;
import com.huawei.jade.common.filter.HttpResult;

/**
 * 默认全局异常处理器。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Component
public class DefaultHttpExceptionAdvice {
    /**
     * 处理 {@link FitException} 异常。
     *
     * @param exception 表示异常的 {@link FitException}。
     * @return 表示统一返回结果的 {@link HttpResult}。
     */
    @ExceptionHandler(value = FitException.class, scope = Scope.GLOBAL)
    public HttpResult<Void> handleFitException(FitException exception) {
        return HttpResult.error(exception.getCode(), exception.getMessage());
    }
}