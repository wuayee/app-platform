/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.authentication;

import modelengine.fit.http.Cookie;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

/**
 * 表示用户认证服务接口实现。
 *
 * @author 陈潇文
 * @since 2024-07-30
 */
@Component
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String USERNAME_KEY = "username";

    private static final String DEFAULT_USERNAME = "Jade";

    @Override
    public String getUserName(HttpClassicServerRequest request) {
        return request.cookies()
            .all()
            .stream()
            .filter(cookie -> USERNAME_KEY.equals(cookie.name()))
            .findFirst()
            .map(Cookie::value)
            .orElse(DEFAULT_USERNAME);
    }
}
