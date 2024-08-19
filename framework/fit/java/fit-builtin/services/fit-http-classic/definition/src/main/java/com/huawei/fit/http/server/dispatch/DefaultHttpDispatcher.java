/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.dispatch;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.HttpClassicResponse;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpDispatcher;
import com.huawei.fit.http.server.HttpHandler;
import com.huawei.fit.http.server.HttpHandlerGroup;
import com.huawei.fit.http.server.HttpHandlerNotFoundException;
import com.huawei.fit.http.server.RegisterHttpHandlerException;
import com.huawei.fit.http.server.dispatch.support.DefaultMappingTree;
import com.huawei.fitframework.resource.UrlUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.OptionalUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.wildcard.PathPattern;
import com.huawei.fitframework.util.wildcard.Pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 表示 {@link HttpDispatcher} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-07-19
 */
public class DefaultHttpDispatcher implements HttpDispatcher {
    private static final char PATH_SEPARATOR = '/';

    /**
     * 表示路径样式中没有路径变量的处理器集合。
     * <p>其键值对映射分别表示的含义如下：
     *     <ul>
     *         <li>第一层映射中的键表示 Http 请求的方法的 {@link HttpRequestMethod}。</li>
     *         <li>第二层映射中的键表示路径样式，如 {@code /a/b}，当前映射中的路径样式不存在变量，即没有类似
     *         {@code /a/*} 这样的路径样式。</li>
     *         <li>第二层映射中的值为其对应的 Http 请求的处理器的 {@link HttpHandler}。</li>
     *     </ul>
     * </p>
     */
    private final Map<HttpRequestMethod, Map<String, HttpHandler>> noPathVariableHandlers = new ConcurrentHashMap<>();

    /**
     * 表示路径样式中存在路径变量的处理器集合。
     * <p>其键值对映射分别表示的含义如下：
     *     <ul>
     *         <li>映射中的键表示 Http 请求的方法的 {@link HttpRequestMethod}。</li>
     *         <li>映射中的值表示 Http 请求路径的匹配树的 {@link MappingTree}{@code <}{@link HttpHandler}{@code >}。</li>
     *     </ul>
     * </p>
     */
    private final Map<HttpRequestMethod, MappingTree<HttpHandler>> pathVariableHandlers = new ConcurrentHashMap<>();

    /**
     * 表示路径样式中存在 {@code '**'} 通配符的处理器集合。
     * <p>其键值对映射分别表示的含义如下：
     *     <ul>
     *         <li>第一层映射中的键表示 Http 请求的方法的 {@link HttpRequestMethod}。</li>
     *         <li>第二层映射中的键表示路径样式，如 {@code /**}。</li>
     *         <li>第二层映射中的值为其对应的 Http 请求的处理器的 {@link HttpHandler}。</li>
     *     </ul>
     * </p>
     */
    private final Map<HttpRequestMethod, Map<String, HttpHandler>> wildcardHandlers = new ConcurrentHashMap<>();

    private final Map<String, HttpHandlerGroup> groups = new ConcurrentHashMap<>();

    @Override
    public HttpHandler dispatch(HttpClassicServerRequest request, HttpClassicResponse response) {
        return OptionalUtils.get(() -> this.selectFromNoPathVariableHandlers(request))
                .orElse(() -> this.selectFromPathVariableHandlers(request))
                .orElse(() -> this.selectFromWildcardHandlers(request))
                .orElseThrow(() -> {
                    String message = StringUtils.format("No http handler for http request. [method={0}, path={1}]",
                            request.method().name(),
                            request.path());
                    return new HttpHandlerNotFoundException(message);
                });
    }

    private Optional<HttpHandler> selectFromNoPathVariableHandlers(HttpClassicServerRequest request) {
        Map<String, HttpHandler> handlers = this.noPathVariableHandlers.get(request.method());
        if (MapUtils.isEmpty(handlers)) {
            return Optional.empty();
        }
        String path = UrlUtils.decodeValue(request.path());
        HttpHandler handler = handlers.get(path);
        return Optional.ofNullable(handler);
    }

    private Optional<HttpHandler> selectFromPathVariableHandlers(HttpClassicServerRequest request) {
        MappingTree<HttpHandler> mappingTree = this.pathVariableHandlers.get(request.method());
        if (mappingTree == null) {
            return Optional.empty();
        }
        String path = UrlUtils.decodeValue(request.path());
        return mappingTree.search(path);
    }

