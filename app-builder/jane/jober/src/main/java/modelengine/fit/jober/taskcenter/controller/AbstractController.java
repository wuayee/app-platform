/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.controller;

import static modelengine.fit.jober.taskcenter.controller.Views.viewOf;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.PagedResultSet;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 Controller 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
public abstract class AbstractController {
    /**
     * 表示 URI 的前缀，包含 tenant_id 路径参数。
     */
    public static final String URI_PREFIX = "/v1/{tenant_id}";

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
        return request.headers().first("Accept-Language")
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
        String operator = getOperator(request);
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
     * @return 操作人
     */
    protected String getOperator(HttpClassicServerRequest request) {
        return this.authenticator.authenticate(request).fqn();
    }

    /**
     * 添加类型属性
     *
     * @param task 表示任务map
     */
    protected static void appendTypeProperty(Map<String, Object> task) {
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

    /**
     * 构造多任务实例视图
     *
     * @param results 表示分页结果集
     * @param taskEntityList 表示任务实体列表
     * @return 多任务实例视图
     */
    protected static Map<String, Object> buildMultiTaskInstanceView(PagedResultSet<TaskInstance> results,
            List<TaskEntity> taskEntityList) {
        List<TaskInstance> instanceList = results.results()
                .stream()
                .peek(AbstractController::convertListOwner)
                .collect(Collectors.toList());
        Map<String, Object> view = viewOf(PagedResultSet.create(instanceList, results.pagination()), "instances",
                Views::viewOf);
        Map<String, Object> actualView = new LinkedHashMap<>(view.size() + 1);
        List<Map<String, Object>> taskView = new ArrayList<>(taskEntityList.size());
        taskEntityList.forEach(task -> {
            Map<String, Object> taskInfo = viewOf(task);
            appendTypeProperty(taskInfo);
            taskView.add(taskInfo);
        });
        actualView.put("task", taskView);
        actualView.putAll(view);
        return actualView;
    }

    private static void convertListOwner(TaskInstance instance) {
        if (instance.info().get("owner") != null) {
            if (instance.info().get("owner") instanceof ArrayList) {
                instance.info().put("owner", String.join(",", (ArrayList) instance.info().get("owner")));
            }
        }
    }
}
