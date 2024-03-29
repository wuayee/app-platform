/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.server.dispatch.support;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.HttpHandler;
import com.huawei.fit.http.server.dispatch.MappingTree;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link MappingTree} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-07-26
 */
public class DefaultMappingTree implements MappingTree {
    /** 表示路径分隔符的 {@link String}。 */
    public static final String PATH_SEPARATOR = "/";

    private static final String WILD_KEY = "*";

    private final Map<String, MappingTreeNode> nodes = new ConcurrentHashMap<>();

    /**
     * 将指定路径按照路径分隔符进行切分。
     *
     * @param path 表示待切分的完整路径的 {@link String}。
     * @return 表示切分后路径的 {@link Deque}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code path} 为 {@code null} 时。
     */
    public static Deque<String> splitPath(String path) {
        notNull(path, "The path cannot be null.");
        return StringUtils.split(path, PATH_SEPARATOR, LinkedList::new, StringUtils::isNotBlank);
    }

    @Override
    public Optional<HttpHandler> register(String pathPattern, HttpHandler handler) {
        notBlank(pathPattern, "The path pattern to register cannot be blank.");
        notNull(handler, "The http handler to register cannot be null.");
        String actualPathPattern = pathPattern.trim();
        isTrue(actualPathPattern.startsWith(PATH_SEPARATOR),
                "The path pattern is not start with '/'. [pathPattern={0}]",
                pathPattern);
        Queue<String> pathFragments = splitPath(actualPathPattern);
        isTrue(CollectionUtils.isNotEmpty(pathFragments),
                "The path pattern cannot only contain '/'. [pathPattern={0}]",
                pathPattern);
        return this.nodes.computeIfAbsent(pathFragments.remove(), fragment -> new MappingTreeNode())
                .register(pathFragments, handler);
    }

    @Override
    public void unregister(String pathPattern) {
        notBlank(pathPattern, "The path pattern to unregister cannot be blank.");
        String actualPathPattern = pathPattern.trim();
        isTrue(actualPathPattern.startsWith(PATH_SEPARATOR),
                "The path pattern is not start with '/'. [pathPattern={0}]",
                pathPattern);
        Queue<String> pathFragments = splitPath(actualPathPattern);
        isTrue(CollectionUtils.isNotEmpty(pathFragments),
                "The path pattern cannot only contain '/'. [pathPattern={0}]",
                pathPattern);
        String removed = pathFragments.remove();
        if (!this.nodes.containsKey(removed)) {
            return;
        }
        MappingTreeNode treeNode = this.nodes.get(removed);
        boolean isEmpty = treeNode.unregister(pathFragments);
        if (isEmpty) {
            this.nodes.remove(removed);
        }
    }

    @Override
    public Optional<HttpHandler> search(String path) {
        notBlank(path, "The search path cannot be blank.");
        String actualPath = path.trim();
        isTrue(actualPath.startsWith(PATH_SEPARATOR), "The search path is not start with '/'. [path={0}]", path);
        Deque<String> pathFragments = splitPath(actualPath);
        isTrue(CollectionUtils.isNotEmpty(pathFragments),
                "The search path cannot only contain '/'. [pathPattern={0}]",
                path);
        MappingTreeNode childNode = this.nodes.get(pathFragments.removeFirst());
        Optional<HttpHandler> httpHandler = Optional.ofNullable(childNode).flatMap(node -> node.search(pathFragments));
        if (httpHandler.isPresent()) {
            return httpHandler;
        }
        MappingTreeNode wildChildNode = this.nodes.get(WILD_KEY);
        return Optional.ofNullable(wildChildNode).flatMap(node -> node.search(pathFragments));
    }

    @Override
    public List<HttpHandler> getAllHttpHandlers() {
        Collection<MappingTreeNode> values = this.nodes.values();
        return values.stream()
                .filter(Objects::nonNull)
                .flatMap(value -> this.getNodeHandlers(value).stream())
                .collect(Collectors.toList());
    }

    private List<HttpHandler> getNodeHandlers(MappingTreeNode node) {
        List<HttpHandler> handlers = new ArrayList<>();
        if (node.handler != null) {
            handlers.add(node.handler);
        }
        if (node.children.size() != 0) {
            List<HttpHandler> childrenHandlers = node.children.values()
                    .stream()
                    .filter(Objects::nonNull)
                    .flatMap(child -> this.getNodeHandlers(child).stream())
                    .collect(Collectors.toList());
            handlers.addAll(childrenHandlers);
        }
        return handlers;
    }

    private static class MappingTreeNode {
        private final Map<String, MappingTreeNode> children = new ConcurrentHashMap<>();
        private HttpHandler handler;

        private Optional<HttpHandler> register(Queue<String> pathFragments, HttpHandler handler) {
            if (pathFragments.isEmpty()) {
                HttpHandler pre = this.handler;
                this.handler = handler;
                return Optional.ofNullable(pre);
            }
            return this.children.computeIfAbsent(pathFragments.remove(), fragment -> new MappingTreeNode())
                    .register(pathFragments, handler);
        }

        private boolean unregister(Queue<String> pathFragments) {
            if (pathFragments.isEmpty()) {
                this.handler = null;
                return this.children.isEmpty();
            }
            String removed = pathFragments.remove();
            if (!this.children.containsKey(removed)) {
                return this.children.isEmpty();
            }
            boolean isEmpty = this.children.get(removed).unregister(pathFragments);
            if (isEmpty) {
                this.children.remove(removed);
            }
            return this.children.isEmpty();
        }

        private Optional<HttpHandler> search(Deque<String> pathFragments) {
            if (pathFragments.isEmpty()) {
                return Optional.ofNullable(this.handler);
            }
            String removed = pathFragments.removeFirst();
            MappingTreeNode child = this.children.get(removed);
            Optional<HttpHandler> httpHandler = Optional.ofNullable(child).flatMap(node -> node.search(pathFragments));
            if (httpHandler.isPresent()) {
                return httpHandler;
            }
            MappingTreeNode wildChild = this.children.get(WILD_KEY);
            httpHandler = Optional.ofNullable(wildChild).flatMap(node -> node.search(pathFragments));
            if (httpHandler.isPresent()) {
                return httpHandler;
            }
            pathFragments.addFirst(removed);
            return Optional.empty();
        }
    }
}
