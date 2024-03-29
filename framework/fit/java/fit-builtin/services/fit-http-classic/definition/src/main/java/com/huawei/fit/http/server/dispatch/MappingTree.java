/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.dispatch;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.server.HttpHandler;
import com.huawei.fit.http.server.dispatch.support.DefaultMappingTree;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * 表示 Http 请求路径的匹配树。
 * <p>每一棵匹配树都代表一种特定的 Http 请求方法，比如 {@link HttpRequestMethod#GET}</p>
 *
 * @author 季聿阶 j00559309
 * @since 2022-07-26
 */
public interface MappingTree {
    /**
     * 向当前的匹配树中注册指定匹配路径样式的处理器。
     *
     * @param pathPattern 表示指定的匹配路径样式的 {@link String}。
     * @param handler 表示指定匹配路径样式的处理器的 {@link HttpHandler}。
     * @return 当指定匹配路径样式已经存在处理器时，返回注册过的处理器的
     * {@link Optional}{@code <}{@link HttpHandler}{@code >}，否则，返回 {@link Optional#empty()}。
     * @throws IllegalArgumentException 当 {@code pathPattern} 为 {@code null} 或空白字符串或不以 {@code '/'}
     * 开头或仅包含 {@code '/'} 时。
     * @throws IllegalArgumentException 当 {@code handler} 为 {@code null} 时。
     */
    Optional<HttpHandler> register(String pathPattern, HttpHandler handler);

    /**
     * 向当前的匹配树中取消注册指定匹配路径样式的处理器。
     *
     * @param pathPattern 表示指定的匹配路径样式的 {@link String}。
     */
    void unregister(String pathPattern);

    /**
     * 从当前的匹配树中搜索指定路径的处理器。
     *
     * @param path 表示指定路径的 {@link String}。
     * @return 表示搜索到的指定路径的处理器的 {@link Optional}{@code <}{@link HttpHandler}{@code >}。
     * @throws IllegalArgumentException 当 {@code path} 为 {@code null} 或空白字符串或不以 {@code '/'}
     * 开头或仅包含 {@code '/'} 时。
     */
    Optional<HttpHandler> search(String path);

    /**
     * 获取当前匹配树上所有的处理器。
     *
     * @return 表示处理器的 {@link List}{@code <}{@link HttpHandler}{@code >}。
     */
    List<HttpHandler> getAllHttpHandlers();

    /**
     * 将指定路径按照路径分隔符进行切分。
     *
     * @param path 表示待切分的完整路径的 {@link String}。
     * @return 表示切分后路径的 {@link Deque}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code path} 为 {@code null} 时。
     */
    static Deque<String> splitPath(String path) {
        return DefaultMappingTree.splitPath(path);
    }
}
