/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.util;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 为 Controller 提供工具方法。
 *
 * @author 梁济时 l00815032
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

    /**
     * 向任务属性中添加类型属性。
     *
     * @param task 表示任务的信息 {@link Map}。
     */
    public static void appendTypeProperty(Map<String, Object> task) {
        Map<String, Object> appearance = MapBuilder.<String, Object>get()
                .put("config", Collections.emptyMap())
                .put("displayOrder", -1)
                .put("displayType", "text")
                .put("modifiable", false)
                .put("options", null)
                .put("visible", true)
                .put("name", "类型")
                .build();
        Map<String, Object> property = MapBuilder.<String, Object>get()
                .put("name", "type")
                .put("appearance", appearance)
                .build();
        List<Map<String, Object>> properties = cast(task.get("properties"));
        properties.add(property);
    }
}
