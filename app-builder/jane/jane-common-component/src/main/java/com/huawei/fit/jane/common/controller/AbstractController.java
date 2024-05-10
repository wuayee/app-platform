/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.controller;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.gateway.User;
import com.huawei.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 为 Controller 提供工具方法。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-08
 */
public abstract class AbstractController {
    private static final String UNKNOWN_IP = "unknown";

    private final Authenticator authenticator;

    public AbstractController(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

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
                .filter(AbstractController::knownIp)
                .findFirst();
    }

    private static Optional<String> getProxyClientIp(HttpClassicServerRequest request) {
        return header(request, "Proxy-Client-IP").filter(AbstractController::knownIp);
    }

    private static Optional<String> getWlProxyClientIp(HttpClassicServerRequest request) {
        return header(request, "WL-Proxy-Client-IP").filter(AbstractController::knownIp);
    }

    private static Optional<String> getHttpClientIp(HttpClassicServerRequest request) {
        return header(request, "HTTP_CLIENT_IP").filter(AbstractController::knownIp);
    }

    private static Optional<String> getHttpForwardedFor(HttpClassicServerRequest request) {
        return header(request, "HTTP_X_FORWARDED_FOR").filter(AbstractController::knownIp);
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
     * @return 表示操作上下文的 {@link OperationContext}。
     */
    public OperationContext contextOf(HttpClassicServerRequest request, String tenantId) {
        String ip = compute(Arrays.asList(AbstractController::getForwardedIp, AbstractController::getProxyClientIp,
                AbstractController::getWlProxyClientIp, AbstractController::getHttpClientIp,
                AbstractController::getHttpForwardedFor), request);
        User user = this.authenticator.authenticate(request);
        if (user == null) {
            return new OperationContext(tenantId, null, null, null, null, null, ip, getSourcePlatform(request),
                    getAcceptLangaes(request));
        }
        return new OperationContext(tenantId, user.fqn(), null, user.account(), null, user.name(), ip,
                getSourcePlatform(request), getAcceptLangaes(request));
    }
}
