/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.Cookie;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.SourceFetcher;

/**
 * 表示从 Cookie 中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class CookieFetcher implements SourceFetcher {
    private final String cookieName;

    /**
     * 通过 Cookie 的名字来实例化 {@link CookieFetcher}。
     *
     * @param cookieName 表示 Cookie 名字的 {@link String}。
     * @throws IllegalArgumentException 当 {@code cookieName} 为 {@code null} 或空白字符串时。
     */
    public CookieFetcher(String cookieName) {
        this.cookieName = notBlank(cookieName, "The cookie name cannot be blank.");
    }

    @Override
    public Object get(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        return request.cookies().get(this.cookieName).map(Cookie::value).orElse(null);
    }
}
