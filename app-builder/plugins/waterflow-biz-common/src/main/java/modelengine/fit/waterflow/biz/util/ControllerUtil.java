/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.biz.util;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 为 Controller 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
public class ControllerUtil {
    private static final String UNKNOWN_IP = "unknown";

    private static Optional<String> header(HttpClassicServerRequest request, String name) {
        return request.headers()
                .names()
                .stream()
                .filter(value -> StringUtils.equalsIgnoreCase(value, name))
                .findFirst()
                .flatMap(request.headers()::first);
    }

    private static String compute(List<Function<HttpClassicServerRequest, Optional<String>>> mappers,
            HttpClassicServerRequest request) {
        Optional<String> optional = Optional.empty();
        for (Function<HttpClassicServerRequest, Optional<String>> mapper : mappers) {
            optional = mapper.apply(request);
            if (optional.isPresent()) {
                break;
            }
        }
        return optional.orElse(request.remoteAddress().hostAddress());
    }

    private static Optional<String> getForwardedIp(HttpClassicServerRequest request) {
        return header(request, "X-Forwarded-For").map(value -> StringUtils.split(value, ','))
                .map(Stream::of)
                .orElse(Stream.empty())
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .filter(ControllerUtil::knownIp)
                .findFirst();
    }

    private static Optional<String> getProxyClientIp(HttpClassicServerRequest request) {
        return header(request, "Proxy-Client-IP").filter(ControllerUtil::knownIp);
    }

    private static Optional<String> getWlProxyClientIp(HttpClassicServerRequest request) {
        return header(request, "WL-Proxy-Client-IP").filter(ControllerUtil::knownIp);
    }

    private static Optional<String> getHttpClientIp(HttpClassicServerRequest request) {
        return header(request, "HTTP_CLIENT_IP").filter(ControllerUtil::knownIp);
    }

    private static Optional<String> getHttpForwardedFor(HttpClassicServerRequest request) {
        return header(request, "HTTP_X_FORWARDED_FOR").filter(ControllerUtil::knownIp);
    }

    private static boolean knownIp(String ip) {
        return !StringUtils.equalsIgnoreCase(ip, UNKNOWN_IP);
    }

    private static String getAcceptLangaes(HttpClassicServerRequest request) {
        return request.headers()
                .first("Accept-Language")
                .orElse(request.headers().first("accept-language").orElse(StringUtils.EMPTY));
    }

    private static String getSourcePlatform(HttpClassicServerRequest request) {
        return request.headers().first("SourcePlatform").orElse(StringUtils.EMPTY);
    }

    /**
     * 获取操作上下文。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param authenticator 认证器
     * @return 表示操作上下文的 {@link OperationContext}。
     */
    public static OperationContext contextOf(HttpClassicServerRequest request, String tenantId,
            Authenticator authenticator) {
        String ip = compute(Arrays.asList(ControllerUtil::getForwardedIp, ControllerUtil::getProxyClientIp,
                ControllerUtil::getWlProxyClientIp, ControllerUtil::getHttpClientIp,
                ControllerUtil::getHttpForwardedFor), request);
        String operator = getOperator(request, authenticator);
        return OperationContext.custom()
                .operator(operator)
                .operatorIp(ip)
                .tenantId(tenantId)
                .langage(getAcceptLangaes(request))
                .sourcePlatform(getSourcePlatform(request))
                .build();
    }

    /**
     * 解析操作人
     *
     * @param request http请求
     * @param authenticator 认证器
     * @return 操作人
     */
    public static String getOperator(HttpClassicServerRequest request, Authenticator authenticator) {
        return authenticator.authenticate(request).fqn();
    }
}
