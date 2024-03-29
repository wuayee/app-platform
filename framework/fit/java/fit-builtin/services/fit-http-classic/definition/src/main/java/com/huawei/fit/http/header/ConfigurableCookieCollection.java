/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http.header;

import com.huawei.fit.http.Cookie;
import com.huawei.fit.http.support.DefaultCookieCollection;

/**
 * 表示 Http 中的可读可写的 Cookie 集合。
 *
 * @author 季聿阶 j00559309
 * @since 2022-07-07
 */
public interface ConfigurableCookieCollection extends CookieCollection {
    /**
     * 添加一个指定的 {@link Cookie}。
     *
     * @param cookie 表示待添加的 {@link Cookie}。
     */
    void add(Cookie cookie);

    /**
     * 创建一个新的可读可写的 Cookie 集合。
     *
     * @return 表示创建出来的可读可写的 Cookie 集合的 {@link ConfigurableCookieCollection}。
     */
    static ConfigurableCookieCollection create() {
        return new DefaultCookieCollection();
    }

    /**
     * 根据指定的消息头创建一个可读可写的 Cookie 集合。
     *
     * @param headerValue 表示指定消息头的 {@link HeaderValue}。
     * @return 表示创建出来的可读可写的 Cookie 集合的 {@link ConfigurableCookieCollection}。
     * @throws IllegalArgumentException 当 {@code headerValue} 为 {@code null} 时。
     */
    static ConfigurableCookieCollection create(HeaderValue headerValue) {
        return new DefaultCookieCollection(headerValue);
    }
}
