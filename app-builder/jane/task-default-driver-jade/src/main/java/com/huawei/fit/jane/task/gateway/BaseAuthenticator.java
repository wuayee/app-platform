/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;

/**
 * 为 {@link Authenticator} 提供不含sso的a3000默认实现。
 *
 * @author 孙怡菲
 * @since 2023/11/28
 */
@Component
public class BaseAuthenticator implements Authenticator {
    @Override
    public User authenticate(HttpClassicServerRequest request) {
        UserContext userContext = Validation.notNull(UserContextHolder.get(), "The user context cannot be null.");
        return User.custom()
                .account(userContext.getName())
                .name(userContext.getName())
                .fqn(userContext.getName())
                .build();
    }
}
