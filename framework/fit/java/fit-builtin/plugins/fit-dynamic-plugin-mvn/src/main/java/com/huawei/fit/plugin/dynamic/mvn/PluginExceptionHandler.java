/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.plugin.dynamic.mvn;

import com.huawei.fit.http.annotation.ExceptionHandler;
import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 表示动态插件控制器的统一异常处理器。
 *
 * @author 季聿阶
 * @since 2023-09-17
 */
@Component
public class PluginExceptionHandler {
    /**
     * 处理当发生参数异常时的情况。
     *
     * @param cause 表示参数异常的 {@link IllegalArgumentException}。
     * @return 表示处理完毕后的返回值的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException cause) {
        return MapBuilder.<String, Object>get().put("error", cause.getMessage()).build();
    }

    /**
     * 处理当发生未识别异常时的情况。
     *
     * @param cause 表示未识别异常的 {@link Exception}。
     * @return 表示处理完毕后的返回值的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception cause) {
        return MapBuilder.<String, Object>get().put("error", cause.getMessage()).build();
    }
}
