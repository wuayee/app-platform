/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.authentication;

import modelengine.fit.http.server.HttpClassicServerRequest;

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
