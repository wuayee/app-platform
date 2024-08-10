/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.authentication;

import com.huawei.fit.http.Cookie;
import com.huawei.fit.http.header.CookieCollection;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fitframework.annotation.Component;

import java.util.Optional;

/**
 * 表示用户认证服务接口实现。
 *
 * @author 陈潇文
 * @since 2024-07-30
 */
@Component
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public String getUserName(HttpClassicServerRequest request) {
        CookieCollection cookies = request.cookies();
        Optional<Cookie> tokenOptional = cookies.get("X-Uni-Crsf-Token");
        Optional<Cookie> tokenIdOptional = cookies.get("X-Uni-Crsf-Token-Id");

        return !tokenOptional.isPresent() || !tokenIdOptional.isPresent() ? "admin" : "Jade";
    }
}
