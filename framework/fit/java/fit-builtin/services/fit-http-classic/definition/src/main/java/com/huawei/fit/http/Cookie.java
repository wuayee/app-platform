/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http;

import modelengine.fitframework.pattern.builder.BuilderFactory;

/**
 * 表示 Http 中的 Cookie。
 *
 * @author 季聿阶
 * @since 2022-07-06
 */
public interface Cookie {
    /**
     * 获取 Cookie 的名字。
     *
     * @return 表示 Cookie 名字的 {@link String}。
     */
    String name();

    /**
     * 获取 Cookie 的值。
     *
     * @return 表示 Cookie 值的 {@link String}。
     */
    String value();

    /**
     * 获取 Cookie 的版本号。
     * <p>其版本号的格式为 {@code ;Version=1 ...}，为 RFC 2109 的风格。</p>
     *
     * @return 表示 Cookie 版本号的 {@code int}。
     */
    int version();

    /**
     * 获取 Cookie 的注释。
     * <p>其注释的格式为 {@code ;Comment=VALUE ...}。</p>
     *
     * @return 表示 Cookie 注释的 {@link String}。
     */
    String comment();

    /**
     * 获取 Cookie 的可见域。
     * <p>其域的格式为 {@code ;Domain=VALUE ...}，只有指定域对该 Cookie 可见。</p>
     *
     * @return 表示 Cookie 可见域的 {@link String}。
     */
    String domain();

    /**
     * 获取 Cookie 的自动过期时间。
     * <p>其自动过期时间的格式为 {@code ;Max-Age=VALUE ...}，单位为秒。</p>
     *
     * @return 表示 Cookie 自动过期时间的 {@code int}。
     */
    int maxAge();

    /**
     * 获取 Cookie 的 URL 路径。
     * <p>其 URL 路径格式为 {@code ;Path=VALUE ...}，只有指定 URL 对该 Cookie 可见。</p>
     *
     * @return 表示 Cookie 的可见 URL 路径的 {@link String}。
     */
    String path();

    /**
     * 判断 Cookie 是否使用了 SSL。
     * <p>其是否使用 SSL 的格式为 {@code ;Secure ...}。</p>
     *
     * @return 如果 Cookie 使用了 SSL，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean secure();

    /**
     * 判断 Cookie 是否仅允许在服务端获取。
     * <p>该属性并不是 Cookie 的标准，但是被浏览器支持。</p>
     *
     * @return 如果 Cookie 仅允许在服务端获取，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean httpOnly();

    /**
     * {@link Cookie} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置 Cookie 的名字。
         *
         * @param name 表示待设置的 Cookie 名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置 Cookie 的值。
         *
         * @param value 表示待设置的 Cookie 值的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder value(String value);

        /**
         * 向当前构建器中设置 Cookie 的版本。
         *
         * @param version 表示待设置的 Cookie 版本的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder version(int version);

        /**
         * 向当前构建器中设置 Cookie 的注释。
         *
         * @param comment 表示待设置的 Cookie 注释的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder comment(String comment);

        /**
         * 向当前构建器中设置 Cookie 的可见域。
         *
         * @param domain 表示待设置的 Cookie 可见域的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder domain(String domain);

        /**
         * 向当前构建器中设置 Cookie 的自动过期时间。
         *
         * @param maxAge 表示待设置的 Cookie 自动过期时间的 {@code int}，其单位为秒。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder maxAge(int maxAge);

        /**
         * 向当前构建器中设置 Cookie 的可见 URL 路径。
         *
         * @param path 表示待设置的 Cookie 可见 URL 路径的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder path(String path);

        /**
         * 向当前构建器中设置 Cookie 的安全性。
         *
         * @param secure 如果 Cookie 使用了 SSL，则为 {@code true}，否则，为 {@code false}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder secure(boolean secure);

        /**
         * 向当前构建器中设置 Cookie 是否仅允许在服务端获取。
         *
         * @param httpOnly 如果 Cookie 仅允许在服务端获取，则为 {@code true}，否则，为 {@code false}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder httpOnly(boolean httpOnly);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Cookie}。
         */
        Cookie build();
    }

    /**
     * 获取 {@link Cookie} 的构建器。
     *
     * @return 表示 {@link Cookie} 的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null).maxAge(-1);
    }

    /**
     * 获取 {@link Cookie} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link Cookie}。
     * @return 表示 {@link Cookie} 的构建器的 {@link Builder}。
     */
    static Builder builder(Cookie value) {
        return BuilderFactory.get(Cookie.class, Builder.class).create(value);
    }
}
