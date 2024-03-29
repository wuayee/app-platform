/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.server.http;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fit.http.server.HttpHandler;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.broker.LocalGenericableRepository;
import com.huawei.fitframework.broker.server.Dispatcher;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.util.StringUtils;

/**
 * FIT 通信方式的处理器的注册器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-14
 */
@Component
public class FitHttpHandlerRegistry {
    private static final String FIT_PATH_PATTERN = "/fit/{genericableId}/{fitableId}";
    private static final String FIT_ASYNC_TASK_PATH_PATTERN = "/fit/async/awaitResponse";

    private final BeanContainer container;
    private final LocalGenericableRepository repository;
    private final String contextPath;

    /**
     * 创建 FIT 通信的处理器的注册器对象。
     *
     * @param httpServer 表示 Http 服务器的 {@link HttpClassicServer}。
     * @param dispatcher 表示 Http 请求转发器的 {@link Dispatcher}。
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param repository 表示本地服务仓的 {@link LocalGenericableRepository}。
     * @param contextPath 表示 Http 请求前缀的 {@link String}。
     */
    public FitHttpHandlerRegistry(HttpClassicServer httpServer, Dispatcher dispatcher, BeanContainer container,
            LocalGenericableRepository repository, @Value("${server.http.context-path}") String contextPath) {
        notNull(httpServer, "The http server cannot be null.");
        notNull(dispatcher, "The receiver cannot be null.");
        this.container = notNull(container, "The bean container cannot be null.");
        this.repository = notNull(repository, "The local genericable repository cannot be null.");
        this.contextPath = StringUtils.isBlank(contextPath) ? StringUtils.EMPTY : contextPath;
        if (StringUtils.isNotBlank(this.contextPath)) {
            HttpHandler handler = this.createHttpHandler(httpServer, dispatcher, this.contextPath + FIT_PATH_PATTERN);
            httpServer.dispatcher().register(HttpRequestMethod.POST.name(), handler);
            HttpHandler asyncTaskHandler =
                    this.createAsyncHttpHandler(httpServer, dispatcher, this.contextPath + FIT_ASYNC_TASK_PATH_PATTERN);
            httpServer.dispatcher().register(HttpRequestMethod.GET.name(), asyncTaskHandler);
        }
        HttpHandler handler = this.createHttpHandler(httpServer, dispatcher, FIT_PATH_PATTERN);
        httpServer.dispatcher().register(HttpRequestMethod.POST.name(), handler);
        HttpHandler asyncTaskHandler = this.createAsyncHttpHandler(httpServer, dispatcher, FIT_ASYNC_TASK_PATH_PATTERN);
        httpServer.dispatcher().register(HttpRequestMethod.GET.name(), asyncTaskHandler);
    }

    private HttpHandler createHttpHandler(HttpClassicServer httpServer, Dispatcher dispatcher, String pathPattern) {
        return new FitHttpHandler(this.container,
                this.repository,
                HttpHandler.StaticInfo.builder().pathPattern(pathPattern).build(),
                HttpHandler.ExecutionInfo.builder().httpServer(httpServer).build(),
                dispatcher);
    }

    private HttpHandler createAsyncHttpHandler(HttpClassicServer httpServer, Dispatcher dispatcher,
            String pathPattern) {
        return new FitHttpAsyncTaskHandler(this.container,
                HttpHandler.StaticInfo.builder().pathPattern(pathPattern).build(),
                HttpHandler.ExecutionInfo.builder().httpServer(httpServer).build());
    }

    String getContextPath() {
        return this.contextPath;
    }
}
