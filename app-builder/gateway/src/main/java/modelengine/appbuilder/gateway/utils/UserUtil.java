/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.utils;

import modelengine.framework.crypt.grpc.client.exception.CryptoInvokeException;

import modelengine.appbuilder.gateway.jwt.JsonToken;
import modelengine.appbuilder.gateway.jwt.JsonTokenPayload;

import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

/**
 * 用户相关工具类
 *
 * @author 邬涨财
 * @since 2025-01-06
 */
public class UserUtil {
    private static final String BEARER = "Bearer ";

    /**
     * 构建新的请求参数
     *
     * @param exchange 表示 http 交换器的 {@link ServerWebExchange}。
     * @param userName 表示用户名的 {@link String}。
     * @return 表示新的请求参数构建器的 {@link ServerHttpRequest.Builder}。
     * @throws CryptoInvokeException jwt解析失败时抛出
     */
    public static ServerHttpRequest.Builder buildNewRequestBuilder(ServerWebExchange exchange, String userName)
            throws CryptoInvokeException {
        JsonTokenPayload jsonTokenPayload = JsonTokenPayload.builder().user(userName).build();
        JsonToken jsonToken = new JsonToken(jsonTokenPayload);
        String jwtString;
        jwtString = jsonToken.toJwt();
        ServerHttpRequest request = exchange.getRequest();
        return request.mutate().header(HttpHeaders.AUTHORIZATION, BEARER + jwtString);
    }
}
