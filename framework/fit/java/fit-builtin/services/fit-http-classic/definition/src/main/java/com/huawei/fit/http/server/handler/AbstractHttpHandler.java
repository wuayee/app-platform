/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fit.http.server.HttpHandler;
import com.huawei.fit.http.server.HttpServerFilter;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link HttpHandler} 的抽象父类。
 *
 * @author 季聿阶
 * @since 2022-08-24
 */
public abstract class AbstractHttpHandler implements HttpHandler {
    private final HttpClassicServer httpServer;
    private final String pathPattern;
    private final List<HttpServerFilter> preFilters;

    /**
     * 通过 Http 处理器的相关静态信息来实例化 {@link AbstractHttpHandler}。
     *
     * @param staticInfo 表示 Http 处理器的相关静态信息的 {@link StaticInfo}。
     * @param executionInfo 表示 Http 处理器的相关执行信息的 {@link ExecutionInfo}。
     * @throws IllegalArgumentException 当 {@code staticInfo} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code staticInfo}{@link ExecutionInfo#httpServer() .httpServer()}
     * 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code staticInfo}{@link StaticInfo#pathPattern() .pathPattern()}
     * 为 {@code null} 或空白字符串时。
     */
    protected AbstractHttpHandler(StaticInfo staticInfo, ExecutionInfo executionInfo) {
        notNull(staticInfo, "The http handler static info cannot be null.");
        notNull(executionInfo, "The http handler execution info cannot be null.");
        this.httpServer = notNull(executionInfo.httpServer(), "The http server cannot be null.");
        this.pathPattern = notBlank(staticInfo.pathPattern(), "The path pattern cannot be blank.");
        this.preFilters = getIfNull(executionInfo.preFilters(), Collections::emptyList);
    }

    @Override
    public HttpClassicServer httpResource() {
        return this.httpServer;
    }

    @Override
    public String pathPattern() {
        return this.pathPattern;
    }

    @Override
    public List<HttpServerFilter> preFilters() {
        return Collections.unmodifiableList(this.preFilters);
    }
}
