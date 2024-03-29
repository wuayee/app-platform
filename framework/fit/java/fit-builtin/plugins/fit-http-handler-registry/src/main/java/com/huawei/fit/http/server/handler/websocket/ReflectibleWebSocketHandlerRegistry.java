/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.websocket;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fit.http.server.handler.GlobalPathPatternPrefixResolver;
import com.huawei.fit.http.server.handler.PropertyValueMapperResolver;
import com.huawei.fit.http.server.handler.ReflectibleHttpHandlerRegistry;
import com.huawei.fit.http.server.handler.parameter.PathVariableMapperResolver;
import com.huawei.fit.http.server.handler.parameter.RequestBeanMapperResolver;
import com.huawei.fit.http.server.handler.parameter.RequestCookieMapperResolver;
import com.huawei.fit.http.server.handler.parameter.RequestHeaderMapperResolver;
import com.huawei.fit.http.server.handler.parameter.RequestQueryMapperResolver;
import com.huawei.fit.http.server.handler.support.ErrorMapperResolver;
import com.huawei.fit.http.websocket.server.WebSocketBinaryMessageMapperResolver;
import com.huawei.fit.http.websocket.server.WebSocketHandler;
import com.huawei.fit.http.websocket.server.WebSocketSessionMapperResolver;
import com.huawei.fit.http.websocket.server.WebSocketTextMessageMapperResolver;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginStartedObserver;
import com.huawei.fitframework.plugin.PluginStoppingObserver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 {@link WebSocketHandler} 的注册器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-12-09
 */
@Component
public class ReflectibleWebSocketHandlerRegistry implements PluginStartedObserver, PluginStoppingObserver {
    private final HttpClassicServer server;
    private final WebSocketHandlerResolver resolver;

    public ReflectibleWebSocketHandlerRegistry(HttpClassicServer server, WebSocketHandlerResolver resolver) {
        this.server = notNull(server, "The http classic server cannot be null.");
        this.resolver = notNull(resolver, "The websocket handler resolver cannot be null.");
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        List<WebSocketHandler> handlers = this.getWebSocketHandlers(plugin);
        handlers.forEach(handler -> this.server.webSocketDispatcher().register(handler));
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        List<WebSocketHandler> handlers = this.getWebSocketHandlers(plugin);
        handlers.forEach(handler -> this.server.webSocketDispatcher().unregister(handler));
    }

    private List<WebSocketHandler> getWebSocketHandlers(Plugin plugin) {
        BeanContainer container = plugin.container();
        PropertyValueMapperResolver mapperResolver = this.mapperResolver(plugin.runtime().resolverOfAnnotations());
        GlobalPathPatternPrefixResolver pathPatternPrefixResolver =
                ReflectibleHttpHandlerRegistry.getPathPatternPrefixResolver(container);
        return container.factories()
                .stream()
                .map(beanFactory -> this.resolver.resolve(beanFactory, pathPatternPrefixResolver, mapperResolver))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private PropertyValueMapperResolver mapperResolver(AnnotationMetadataResolver annotationResolver) {
        return PropertyValueMapperResolver.combine(new WebSocketSessionMapperResolver(),
                new WebSocketTextMessageMapperResolver(annotationResolver),
                new WebSocketBinaryMessageMapperResolver(annotationResolver),
                new RequestCookieMapperResolver(annotationResolver),
                new PathVariableMapperResolver(annotationResolver),
                new RequestHeaderMapperResolver(annotationResolver),
                new RequestQueryMapperResolver(annotationResolver),
                new RequestBeanMapperResolver(annotationResolver),
                new ErrorMapperResolver());
    }
}
