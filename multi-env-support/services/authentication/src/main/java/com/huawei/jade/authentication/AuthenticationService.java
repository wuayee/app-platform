/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.authentication;

import com.huawei.fit.http.server.HttpClassicServerRequest;

/**
 * 用户认证服务接口。
 *
 * @author 陈潇文
 * @since 2024-07-30
 */
public interface AuthenticationService {
    /**
     * 根据请求获取用户名称。
     *
     * @param request 表示 http 请求的 {@link HttpClassicServerRequest}。
     * @return 表示用户名称的 {@link String}。
     */
    String getUserName(HttpClassicServerRequest request);
}
