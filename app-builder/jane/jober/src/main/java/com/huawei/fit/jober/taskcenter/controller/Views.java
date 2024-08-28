/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

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
import com.huawei.fit.jober.common.utils.UuidUtil;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.jober.taskcenter.declaration.InstanceDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.InstanceEventDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TaskCategoryTriggerDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TenantDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TriggerDeclaration;
import com.huawei.fit.jober.taskcenter.domain.HierarchicalTaskInstance;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.InstanceEvent;
import com.huawei.fit.jober.taskcenter.domain.NodeEntity;
import com.huawei.fit.jober.taskcenter.domain.OperationRecordEntity;
import com.huawei.fit.jober.taskcenter.domain.RefreshInTimeSourceEntity;
import com.huawei.fit.jober.taskcenter.domain.ScheduleSourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskCategoryTriggerEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskInstanceCount;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.domain.TreeEntity;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;
import com.huawei.fit.jober.taskcenter.domain.portal.TaskNode;
import com.huawei.fit.jober.taskcenter.filter.OperationRecordFilter;
import com.huawei.fit.jober.taskcenter.filter.TaskFilter;
import com.huawei.fit.jober.taskcenter.filter.TaskTemplateFilter;
import com.huawei.fit.jober.taskcenter.filter.TriggerFilter;
import com.huawei.fit.jober.taskcenter.service.PortalService;
import com.huawei.fit.jober.taskcenter.util.Enums;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangeResult;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为视图提供工具方法。
 *
 * @author 陈镕希
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
        List<Object> list = ObjectUtils.<List<Object>>cast(view.get(key));
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

    /**
     * 从视图中解析任务属性触发器的声明信息。
     *
     * @param view 表示包含声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从视图中解析到的任务属性触发器的 {@link TriggerDeclaration}。
     */
    public static TriggerDeclaration declareTrigger(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        TriggerDeclaration declaration = new TriggerDeclaration();
        declaration.setPropertyName(valueOf(view, "propertyName"));
        declaration.setFitableId(valueOf(view, "fitableId"));
        return declaration;
    }

    /**
     * 从视图中解析任务实例的声明信息。
     *
     * @param view 表示包含声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param task 任务对象
     * @return 表示从视图中解析到的任务实例的 {@link InstanceDeclaration}。
     */
    public static TaskInstance.Declaration declareInstance(Map<String, Object> view, TaskEntity task) {
        if (view == null) {
            return null;
        }
        TaskInstance.Declaration.Builder builder = TaskInstance.Declaration.custom();
        declare(view, "typeId", builder::type);
        declare(view, "sourceId", builder::source);
        declare(view, "info", builder::info);
        declare(view, "tags", builder::tags);
        return builder.build();
    }

    /**
     * 从视图中解析任务数据源的声明信息。
     *
     * @param view 表示包含声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从视图中解析到的任务数据源的 {@link SourceDeclaration}。
     */
    public static SourceDeclaration declareSource(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        SourceDeclaration declaration = new SourceDeclaration();
        declaration.setName(valueOf(view, "name"));
        declaration.setApp(valueOf(view, "app"));
        declaration.setType(valueOf(view, "type"));
        declaration.setTriggers(listOf(view, "triggers", Views::declareTrigger));
        declaration.setFitableId(valueOf(view, "fitableId"));
        declaration.setInterval(valueOf(view, "interval"));
        declaration.setFilter(valueOf(view, "filter"));
        declaration.setEvents(listOf(view, "events", Views::declareInstanceEvent));
        declaration.setMetadata(valueOf(view, "metadata"));
        declaration.setCreateFitableId(valueOf(view, "createFitableId"));
        declaration.setPatchFitableId(valueOf(view, "patchFitableId"));
        declaration.setDeleteFitableId(valueOf(view, "deleteFitableId"));
        declaration.setRetrieveFitableId(valueOf(view, "retrieveFitableId"));
        declaration.setListFitableId(valueOf(view, "listFitableId"));
        return declaration;
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
        com.huawei.fit.jane.task.domain.TaskProperty.Declaration.Builder builder =
                com.huawei.fit.jane.task.domain.TaskProperty.Declaration.custom();
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

    /**
     * 从视图中解析任务定义的声明信息。
     *
     * @param view 表示包含声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从视图中解析到的任务定义的 {@link TaskDeclaration}。
     */
    public static TaskDeclaration declareTask(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        TaskDeclaration declaration = new TaskDeclaration();
        declaration.setName(valueOf(view, "name"));
        declaration.setCategory(valueOf(view, "category"));
        declaration.setTemplateId(valueOf(view, "templateId"));
        declaration.setAttributes(valueOf(view, "attributes"));
        declaration.setProperties(listOf(view, "properties", Views::declareProperty));
        declaration.setCategoryTriggers(listOf(view, "categoryTriggers", Views::declareTaskCategoryTrigger));
        declaration.setIndexes(listOf(view, "indexes", Views::declareIndex));
        return declaration;
    }

    private static TaskCategoryTriggerDeclaration declareTaskCategoryTrigger(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        TaskCategoryTriggerDeclaration declaration = new TaskCategoryTriggerDeclaration();
        declaration.setCategory(valueOf(view, "category"));
        declaration.setFitableIds(valueOf(view, "fitableIds"));
        return declaration;
    }

    /**
     * 从视图中解析租户的声明信息。
     *
     * @param view 表示包含租户声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从视图中解析到的租户声明的 {@link TenantDeclaration}。
     */
    public static Tenant.Declaration declareTenant(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        Tenant.Declaration.Builder builder = Tenant.Declaration.custom();
        declare(view, "name", builder::name);
        declare(view, "description", builder::description);
        declare(view, "avatarId", builder::avatarId);
        declare(view, "members", builder::members);
        declare(view, "tags", builder::tags);
        if (view.get("accessLevel") != null) {
            TenantAccessLevel tenantAccessLevel =
                    Enums.parse(TenantAccessLevel.class, String.valueOf(view.get("accessLevel")));
            builder.accessLevel(tenantAccessLevel);
        }
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

    /**
     * 新增数据
     *
     * @param view map数据
     * @param key 键
     * @param value 值
     * @param mapper 转换器
     * @param <T> 源类型
     * @param <R> 目的类型
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
     * 获取一个视图，表示指定的任务属性触发器。
     *
     * @param trigger 表示待获取视图的任务属性触发器的 {@link TriggerEntity}。
     * @return 表示该任务属性触发器的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(TriggerEntity trigger) {
        if (trigger == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(3);
        put(view, "id", trigger.getId());
        put(view, "propertyId", trigger.getPropertyId());
        put(view, "fitableId", trigger.getFitableId());
        return view;
    }

    /**
     * 获取一个视图，表示指定的任务数据源。
     *
     * @param source 表示待获取视图的任务数据源的 {@link SourceEntity}。
     * @return 表示该任务属性触发器的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(SourceEntity source) {
        if (source == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(8);
        put(view, "id", source.getId());
        put(view, "name", source.getName());
        put(view, "app", source.getApp());
        put(view, "type", Enums.toString(source.getType()));
        putList(view, "triggers", source.getTriggers(), Views::viewOf);
        putList(view, "events", source.getEvents(), Views::viewOf);
        if (source instanceof ScheduleSourceEntity) {
            ScheduleSourceEntity schedule = (ScheduleSourceEntity) source;
            put(view, "fitableId", schedule.getFitableId());
            put(view, "interval", schedule.getInterval());
            put(view, "filter", schedule.getFilter());
        }
        if (source instanceof RefreshInTimeSourceEntity) {
            RefreshInTimeSourceEntity actual = (RefreshInTimeSourceEntity) source;
            put(view, "metadata", actual.getMetadata());
            put(view, "createFitableId", actual.getCreateFitableId());
            put(view, "patchFitableId", actual.getPatchFitableId());
            put(view, "deleteFitableId", actual.getDeleteFitableId());
            put(view, "retrieveFitableId", actual.getRetrieveFitableId());
            put(view, "listFitableId", actual.getListFitableId());
        }
        return view;
    }

    /**
     * 获取一个视图，表示指定的任务实例。
     *
     * @param instance 表示待获取视图的任务实例的 {@link TaskInstance}。
     * @return 表示该任务实例的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(TaskInstance instance) {
        if (instance == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(8);
        put(view, "id", instance.id());
        put(view, "typeId", instance.type(), TaskType::id);
        put(view, "sourceId", instance.source(), SourceEntity::getId);
        put(view, "info", instance.info());
        putList(view, "tags", instance.tags());
        putList(view, "categories", instance.categories());
        put(view, "taskId", instance.task().getId());
        put(view, "type", instance.type(), TaskType::name);
        if (instance instanceof HierarchicalTaskInstance) {
            HierarchicalTaskInstance hierarchical = (HierarchicalTaskInstance) instance;
            putList(view, "children", hierarchical.children(), Views::viewOf);
            put(view, "sourceOrigin", Optional.ofNullable(instance.source()).map(SourceEntity::getApp).orElse("Jane"));
        }
        return view;
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
     * 获取一个视图，表示指定的任务定义。
     *
     * @param task 表示待获取视图的任务定义的 {@link TaskEntity}。
     * @return 表示该任务属性触发器的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(TaskEntity task) {
        if (task == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>();
        put(view, "id", task.getId());
        put(view, "name", task.getName());
        put(view, "tenantId", task.getTenantId());
        put(view, "templateId", task.getTemplateId());
        put(view, "category", task.getCategory());
        putList(view, "properties", task.getProperties(), Views::viewOf);
        putList(view, "sources", task.getSources(), Views::viewOf);
        putList(view, "types", task.getTypes(), Views::viewOf);
        putList(view, "categoryTriggers", task.getCategoryTriggers(), Views::viewOf);
        put(view, "attributes", task.getAttributes());
        put(view, "creator", task.getCreator());
        put(view, "creationTime", Dates.toString(task.getCreationTime()));
        put(view, "lastModifier", task.getLastModifier());
        put(view, "lastModificationTime", Dates.toString(task.getLastModificationTime()));
        return view;
    }

    /**
     * viewOf
     *
     * @param type type
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(TaskType type) {
        if (type == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>();
        put(view, "id", type.id());
        put(view, "name", type.name());
        put(view, "parentId", type.parentId());
        putList(view, "sources", type.sources(), Views::viewOf);
        putList(view, "children", type.children(), Views::viewOf);
        put(view, "creator", type.creator());
        put(view, "creationTime", type.creationTime());
        put(view, "lastModifier", type.lastModifier());
        put(view, "lastModificationTime", type.lastModificationTime());
        return view;
    }

    private static Map<String, Object> viewOf(TaskCategoryTriggerEntity trigger) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        put(view, "category", trigger.getCategory());
        putList(view, "fitableIds", trigger.getFitableIds());
        return view;
    }

    /**
     * 获取一个视图，表示指定的任务树。
     *
     * @param tree 表示待获取视图的任务树的 {@link TreeEntity}。
     * @return 表示该任务树的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(TreeEntity tree) {
        if (tree == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(7);
        put(view, "id", tree.getId());
        put(view, "name", tree.getName());
        put(view, "taskId", tree.getTaskId());
        put(view, "childCount", tree.getChildCount());
        put(view, "creator", tree.getCreator());
        put(view, "creationTime", Dates.toString(tree.getCreationTime()));
        put(view, "lastModifier", tree.getLastModifier());
        put(view, "lastModificationTime", Dates.toString(tree.getLastModificationTime()));
        return view;
    }

    /**
     * 获取一个视图，表示指定的任务树节点。
     *
     * @param node 表示待获取视图的任务树节点的 {@link NodeEntity}。
     * @return 表示该任务树节点的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(NodeEntity node) {
        Map<String, Object> view = new LinkedHashMap<>(8);
        put(view, "id", node.getId());
        put(view, "name", node.getName());
        put(view, "parentId", node.getParentId());
        put(view, "childCount", node.getChildCount());
        putList(view, "sourceIds", node.getSourceIds());
        put(view, "creator", node.getCreator());
        put(view, "creationTime", Dates.toString(node.getCreationTime()));
        put(view, "lastModifier", node.getLastModifier());
        put(view, "lastModificationTime", Dates.toString(node.getLastModificationTime()));
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
     * 定义list
     *
     * @param list 列表数据
     * @param <T> 泛型类型
     * @return 列表
     */
    public static <T> UndefinableValue<List<T>> defineList(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return UndefinableValue.undefined();
        } else {
            return UndefinableValue.defined(decodeList(list));
        }
    }

    /**
     * 从 HTTP 请求中解析任务定义的过滤器。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @return 表示解析到的过滤器的 {@link TaskFilter}。
     */
    public static TaskFilter filterOfTasks(HttpClassicServerRequest request) {
        TaskFilter filter = new TaskFilter();
        filter.setIds(defineList(request.queries().all("id")));
        filter.setNames(defineList(request.queries().all("name")));
        filter.setTemplateIds(defineList(request.queries().all("templateId")));
        filter.setCategories(defineList(request.queries().all("category")));
        filter.setCreators(defineList(request.queries().all("creator")));
        filter.setOrderBys(defineList(request.queries().all("orderBy")));
        return filter;
    }

    /**
     * 从 HTTP 请求中解析任务属性触发器的过滤器。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @return 表示解析到的过滤器的 {@link TriggerFilter}。
     */
    public static TriggerFilter filterOfTriggers(HttpClassicServerRequest request) {
        TriggerFilter filter = new TriggerFilter();
        filter.setIds(defineList(request.queries().all("id")));
        filter.setSourceIds(defineList(request.queries().all("sourceId")));
        filter.setPropertyIds(defineList(request.queries().all("propertyId")));
        filter.setFitableIds(defineList(request.queries().all("fitableId")));
        return filter;
    }

    /**
     * 获取一个视图，表示指定的实体列表。
     *
     * @param triggers triggers
     * @return 实体列表
     */
    public static Map<String, Object> viewOf(Map<String, List<TriggerEntity>> triggers) {
        if (triggers == null) {
            return new LinkedHashMap<>();
        }
        return triggers.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> viewOf(entry.getValue(), Views::viewOf)));
    }

    /**
     * 从 HTTP 请求中解析任务实例的过滤器。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @param isDeleted 是否删除了
     * @return 表示解析到的过滤器的 {@link TaskInstance.Filter}。
     */
    public static TaskInstance.Filter filterOfInstances(HttpClassicServerRequest request, boolean isDeleted) {
        final String infoPrefix = "info.";
        Map<String, List<String>> infoFilters = new LinkedHashMap<>();
        for (String key : request.queries().keys()) {
            if (StringUtils.startsWithIgnoreCase(key, infoPrefix)) {
                String actualKey = key.substring(infoPrefix.length());
                List<String> values = request.queries().all(key);
                infoFilters.put(actualKey, values);
            }
        }
        return TaskInstance.Filter.custom()
                .ids(request.queries().all("id"))
                .typeIds(request.queries().all("typeId"))
                .sourceIds(request.queries().all("sourceId"))
                .infos(infoFilters)
                .tags(request.queries().all("tag"))
                .categories(request.queries().all("category"))
                .deleted(isDeleted)
                .build();
    }

    private static <T> List<T> decodeList(List<T> list) {
        return list.stream().map(param -> {
            if (param instanceof String) {
                try {
                    return ObjectUtils.<T>cast(URLDecoder.decode((String) param, StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    log.warn("unsupported encoding for string {}", param);
                    return null;
                }
            }
            return param;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * viewOf
     *
     * @param count count
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(TaskInstanceCount count) {
        if (count == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(3);
        view.put("taskId", count.getTaskId());
        view.put("taskName", count.getTaskName());
        view.put("count", count.getCount());
        return view;
    }

    /**
     * viewOf
     *
     * @param counts counts
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(List<TaskInstanceCount> counts) {
        Map<String, Object> view = new LinkedHashMap<>();
        List<Object> details = new LinkedList<>();
        long total = 0L;
        for (TaskInstanceCount count : counts) {
            details.add(viewOf(count));
            total += count.getCount();
        }
        view.put("total", total);
        view.put("details", details);
        return view;
    }

    /**
     * viewOf
     *
     * @param pagination pagination
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(PortalService.Pagination pagination) {
        Map<String, Object> view = new LinkedHashMap<>(3);
        put(view, "offset", pagination.getOffset());
        put(view, "limit", pagination.getLimit());
        put(view, "total", pagination.getTotal());
        return view;
    }

    /**
     * declareInstanceEvent
     *
     * @param view view
     * @return InstanceEventDeclaration
     */
    public static InstanceEventDeclaration declareInstanceEvent(Map<String, Object> view) {
        if (view == null) {
            return null;
        }
        InstanceEventDeclaration.Builder builder = InstanceEventDeclaration.custom();
        ifContainsKey(view, "type", builder::type);
        ifContainsKey(view, "fitableId", builder::fitableId);
        return builder.build();
    }

    private static <T> void ifContainsKey(Map<String, Object> view, String key, Consumer<T> consumer) {
        if (view.containsKey(key)) {
            T value = cast(view.get(key));
            consumer.accept(value);
        }
    }

    /**
     * viewOf
     *
     * @param event event
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(InstanceEvent event) {
        if (event == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(2);
        put(view, "type", Enums.toString(event.type()));
        put(view, "fitableId", event.fitableId());
        return view;
    }

    /**
     * declareTaskType
     *
     * @param view view
     * @return TaskType.Declaration
     */
    public static TaskType.Declaration declareTaskType(Map<String, Object> view) {
        TaskType.Declaration.Builder builder = TaskType.Declaration.custom();
        declare(view, "name", builder::name);
        declare(view, "parentId", builder::parentId);
        declare(view, "sourceIds", builder::sourceIds);
        return builder.build();
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
     * viewOf
     *
     * @param node node
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(TaskNode node) {
        Map<String, Object> view = new LinkedHashMap<>();
        if (node == null) {
            return view;
        }
        put(view, "id", node.id());
        put(view, "name", node.name());
        put(view, "type", Enums.toString(node.type()));
        putList(view, "children", node.children(), Views::viewOf);
        return view;
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
        consumeQueryArgumentsIfExists(request,
                "accessLevel",
                (accessLevels) -> builder.accessLevels(accessLevels.stream()
                        .map(TenantAccessLevel::valueOf)
                        .collect(Collectors.toList())));
        return builder.build();
    }

    /**
     * 从 HTTP 请求中解析租户成员的过滤器。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 租户id
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

    /**
     * 通过{@link GetPageResponse}构建视图
     *
     * @param getPageResponse 分页查询流程定义列表response
     * @return 视图
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
     * 通过视图构造认证
     *
     * @param view 视图
     * @return 认证
     */
    public static Authorization.Declaration declareAuthorization(Map<String, Object> view) {
        Authorization.Declaration.Builder builder = Authorization.Declaration.custom();
        declare(view, "system", builder::system);
        declare(view, "user", builder::user);
        declare(view, "token", builder::token);
        declare(view, "expiration", (Number value) -> builder.expiration(nullIf(value, 0L).longValue()));
        return builder.build();
    }

    /**
     * 返回视图
     *
     * @param authorization 认证信息
     * @return 视图
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
     * 构建认证过滤信息
     *
     * @param httpRequest http请求
     * @return 过滤信息
     */
    public static Authorization.Filter filterOfAuthorization(HttpClassicServerRequest httpRequest) {
        Authorization.Filter.Builder builder = Authorization.Filter.custom();
        builder.ids(httpRequest.queries().all("id"));
        builder.systems(httpRequest.queries().all("system"));
        builder.users(httpRequest.queries().all("user"));
        return builder.build();
    }

    /**
     * 从视图中解析任务模板的声明信息。
     *
     * @param view 表示包含声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从视图中解析到的任务模板的 {@link TaskTemplate.Declaration}。
     */
    public static TaskTemplate.Declaration declareTaskTemplate(Map<String, Object> view) {
        if (Objects.isNull(view)) {
            return null;
        }
        TaskTemplate.Declaration.Builder builder = TaskTemplate.Declaration.custom();
        declare(view, "properties", Views::declareTaskTemplateProperties, builder::properties);
        declare(view, "name", builder::name);
        declare(view, "description", builder::description);
        declare(view, "parentTemplateId", builder::parentTemplateId);
        return builder.build();
    }

    private static List<TaskTemplateProperty.Declaration> declareTaskTemplateProperties(
            List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(Views::declareTaskTemplateProperty)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 从视图中解析任务模板属性的声明信息。
     *
     * @param view 表示包含声明信息的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从视图中解析到的任务模板属性的 {@link TaskTemplate.Declaration}。
     */
    public static TaskTemplateProperty.Declaration declareTaskTemplateProperty(Map<String, Object> view) {
        if (Objects.isNull(view)) {
            return null;
        }

        TaskTemplateProperty.Declaration.Builder builder = TaskTemplateProperty.Declaration.custom();
        declare(view, "id", builder::id);
        declare(view, "name", builder::name);
        declare(view, "dataType", builder::dataType);
        return builder.build();
    }

    /**
     * 获取一个视图，表示指定的任务模板。
     *
     * @param template 表示待获取视图的任务模板的 {@link TaskTemplate}。
     * @return 表示该任务模板的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(TaskTemplate template) {
        if (template == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>();
        put(view, "id", template.id());
        put(view, "name", template.name());
        putList(view, "properties", template.properties(), Views::viewOf);
        put(view, "description", template.description());
        return view;
    }

    /**
     * 获取一个视图，表示指定的任务模板属性。
     *
     * @param templateProperty 表示待获取视图的任务模板属性的 {@link TaskTemplateProperty}。
     * @return 表示该任务模板属性的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(TaskTemplateProperty templateProperty) {
        if (templateProperty == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>();
        put(view, "id", templateProperty.id());
        put(view, "name", templateProperty.name());
        put(view, "dataType", templateProperty.dataType());
        put(view, "sequence", templateProperty.sequence());
        put(view, "taskTemplateId", templateProperty.taskTemplateId());
        return view;
    }

    /**
     * 从 HTTP 请求中解析任务模板的过滤器。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @return 表示解析到的过滤器的 {@link TaskTemplateFilter}。
     */
    public static TaskTemplateFilter filterOfTaskTemplates(HttpClassicServerRequest request) {
        TaskTemplateFilter filter = new TaskTemplateFilter();
        filter.setIds(defineList(request.queries().all("id")));
        filter.setNames(defineList(request.queries().all("name")));
        return filter;
    }

    /**
     * 从 HTTP 请求中解析操作记录的过滤器。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @return 表示解析到的过滤器的 {@link OperationRecordFilter}。
     */
    public static OperationRecordFilter filterOfOperationRecord(HttpClassicServerRequest request) {
        OperationRecordFilter filter = new OperationRecordFilter();
        filter.setObjectIds(defineList(request.queries().all("object_id")));
        filter.setObjectTypes(defineList(request.queries().all("object_type")));
        return filter;
    }

    /**
     * 获取一个视图，表示指定的任务模板属性。
     *
     * @param operationRecordEntity 表示待获取视图的任务模板属性的 {@link OperationRecordEntity}。
     * @return 表示该任务模板属性的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(OperationRecordEntity operationRecordEntity) {
        if (operationRecordEntity == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>();
        put(view, "id", operationRecordEntity.getId());
        put(view, "objectId", operationRecordEntity.getObjectId());
        put(view, "objectType", operationRecordEntity.getObjectType());
        put(view, "operator", operationRecordEntity.getOperator());
        put(view, "operate", operationRecordEntity.getOperate());
        put(view, "operatedTime", Dates.toString(operationRecordEntity.getOperatedTime()));
        put(view, "instanceTask", operationRecordEntity.getInstanceTask());
        put(view, "title", operationRecordEntity.getTitle());
        put(view, "content", operationRecordEntity.getContent());
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
        builder.id(UuidUtil.uuid());
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

    /**
     * 声明索引。
     *
     * @param view 表示包含索引声明的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示索引声明的 {@link Index.Declaration}。
     */
    public static Index.Declaration declareIndex(Map<String, Object> view) {
        Index.Declaration.Builder builder = Index.Declaration.custom();
        declare(view, "name", builder::name);
        declare(view, "properties", builder::propertyNames);
        return builder.build();
    }

    /**
     * 将索引转为视图。
     *
     * @param index 表示待转为视图的索引的 {@link Index}。
     * @return 表示索引的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> viewOf(Index index) {
        Map<String, Object> view = new LinkedHashMap<>(7);
        put(view, "id", index.id());
        put(view, "name", index.name());
        putList(view, "propertyIds", index.properties().stream().map(DomainObject::id).collect(Collectors.toList()));
        fillTraceInfo(view, index);
        return view;
    }

    private static void fillTraceInfo(Map<String, Object> view, DomainObject domain) {
        put(view, "creator", domain.creator());
        put(view, "creationTime", Dates.toString(domain.creationTime()));
        put(view, "lastModifier", domain.lastModifier());
        put(view, "lastModificationTime", Dates.toString(domain.lastModificationTime()));
    }
}