    private Optional<HttpHandler> selectFromWildcardHandlers(HttpClassicServerRequest request) {
        Map<String, HttpHandler> handlers = this.wildcardHandlers.get(request.method());
        if (MapUtils.isEmpty(handlers)) {
            return Optional.empty();
        }
        String path = UrlUtils.decodeValue(request.path());
        for (Map.Entry<String, HttpHandler> entry : handlers.entrySet()) {
            PathPattern pattern = Pattern.forPath(entry.getKey(), PATH_SEPARATOR);
            if (pattern.matches(path)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public void register(String httpMethod, HttpHandler handler) {
        HttpRequestMethod method =
                notNull(HttpRequestMethod.from(httpMethod), "Not supported http method. [method={0}]", httpMethod);
        notNull(handler, "The http handler cannot be null.");
        String pathPattern = MappingTree.convertToMatchedPathPattern(handler.pathPattern());
        notBlank(pathPattern, "The path pattern cannot be blank.");
        HttpHandler preHandler;
        if (pathPattern.contains("**")) {
            Map<String, HttpHandler> handlers =
                    this.wildcardHandlers.computeIfAbsent(method, methodName -> new ConcurrentHashMap<>());
            preHandler = handlers.put(pathPattern, handler);
        } else if (pathPattern.contains("*")) {
            MappingTree<HttpHandler> mappingTree =
                    this.pathVariableHandlers.computeIfAbsent(method, methodName -> new DefaultMappingTree<>());
            preHandler = mappingTree.register(pathPattern, handler).orElse(null);
        } else {
            Map<String, HttpHandler> handlers =
                    this.noPathVariableHandlers.computeIfAbsent(method, methodName -> new ConcurrentHashMap<>());
            preHandler = handlers.put(pathPattern, handler);
        }
        if (preHandler != null) {
            String message = StringUtils.format("Http handler has been registered. [method={0}, pattern={1}]",
                    httpMethod,
                    pathPattern);
            throw new RegisterHttpHandlerException(message);
        }
    }

    @Override
    public void unregister(String httpMethod, HttpHandler handler) {
        HttpRequestMethod method =
                notNull(HttpRequestMethod.from(httpMethod), "Not supported http method. [method={0}]", httpMethod);
        notNull(handler, "The http handler cannot be null.");
        String pathPattern = MappingTree.convertToMatchedPathPattern(handler.pathPattern());
        notBlank(pathPattern, "The path pattern cannot be blank.");
        if (pathPattern.contains("**")) {
            Optional.ofNullable(this.wildcardHandlers.get(method)).ifPresent(handlers -> handlers.remove(pathPattern));
        } else if (pathPattern.contains("*")) {
            Optional.ofNullable(this.pathVariableHandlers.get(method))
                    .ifPresent(mappingTree -> mappingTree.unregister(pathPattern));
        } else {
            Optional.ofNullable(this.noPathVariableHandlers.get(method))
                    .ifPresent(handlers -> handlers.remove(pathPattern));
        }
    }

    @Override
    public Map<HttpRequestMethod, List<HttpHandler>> getHttpHandlersMapping() {
        Map<HttpRequestMethod, List<HttpHandler>> map = this.noPathVariableHandlers.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue().values())));
        this.pathVariableHandlers.forEach((requestMethod, mappingTree) -> {
            List<HttpHandler> handlerList = map.computeIfAbsent(requestMethod, key -> new ArrayList<>());
            handlerList.addAll(mappingTree.getAllHandlers());
        });
        this.wildcardHandlers.forEach((requestMethod, httpHandlers) -> {
            List<HttpHandler> handlerList = map.computeIfAbsent(requestMethod, key -> new ArrayList<>());
            handlerList.addAll(httpHandlers.values());
        });
        return map;
    }

    @Override
    public void registerGroup(HttpHandlerGroup group) {
        if (group != null) {
            this.groups.put(group.getName(), group);
        }
    }

    @Override
    public void unregisterGroup(String groupName) {
        this.groups.remove(groupName);
    }

    @Override
    public Map<String, HttpHandlerGroup> getHttpHandlerGroups() {
        return Collections.unmodifiableMap(this.groups);
    }
}
