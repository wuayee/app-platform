/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.util;

import static com.huawei.fit.waterflow.biz.common.Constant.STREAM_ID_SEPARATOR;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import com.huawei.fit.jane.task.domain.Authorization;
import com.huawei.fit.jane.task.domain.DomainObject;
import com.huawei.fit.jane.task.domain.File;
import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.PropertyCategoryDeclaration;
import com.huawei.fit.jane.task.domain.TaskRelation;
import com.huawei.fit.jane.task.domain.Tenant;
import com.huawei.fit.jane.task.domain.TenantAccessLevel;
import com.huawei.fit.jane.task.domain.TenantMember;
import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.PaginationResult;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.jober.entity.TaskEntity;
import com.huawei.fit.jober.entity.TaskFilter;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.jober.entity.task.TaskType;
import com.huawei.fit.waterflow.biz.common.Constant;
import com.huawei.fit.waterflow.biz.common.entity.CleanTaskPageResult;
import com.huawei.fit.waterflow.biz.common.vo.FlowDefinitionVO;
import com.huawei.fit.waterflow.common.utils.UUIDUtil;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowDefinitionPO;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.RangeResult;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为视图提供工具方法。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
public final class Views {
    private static final Logger log = Logger.get(Views.class);

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Views() {
    }

    private static <T> UndefinableValue<T> valueOf(Map<String, Object> view, String key) {
        if (!view.containsKey(key)) {
            return UndefinableValue.undefined();
        }
        T value = cast(view.get(key));
        return UndefinableValue.defined(value);
    }

    private static <T, R> UndefinableValue<List<R>> listOf(Map<String, Object> view, String key,
            Function<T, R> mapper) {
        if (!view.containsKey(key)) {
            return UndefinableValue.undefined();
        }
        @SuppressWarnings("unchecked") List<Object> list = (List<Object>) view.get(key);
        if (list == null) {
            return UndefinableValue.defined(null);
        }
        List<R> results = new ArrayList<>(list.size());
        for (Object item : list) {
            T value = cast(item);
            R result = mapper.apply(value);
            results.add(result);
        }
        return UndefinableValue.defined(results);
    }

    private static List<PropertyCategoryDeclaration> declarePropertyCategories(List<Map<String, Object>> views) {
        if (views == null) {
            return null;
        } else {
            return views.stream().map(Views::declarePropertyCategory).collect(Collectors.toList());
        }
    }

    private static PropertyCategoryDeclaration declarePropertyCategory(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        PropertyCategoryDeclaration declaration = new PropertyCategoryDeclaration();
        declaration.setValue(valueOf(view, "value"));
        declaration.setCategory(valueOf(view, "category"));
        return declaration;
    }

