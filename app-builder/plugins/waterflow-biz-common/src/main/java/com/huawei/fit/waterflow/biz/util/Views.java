/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.util;

import static com.huawei.fit.waterflow.biz.common.Constant.STREAM_ID_SEPARATOR;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import com.huawei.fit.jane.task.domain.Authorization;
import com.huawei.fit.jane.task.domain.File;
import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.TaskRelation;
import com.huawei.fit.jane.task.domain.Tenant;
import com.huawei.fit.jane.task.domain.TenantMember;
import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.PaginationResult;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.waterflow.biz.common.Constant;
import com.huawei.fit.waterflow.biz.common.vo.FlowDefinitionVO;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import com.huawei.fitframework.model.RangeResult;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 为视图提供工具方法。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
public final class Views {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Views() {
    }

    private static void put(Map<String, Object> view, String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            view.put(key, value);
        }
    }

    private static void put(Map<String, Object> view, String key, Object value) {
        if (value != null) {
            view.put(key, value);
        }
    }

    private static void putList(Map<String, Object> view, String key, List<?> value) {
        if (value != null && !value.isEmpty()) {
            view.put(key, value);
        }
    }

    private static void put(Map<String, Object> view, String key, Map<?, ?> value) {
        if (MapUtils.isNotEmpty(value)) {
            view.put(key, value);
        }
    }

    /**
     * 将指定的键值对放入视图中，如果值为null则不放入。
     *
     * @param view 表示待放入键值对的视图的 {@link Map}。
     * @param key 表示键值对的键的 {@link String}。
     * @param value 表示键值对的值的 {@link Object}。
     * @param mapper 表示将值转换为另一种类型的 {@link Function}。
     */
    public static <T, R> void put(Map<String, Object> view, String key, T value, Function<T, R> mapper) {
        if (value == null) {
            return;
        }
        R actual = mapper.apply(value);
        view.put(key, actual);
    }

    private static <T> void putList(Map<String, Object> view, String key, List<T> values,
            Function<T, Map<String, Object>> mapper) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        List<Map<String, Object>> results = new ArrayList<>(values.size());
        for (T value : values) {
            Map<String, Object> result = mapper.apply(value);
            results.add(result);
        }
        view.put(key, results);
    }

    /**
     * 获取一个视图，表示指定的任务属性。
     *
     * @param property 表示待获取视图的任务属性的 {@link TaskProperty}。
     * @return 表示该任务属性的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(com.huawei.fit.jane.task.domain.TaskProperty property) {
        Map<String, Object> view = new LinkedHashMap<>();
        put(view, "id", property.id());
        put(view, "name", property.name());
        put(view, "dataType", property.dataType());
        put(view, "sequence", property.sequence());
        put(view, "description", property.description());
        put(view, "identifiable", property.identifiable());
        put(view, "required", property.required());
        put(view, "scope", property.scope());
        put(view, "appearance", property.appearance());
        putList(view, "categories", property.categories(), Views::viewOf);
        return view;
    }

    /**
     * viewOf
     *
     * @param propertyCategory propertyCategory
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(PropertyCategory propertyCategory) {
        if (propertyCategory == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(2);
        put(view, "value", propertyCategory.getValue());
        put(view, "category", propertyCategory.getCategory());
        return view;
    }

    /**
     * 获取一个视图，表示分页查询的结果集。
     *
     * @param results 表示分页查询结果集的 {@link RangedResultSet}。
     * @param key 表示结果集的键的 {@link String}。
     * @param mapper 表示用以映射结果元素的方法的 {@link Function}。
     * @param <T> 结果集中元素的类型。
     * @return 表示结果集的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static <T> Map<String, Object> viewOf(RangedResultSet<T> results, String key,
            Function<T, Map<String, Object>> mapper) {
        if (results == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(2);
        view.put(key, results.getResults().stream().map(mapper).collect(Collectors.toList()));
        view.put("pagination", viewOf(results.getRange()));
        return view;
    }

    /**
     * 获取一个视图，表示分页查询的结果集。
     *
     * @param results 表示分页查询结果集的 {@link PagedResultSet}。
     * @param key 表示结果集的键的 {@link String}。
     * @param mapper 表示用以映射结果元素的方法的 {@link Function}。
     * @param <T> 结果集中元素的类型。
     * @return 表示结果集的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static <T> Map<String, Object> viewOf(PagedResultSet<T> results, String key,
            Function<T, Map<String, Object>> mapper) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        view.put(key, results.results().stream().map(mapper).collect(Collectors.toList()));
        view.put("pagination", viewOf(results.pagination()));
        return view;
    }

    private static Map<String, Object> viewOf(PaginationResult result) {
        Map<String, Object> view = new LinkedHashMap<>(3);
        put(view, "offset", result.offset());
        put(view, "limit", result.limit());
        put(view, "total", result.total());
        return view;
    }

    /**
     * 获取一个视图，表示指定的实体列表。
     *
     * @param list 表示实体列表的 {@link List}。
     * @param mapper 表示实体列表中元素的视图映射器的 {@link Function}。
     * @param <T> 表示实体的类型。
     * @return 表示列表的视图的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static <T> List<Map<String, Object>> viewOf(List<T> list, Function<T, Map<String, Object>> mapper) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    private static Map<String, Object> viewOf(RangeResult result) {
        if (result == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(3);
        view.put("offset", result.getOffset());
        view.put("limit", result.getLimit());
        view.put("total", result.getTotal());
        return view;
    }

    /**
     * 获取指定的流程定义信息。
     *
     * @param flowDefinition 表示待获取视图的流程定义 {@link FlowDefinitionVO}。
     * @return 表示流程定义的对外数据。
     */
    public static Map<String, Object> viewOfFlows(FlowDefinitionVO flowDefinition) {
        Map<String, Object> view = new LinkedHashMap<>(1);
        put(view, "flowDefinition", flowDefinition);
        return view;
    }

    /**
     * 获取指定的流程定义的状态。
     *
     * @param flowDefinitionPO 表示待获取视图的流程定义 {@link FlowDefinitionPO}。
     * @return 表示流程定义状态。
     */
    public static Map<String, Object> viewOfFlowStatus(FlowDefinitionPO flowDefinitionPO) {
        Map<String, Object> view = new LinkedHashMap<>(1);
        view.put("status", flowDefinitionPO == null ? "unpublished" : flowDefinitionPO.getStatus());
        return view;
    }

    /**
     * viewOfFlows
     *
     * @param flowDefinition flowDefinition
     * @param graphData graphData
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOfFlows(FlowDefinition flowDefinition, String graphData) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        put(view, "flowDefinitionId", flowDefinition.getDefinitionId());
        put(view, "metaId", flowDefinition.getMetaId());
        put(view, "version", flowDefinition.getVersion());
        put(view, "graphData", graphData);
        return view;
    }

    /**
     * viewOfFlows
     *
     * @param flows flows
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> viewOfFlows(List<FlowDefinitionPO> flows) {
        if (flows.isEmpty()) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> view = new ArrayList<>();
        flows.forEach(flow -> {
            Map<String, Object> map = new LinkedHashMap<>(4);
            put(map, "flowDefinitionId", flow.getDefinitionId());
            put(map, "name", flow.getName());
            put(map, "version", flow.getVersion());
            put(map, "status", flow.getStatus());
            view.add(map);
        });
        return view;
    }

    /**
     * viewOfFlows
     *
     * @param flows flows
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOfFlows(FlowDefinitionPO flows) {
        if (flows == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(2);
        put(view, "flowDefinitionId", flows.getDefinitionId());
        put(view, "metaId", flows.getMetaId());
        put(view, "versionStatus", flows.getStatus());
        put(view, "releaseTime", flows.getCreatedAt());
        return view;
    }

    /**
     * 流程运行后的标识信息
     *
     * @param id 标识信息
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOfFlowRunResult(FlowOfferId id) {
        Map<String, Object> view = new LinkedHashMap<>(3);
        put(view, "traceId", id.getTraceId());
        put(view, "transId", id.getTrans().getId());
        return view;
    }

    /**
     * viewOfContexts
     *
     * @param pos pos
     * @return List<Map < String, Object>>
     */
    public static <T> Map<String, Object> viewOfContexts(List<T> pos) {
        if (pos.isEmpty()) {
            return new LinkedHashMap<>();
        }

        Map<String, Object> view = new LinkedHashMap<>();
        putList(view, "contexts", pos);
        return view;
    }

    /**
     * viewOfContextStatus
     *
     * @param flowContexts flowContexts
     * @return Map<String, Map < String, Long>>
     */
    public static Map<String, Map<String, Long>> viewOfContextStatus(List<FlowContext<FlowData>> flowContexts) {
        if (flowContexts.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return flowContexts.stream()
                .collect(Collectors.groupingBy(FlowContext::getPosition,
                        Collectors.groupingBy(c -> c.getStatus().toString(), Collectors.counting())));
    }

    /**
     * 获取一个视图，表示指定的租户。
     *
     * @param tenant 表示待获取视图的租户的 {@link Tenant}。
     * @return 表示该租户的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(Tenant tenant) {
        if (tenant == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(8);
        put(view, "id", tenant.id());
        put(view, "name", tenant.name());
        put(view, "description", tenant.description());
        put(view, "avatarId", tenant.avatarId());
        putList(view, "tags", tenant.tags());
        put(view, "creationTime", Dates.toString(tenant.creationTime()));
        put(view, "accessLevel", tenant.accessLevel());
        return view;
    }

    /**
     * 获取一个视图，表示指定的租户成员。
     *
     * @param tenantMember 表示待获取视图的租户成员的 {@link Tenant}。
     * @return 表示该租户成员的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(TenantMember tenantMember) {
        if (tenantMember == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(8);
        put(view, "id", tenantMember.id());
        put(view, "tenantId", tenantMember.tenantId());
        put(view, "userId", tenantMember.userId());
        put(view, "creationTime", Dates.toString(tenantMember.creationTime()));
        return view;
    }

    /**
     * 获取一个视图，表示指定的页面响应。
     *
     * @param getPageResponse 表示待获取视图的页面响应的 {@link GetPageResponse}。
     * @return 表示页面响应的视图的
     *         {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(GetPageResponse getPageResponse) {
        Map<String, Object> view = new LinkedHashMap<>();
        if (getPageResponse == null) {
            return view;
        }
        putList(view, "data", getPageResponse.getData());
        put(view, "code", getPageResponse.getCode());
        put(view, "msg", getPageResponse.getMsg());
        put(view, "count", getPageResponse.getCount());
        put(view, "cursor", getPageResponse.getCursor());
        put(view, "size", getPageResponse.getSize());
        return view;
    }

    /**
     * 获取一个视图，表示指定的流程引擎和Elsa流程。
     *
     * @param flowsEngine 表示待获取视图的流程引擎的 {@link Map}。
     * @param elsaFlow 表示待获取视图的Elsa流程的 {@link FlowInfo}。
     * @return 表示流程引擎和Elsa流程的视图的
     *         {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(Map<String, Object> flowsEngine, FlowInfo elsaFlow) {
        Map<String, Object> view = new LinkedHashMap<>(3);
        if (flowsEngine.isEmpty() && elsaFlow == null) {
            return view;
        }
        if (!flowsEngine.isEmpty()) {
            put(view, "metaId", flowsEngine.get("metaId"));
            put(view, "version", flowsEngine.get("version"));
        }
        if (elsaFlow != null) {
            put(view, "updateTime", new Date());
        }
        return view;
    }

    /**
     * 获取指定的流程定义信息。
     *
     * @param flows 表示待获取视图的流程定义的 {@link Map}。
     * @return 表示流程定义的对外数据。
     */
    public static List<Map<String, Object>> viewOfFlows(Map<String, FlowDefinitionPO> flows) {
        List<Map<String, Object>> view = new ArrayList<>();
        flows.entrySet().stream().forEach(e -> {
            Map<String, Object> flowData = new LinkedHashMap<>();
            put(flowData, "releaseTime", e.getValue().getCreatedAt());
            put(flowData, "versionStatus", e.getValue().getStatus());
            put(flowData, "streamId", e.getValue().getMetaId() + STREAM_ID_SEPARATOR + e.getValue().getVersion());
            view.add(flowData);
        });
        return view;
    }

    /**
     * 获取一个视图，表示指定的文件。
     *
     * @param file 表示待获取视图的文件的 {@link File}。
     * @return 表示该文件的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(File file) {
        if (file == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(8);
        put(view, "id", file.id());
        return view;
    }

    /**
     * 根据flowGraphDefinition获取一个视图
     *
     * @param flowGraphDefinition flowGraphDefinition
     * @return 视图
     */
    public static Map<String, Object> viewOfFlowGraphDefinition(FlowGraphDefinition flowGraphDefinition) {
        if (flowGraphDefinition == null) {
            return new LinkedHashMap<>();
        }
        return processFlowGraph(flowGraphDefinition, "isDeleted");
    }

    /**
     * 根据flowGraphDefinition的list获取一个视图
     *
     * @param flowGraphDefinitions flowGraphDefinition的集合
     * @return 流程版本集合
     */
    public static Map<String, Object> viewOfFlowGraphList(List<FlowGraphDefinition> flowGraphDefinitions) {
        Map<String, Object> view = new LinkedHashMap<>(1);
        if (flowGraphDefinitions == null) {
            view.put("flowList", new ArrayList<>());
            return view;
        }
        List<Map<String, Object>> graphDefinitions = new ArrayList<>();
        flowGraphDefinitions.forEach(
                from -> graphDefinitions.add(processFlowGraph(from, "isDeleted", "graphData", "tags", "tenant")));
        view.put("flowList", graphDefinitions);
        return view;
    }

    private static Map<String, Object> processFlowGraph(FlowGraphDefinition flowGraphDefinition, String... except) {
        if (flowGraphDefinition == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(12);
        JSONObject from = convertToJson(flowGraphDefinition);
        from.keySet()
                .stream()
                .filter(key -> !flowGraphFilter(key, except))
                .forEach(key -> view.put(key, from.get(key)));
        return view;
    }

    private static boolean flowGraphFilter(String key, String[] except) {
        if (except == null || except.length == 0) {
            return false;
        }
        Optional<String> first = Arrays.stream(except).filter(key::equals).findFirst();
        return first.isPresent();
    }

    private static JSONObject convertToJson(FlowGraphDefinition flowGraphDefinition) {
        return ObjectUtils.cast(JSONObject.toJSON(flowGraphDefinition));
    }

    /**
     * 获取一个视图，表示指定的授权信息。
     *
     * @param authorization 表示待获取视图的授权信息的 {@link Authorization}。
     * @return 表示授权信息的视图的
     *         {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(Authorization authorization) {
        Map<String, Object> view = new LinkedHashMap<>(8);
        put(view, "id", authorization.id());
        put(view, "system", authorization.system());
        put(view, "user", authorization.user());
        put(view, "token", authorization.token());
        put(view, "expiration", authorization.expiration());
        put(view, "creator", authorization.creator());
        put(view, "creationTime", Dates.toString(authorization.creationTime()));
        put(view, "lastModifier", authorization.lastModifier());
        put(view, "lastModificationTime", Dates.toString(authorization.lastModificationTime()));
        if (authorization.creationTime().isAfter(authorization.lastModificationTime())) {
            if (authorization.expiration() > ChronoUnit.MILLIS.between(authorization.lastModificationTime(),
                    LocalDateTime.now())) {
                put(view, "isExpire", false);
            } else {
                put(view, "isExpire", true);
            }
        } else {
            if (authorization.expiration() > ChronoUnit.MILLIS.between(authorization.creationTime(),
                    LocalDateTime.now())) {
                put(view, "isExpire", false);
            } else {
                put(view, "isExpire", true);
            }
        }
        return view;
    }

    /**
     * 获取一个视图，表示指定的任务关联关系。
     *
     * @param taskRelation 表示待获取任务关联的 {@link TaskRelation}。
     * @return 表示该任务关联的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(TaskRelation taskRelation) {
        if (taskRelation == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(8);
        put(view, "id", taskRelation.id());
        put(view, "objectId1", taskRelation.objectId1());
        put(view, "objectType1", taskRelation.objectType1());
        put(view, "objectId2", taskRelation.objectId2());
        put(view, "objectType2", taskRelation.objectType2());
        put(view, "relationType", taskRelation.relationType());
        put(view, "createdBy", taskRelation.createdBy());
        put(view, "createdAt", Dates.toString(taskRelation.createdAt()));
        return view;
    }

    /**
     * viewOfFlowInfo
     *
     * @param flowInfo flowInfo
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOfFlowInfo(FlowInfo flowInfo) {
        if (flowInfo == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>();
        JSONObject parsedData = JSONObject.parseObject(
                flowInfo.getConfigData());
        String name = parsedData.getString("title");
        put(view, "flowId", flowInfo.getFlowId());
        put(view, "version", flowInfo.getVersion());
        put(view, "graphData", flowInfo.getConfigData());
        put(view, "name", name);
        return view;
    }

    /**
     * viewOf
     *
     * @param supplier supplier
     * @param plugin plugin
     * @param httpRequest 请求体
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(Supplier<Object> supplier, Plugin plugin,
            HttpClassicServerRequest httpRequest) {
        String context = "";
        if (httpRequest != null) {
            context = httpRequest.headers()
                    .first("Accept-Language")
                    .orElse(httpRequest.headers().first("accept-language").orElse(StringUtils.EMPTY));
        }
        try {
            return new HashMap<String, Object>() {
                {
                    put("code", 0);
                    put("data", supplier.get());
                    put("message", "success");
                }
            };
        } catch (JobberException exception) {
            String language = context;
            return new HashMap<String, Object>() {
                {
                    put("code", exception.getCode());
                    put("message", getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(),
                            exception.getArgs(), language, plugin));
                }
            };
        } catch (Exception exception) {
            return new HashMap<String, Object>() {
                {
                    put("code", -1);
                    put("message", exception.getMessage());
                }
            };
        }
    }

    /**
     * 获取国际化异常信息
     *
     * @param plugin plugin
     * @param code 错误编码
     * @param defaultMsg 默认返回信息
     * @param params plugin
     * @param language 语言
     * @return 返回响应信息
     */
    public static String getLocaleMessage(String code, String defaultMsg, Object[] params, String language,
            Plugin plugin) {
        if (StringUtils.isEmpty(language)) {
            return defaultMsg;
        }
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
        Locale locale = StringUtils.isNotEmpty(language) ? Locale.lookup(list, Constant.LOCALES) : Locale.getDefault();
        try {
            String message = plugin.sr().getMessage(locale, code, params);
            if (StringUtils.isEmpty(message)) {
                return defaultMsg;
            }
            return message;
        } catch (Exception e) {
            return defaultMsg;
        }
    }
}
