/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import com.huawei.fitframework.plugin.Plugin;

import java.util.Map;

/**
 * 表示异常处理器的注册器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-12-01
 */
public interface ExceptionHandlerRegistry {
    /**
     * 添加插件的异常处理器集合。
     *
     * @param plugin 表示需要添加的异常处理器集合的所属插件的 {@link Plugin}。
     * @return 表示添加后的异常处理器的 {@link Map}{@code <}{@link Class}{@code <}{@link Throwable}{@code >, }{@link
     * HttpExceptionHandler}{@code >}。
     */
    Map<Class<Throwable>, HttpExceptionHandler> addExceptionHandlers(Plugin plugin);

    /**
     * 获取全局范围的异常处理器。
     *
     * @return 表示获取到的全局范围的异常处理器的 {@link Map}{@code <}{@link Class}{@code <}{@link Throwable}{@code >, }
     * {@link Map}{@code <}{@link String}{@code , }{@link HttpExceptionHandler}{@code >>}。
     */
    Map<Class<Throwable>, Map<String, HttpExceptionHandler>> getGlobalExceptionHandlers();

    /**
     * 删除插件的异常处理器。
     *
     * @param plugin 需要删除的异常处理器的所属插件的 {@link Plugin}。
     */
    void removeExceptionHandlers(Plugin plugin);
}