    /**
     * 从视图中解析任务属性的声明信息。
     *
     * @param view 表示包含声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从视图中解析到的任务属性的 {@link com.huawei.fit.jane.task.domain.TaskProperty.Declaration}。
     */
    public static com.huawei.fit.jane.task.domain.TaskProperty.Declaration declareProperty(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        com.huawei.fit.jane.task.domain.TaskProperty.Declaration.Builder builder
                = com.huawei.fit.jane.task.domain.TaskProperty.Declaration.custom();
        declare(view, "name", builder::name);
        declare(view, "templateId", builder::templateId);
        declare(view, "dataType", builder::dataType);
        declare(view, "description", builder::description);
        declare(view, "required", builder::isRequired);
        declare(view, "identifiable", builder::isIdentifiable);
        declare(view, "scope", builder::scope);
        declare(view, "appearance", builder::appearance);
        declare(view, "categories", Views::declarePropertyCategories, builder::categories);
        return builder.build();
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

    private static void put(Map<String, Object> view, String key, LocalDateTime value) {
        if (value != null) {
            view.put(key, Dates.toString(value));
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

    public static <T> UndefinableValue<List<T>> defineList(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return UndefinableValue.undefined();
        } else {
            return UndefinableValue.defined(decodeList(list));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> decodeList(List<T> list) {
        return list.stream().map(param -> {
            if (param instanceof String) {
                try {
                    return (T) URLDecoder.decode((String) param, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.warn("unsupported encoding for string {}", param);
                    return null;
                }
            }
            return param;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static <T> void ifContainsKey(Map<String, Object> view, String key, Consumer<T> consumer) {
        if (view.containsKey(key)) {
            T value = cast(view.get(key));
            consumer.accept(value);
        }
    }

    public static Map<String, Object> viewOfFlows(FlowDefinitionVO flowDefinition) {
        Map<String, Object> view = new LinkedHashMap<>(1);
        put(view, "flowDefinition", flowDefinition);
        return view;
    }

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
     * viewOfTraceId
     *
     * @param traceId traceId
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOfTraceId(String traceId) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        put(view, "traceId", traceId);
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

    private static <T> void declare(Map<String, Object> view, String key, Consumer<T> consumer) {
        if (view.containsKey(key)) {
            Object value = view.get(key);
            T actual = cast(value);
            consumer.accept(actual);
        }
    }

    private static <T, R> void declare(Map<String, Object> view, String key, Function<T, R> mapper,
            Consumer<R> consumer) {
        if (view.containsKey(key)) {
            Object value = view.get(key);
            R actual = mapper.apply(cast(value));
            consumer.accept(actual);
        }
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
     * 从 HTTP 请求中解析租户的过滤器。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @return 表示解析到的过滤器的 {@link Tenant.Filter}。
     */
    public static Tenant.Filter filterOfTenants(HttpClassicServerRequest request) {
        Tenant.Filter.Builder builder = Tenant.Filter.custom();
        consumeQueryArgumentsIfExists(request, "id", builder::ids);
        consumeQueryArgumentsIfExists(request, "name", builder::names);
        consumeQueryArgumentsIfExists(request, "tag", builder::tags);
        consumeQueryArgumentsIfExists(request, "accessLevel", (accessLevels) -> builder.accessLevels(
                accessLevels.stream().map(TenantAccessLevel::valueOf).collect(Collectors.toList())));
        return builder.build();
    }

    /**
     * 从 HTTP 请求中解析租户成员的过滤器。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @return 表示解析到的过滤器的 {@link TenantMember.Filter}。
     */
    public static TenantMember.Filter filterOfTenantMembers(HttpClassicServerRequest request, String tenantId) {
        TenantMember.Filter.Builder builder = TenantMember.Filter.custom();
        consumeQueryArgumentsIfExists(request, "id", builder::ids);
        builder.tenantId(tenantId);
        consumeQueryArgumentsIfExists(request, "userId", builder::userIds);
        return builder.build();
    }

    private static void consumeQueryArgumentsIfExists(HttpClassicServerRequest request, String key,
            Consumer<List<String>> consumer) {
        List<String> arguments = Optional.ofNullable(request.queries().all(key))
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (!arguments.isEmpty()) {
            consumer.accept(arguments);
        }
    }

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

    public static Map<String, Object> flowContextCountViewOf(Map<String, List<FlowContextPO>> flowContexts) {
        Map<String, Object> view = new LinkedHashMap<>(4);
        if (flowContexts.isEmpty() || flowContexts.get("start").isEmpty()) {
            return view;
        }
        put(view, "allContexts", flowContexts.get("start").size());
        put(view, "runningContexts", flowContexts.get("running").size());
        put(view, "errorContexts", flowContexts.get("error").size());
        put(view, "streamId", flowContexts.get("start").get(0).getStreamId());
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

    /**
     * 提取包含flowGraph部分字段以及分页参数的视图
     *
     * @param flowGraphDefinitions 查询到的flowGraph定义列表
     * @param count 符合条件的记录总数
     * @param offset 分页参数：偏移
     * @param limit 分页参数：条数
     * @return 包含flowGraph部分字段以及分页参数的视图
     */
    public static Map<String, Object> viewOfFlowGraphPage(List<FlowGraphDefinition> flowGraphDefinitions,
            Map<String, List<String>> flowIdAndTags, int count, int offset, int limit) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        Map<String, Object> pagination = new LinkedHashMap<>(3);
        put(pagination, "limit", limit);
        put(pagination, "offset", offset);
        put(pagination, "total", count);
        List<Map<String, Object>> definitions = new ArrayList<>();
        flowGraphDefinitions.forEach(definition -> definitions.add(
                viewOfFlowGraph(definition, flowIdAndTags, "tenant", "graphData", "isDeleted")));
        view.put("definitions", definitions);
        put(view, "pagination", pagination);
        return view;
    }

    /**
     * 提取flowGraph中需要的字段的视图
     *
     * @param flowGraphDefinition 查询到的flowGraph定义
     * @param ExcludeParam flowGraph定义中不需要的字段
     * @return flowGraph的视图，包含需要的字段
     */
    public static Map<String, Object> viewOfFlowGraph(FlowGraphDefinition flowGraphDefinition,
            Map<String, List<String>> flowIdAndTags, String... ExcludeParam) {
        if (flowGraphDefinition == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>();
        HashSet<String> exclude = Arrays.stream(ExcludeParam).collect(Collectors.toCollection(HashSet::new));
        JSONObject from = convertToJson(flowGraphDefinition);
        from.keySet().stream().filter(key -> !exclude.contains(key)).forEach(key -> put(view, key, from.get(key)));
        view.put("tags", flowIdAndTags.get(flowGraphDefinition.getFlowId()));
        return view;
    }

    public static Authorization.Declaration declareAuthorization(Map<String, Object> view) {
        Authorization.Declaration.Builder builder = Authorization.Declaration.custom();
        declare(view, "system", builder::system);
        declare(view, "user", builder::user);
        declare(view, "token", builder::token);
        declare(view, "expiration", (Number value) -> builder.expiration(nullIf(value, 0L).longValue()));
        return builder.build();
    }

    private static JSONObject convertToJson(FlowGraphDefinition flowGraphDefinition) {
        return (JSONObject) JSONObject.toJSON(flowGraphDefinition);
    }

    /**
     * viewOf
     *
     * @param cleanTaskPageResult 分页清洗结果
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(CleanTaskPageResult cleanTaskPageResult) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        view.put("totalPage", cleanTaskPageResult.getTotalNum());
        view.put("result", cleanTaskPageResult.getResult());
        return view;
    }

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

    public static Authorization.Filter filterOfAuthorization(HttpClassicServerRequest httpRequest) {
        Authorization.Filter.Builder builder = Authorization.Filter.custom();
        builder.ids(httpRequest.queries().all("id"));
        builder.systems(httpRequest.queries().all("system"));
        builder.users(httpRequest.queries().all("user"));
        return builder.build();
    }

    public static Map<String, Object> viewOfFitableUsage(FlowDefinitionPO definition) {
        Map<String, Object> view = new LinkedHashMap<>(4);
        put(view, "id", definition.getMetaId());
        put(view, "version", definition.getVersion());
        put(view, "name", definition.getName());
        put(view, "graph", definition.getGraph());
        return view;
    }

    /**
     * 从视图中解析任务关联的声明信息。
     *
     * @param view 表示包含声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从视图中解析到的任务关联的 {@link TaskRelation.Declaration}。
     */
    public static TaskRelation.Declaration declareTaskRelation(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        TaskRelation.Declaration.Builder builder = TaskRelation.Declaration.custom();
        builder.id(UUIDUtil.uuid());
        declare(view, "objectId1", builder::objectId1);
        declare(view, "objectType1", builder::objectType1);
        declare(view, "objectId2", builder::objectId2);
        declare(view, "objectType2", builder::objectType2);
        declare(view, "relationType", builder::relationType);
        declare(view, "createdBy", builder::createdBy);
        declare(view, "createdAt", builder::createdAt);
        return builder.build();
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
     * 从 HTTP 请求中解析任务关联的过滤器。
     *
     * @param httpRequest 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @return 表示解析到的过滤器的 {@link TaskRelation.Filter}。
     */
    public static TaskRelation.Filter filterOfTaskRelation(HttpClassicServerRequest httpRequest) {
        TaskRelation.Filter.Builder builder = TaskRelation.Filter.custom();
        builder.ids(httpRequest.queries().all("id"));
        builder.objectId1s(httpRequest.queries().all("objectId1"));
        builder.objectId2s(httpRequest.queries().all("objectId2"));
        return builder.build();
    }

    private static void fillTraceInfo(Map<String, Object> view, DomainObject domain) {
        put(view, "creator", domain.creator());
        put(view, "creationTime", Dates.toString(domain.creationTime()));
        put(view, "lastModifier", domain.lastModifier());
        put(view, "lastModificationTime", Dates.toString(domain.lastModificationTime()));
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
