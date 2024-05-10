/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link Authenticator} 提供不含sso的a3000默认实现。
 *
 * @author s00664640
 * @since 2023/11/28
 */
@Component
public class BaseAuthenticator implements Authenticator {
    @Override
    public User authenticate(HttpClassicServerRequest request) {
        String key = "com.huawei.jade";
        return User.custom().account(key).name(key).fqn(key).build();
    }
}
