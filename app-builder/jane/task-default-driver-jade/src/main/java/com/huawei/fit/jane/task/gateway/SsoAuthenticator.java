/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fitframework.annotation.Component;

@Component
public class SsoAuthenticator implements Authenticator {
    @Override
    public User authenticate(HttpClassicServerRequest request) {
        String key = "com.huawei.jade";
        return User.custom().account(key).name(key).fqn(key).build();
    }
}


