/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.RangeInfo;
import com.huawei.fit.jane.RangeResultInfo;
import com.huawei.fit.jane.RangedResultSetInfo;
import com.huawei.fit.jane.task.SourcedTaskInstanceService;
import com.huawei.fit.jane.task.TaskInfo;
import com.huawei.fit.jane.task.TaskInstanceFilterInfo;
import com.huawei.fit.jane.task.TaskInstanceInfo;
import com.huawei.fit.jane.task.TaskPropertyInfo;
import com.huawei.fit.jane.task.TaskSourceInfo;
import com.huawei.fit.jane.task.TaskTypeInfo;
import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.PropertyScope;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jober.taskcenter.domain.RefreshInTimeSourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

@DisplayName("测试 RefreshInTimeTaskInstanceRepo")
@Disabled
class RefreshInTimeTaskInstanceRepoTest {
    private static final String OPERATOR = "admin";

    private static final String TYPE_ID = "34f08c7ded6846e088683af396b421e7";

    private static final String SOURCE_ID = "811637cdcb3e405a84bdda1c5ab24a90";

    private static final Map<String, Object> INFO = MapBuilder.<String, Object>get().put("name", "demo").build();

    private static final Map<String, Object> METADATA =
            MapBuilder.<String, Object>get().put("project", "hello").build();

    private static final String CREATE_FITABLE_ID = "abc4077107e94aa0bfa5fc9e4ae1705e";

    private static final String PATCH_FITABLE_ID = "4ec1a1c507eb4feaad6b7e4554aa889b";

    private static final String DELETE_FITABLE_ID = "e48be18cb9104b2d99659bc979b95643";

    private static final String RETRIEVE_FITABLE_ID = "fc4e792fc25d488ba593f24042288697";

    private static final String LIST_FITABLE_ID = "f6409c3af2104057a3e04ebc41b1d9bf";

    private static final TaskEntity TASK_ENTITY;

    private static final TaskInstanceInfo TASK_INSTANCE_INFO;

    private static final TaskInstance.Filter FILTER;

    private static final OperationContext CONTEXT =
            OperationContext.custom().operator(OPERATOR).operatorIp("localhost").tenantId("public").build();

    private static final TaskInstance.Declaration DECLARATION;

    static {
        RefreshInTimeSourceEntity sourceEntity = new RefreshInTimeSourceEntity();
        sourceEntity.setId(SOURCE_ID);
        sourceEntity.setName("demo-source");
        sourceEntity.setType(SourceType.REFRESH_IN_TIME);
        sourceEntity.setApp("demo-app");
        sourceEntity.setEvents(Collections.emptyList());
        sourceEntity.setTriggers(Collections.emptyList());
        sourceEntity.setMetadata(METADATA);
        sourceEntity.setCreateFitableId(CREATE_FITABLE_ID);
        sourceEntity.setPatchFitableId(PATCH_FITABLE_ID);
        sourceEntity.setDeleteFitableId(DELETE_FITABLE_ID);
        sourceEntity.setRetrieveFitableId(RETRIEVE_FITABLE_ID);
        sourceEntity.setListFitableId(LIST_FITABLE_ID);

        TaskType type = TaskType.custom()
                .id(TYPE_ID)
                .name("demo-type")
                .parentId(null)
                .children(Collections.emptyList())
                .sources(Collections.singletonList(sourceEntity))
                .build();

        TASK_ENTITY = new TaskEntity();
        TASK_ENTITY.setId("05e1afb9bdeb423a8ef6985de226e584");
        TASK_ENTITY.setName("demo-task");
        TASK_ENTITY.setProperties(Arrays.asList(property("id", PropertyDataType.TEXT, 1),
                property("name", PropertyDataType.TEXT, 2),
                property("birthday", PropertyDataType.DATETIME, 1)));
        TASK_ENTITY.setAttributes(Collections.emptyMap());
        TASK_ENTITY.setCategoryTriggers(Collections.emptyList());
        TASK_ENTITY.setCreator(OPERATOR);
        TASK_ENTITY.setCreationTime(LocalDateTime.now());
        TASK_ENTITY.setLastModifier(OPERATOR);
        TASK_ENTITY.setLastModificationTime(LocalDateTime.now());
        TASK_ENTITY.setTypes(Collections.singletonList(type));

        DECLARATION = TaskInstance.Declaration.custom()
                .type(TYPE_ID)
                .source(SOURCE_ID)
                .info(INFO)
                .tags(Collections.emptyList())
                .build();

        TASK_INSTANCE_INFO = new TaskInstanceInfo();
        TASK_INSTANCE_INFO.setId("urn:demos:123");
        TASK_INSTANCE_INFO.setTypeId(TYPE_ID);
        TASK_INSTANCE_INFO.setSourceId(SOURCE_ID);
        TASK_INSTANCE_INFO.setInfo(MapBuilder.<String, String>get()
                .put("id", "123")
                .put("name", "demo")
                .put("birthday", "2023-11-23 09:12:23")
                .build());
        TASK_INSTANCE_INFO.setTags(Collections.emptyList());
        TASK_INSTANCE_INFO.setCategories(Collections.emptyList());

        FILTER = TaskInstance.Filter.custom()
                .infos(Collections.singletonMap("name", Collections.singletonList("demo")))
                .categories(Collections.singletonList("已完成"))
                .build();
    }

    private static TaskProperty property(String name, PropertyDataType dataType, int sequence) {
        return TaskProperty.custom()
                .id(UUID.randomUUID().toString().replace("-", ""))
                .name(name)
                .dataType(dataType)
                .sequence(sequence)
                .description(StringUtils.EMPTY)
                .isRequired(false)
                .isIdentifiable(false)
                .scope(PropertyScope.PUBLIC)
                .appearance(Collections.emptyMap())
                .categories(Collections.emptyList())
                .creator(OPERATOR)
                .creationTime(LocalDateTime.now())
                .lastModifier(OPERATOR)
                .lastModificationTime(LocalDateTime.now())
                .build();
    }

    private Router router;

    private Invoker invoker;

    private BrokerClient brokerClient;

    private RefreshInTimeTaskInstanceRepo repo;

    @BeforeEach
    void setup() {
        this.router = mock(Router.class);
        this.invoker = mock(Invoker.class);
        this.brokerClient = mock(BrokerClient.class);
        when(this.brokerClient.getRouter(any(), anyString())).thenReturn(this.router);
        when(this.router.route(any())).thenReturn(this.invoker);
        this.repo = new RefreshInTimeTaskInstanceRepo(this.brokerClient, TASK_ENTITY);
    }

    @Test
    @DisplayName("创建任务实例时，调用创建任务实例的服务实现")
    void should_invoke_create_fitable() throws Throwable {
        when(this.invoker.invoke(any())).thenReturn(TASK_INSTANCE_INFO);
        TaskInstance instance = this.repo.create(DECLARATION, CONTEXT);
        assertGenericableId(this.brokerClient, "ddaa2216ed8a4366af8fa6cf6e8bacf9");
        assertFitableId(this.router, CREATE_FITABLE_ID);
        verify(this.invoker, times(1)).invoke(argThat(RefreshInTimeTaskInstanceRepoTest::checkTaskSource),
                argThat(RefreshInTimeTaskInstanceRepoTest::checkInfo),
                argThat(RefreshInTimeTaskInstanceRepoTest::checkOperationContext));
        assertTaskInstance(instance);
    }

    @Test
    @DisplayName("修改任务实例时，调用修改任务实例服务实现")
    void should_invoke_patch_fitable() throws Throwable {
        when(this.invoker.invoke(any())).thenReturn(null);
        this.repo.patch("urn:demos:123", DECLARATION, CONTEXT);
        assertGenericableId(this.brokerClient, "314757dfb09e47c4b613f98cd086cb25");
        assertFitableId(this.router, PATCH_FITABLE_ID);
        verify(this.invoker, times(1)).invoke(argThat(RefreshInTimeTaskInstanceRepoTest::checkTaskSource),
                argThat(arg -> Objects.equals("urn:demos:123", arg)),
                argThat(RefreshInTimeTaskInstanceRepoTest::checkInfo),
                argThat(RefreshInTimeTaskInstanceRepoTest::checkOperationContext));
    }

    @Test
    @DisplayName("删除任务实例时，调用删除任务实例的服务实现")
    void should_invoke_delete_fitable() throws Throwable {
        final String instanceId = "urn:demos:123";
        when(this.invoker.invoke(any())).thenReturn(null);
        this.repo.delete(instanceId, CONTEXT);
        assertGenericableId(this.brokerClient, "667bc18d3528473c8510b34829c80ce9");
        assertFitableId(this.router, DELETE_FITABLE_ID);
        verify(this.invoker, times(1)).invoke(argThat(RefreshInTimeTaskInstanceRepoTest::checkTaskSource),
                eq(instanceId),
                argThat(RefreshInTimeTaskInstanceRepoTest::checkOperationContext));
    }

    @Test
    @DisplayName("检索任务实例时，调用检索任务实例的服务实现")
    void should_invoke_retrieve_fitable() throws Throwable {
        final String instanceId = "urn:demos:123";
        when(this.invoker.invoke(any())).thenReturn(TASK_INSTANCE_INFO);
        TaskInstance instance = this.repo.retrieve(instanceId, CONTEXT);
        assertGenericableId(this.brokerClient, "fefe9bc6358642a4ac997832db549920");
        assertFitableId(this.router, RETRIEVE_FITABLE_ID);
        verify(this.invoker, times(1)).invoke(argThat(RefreshInTimeTaskInstanceRepoTest::checkTaskSource),
                eq(instanceId),
                argThat(RefreshInTimeTaskInstanceRepoTest::checkOperationContext));
        assertTaskInstance(instance);
    }

    @Test
    @DisplayName("查询任务实例时，调用查询任务实例的服务实现")
    void should_invoke_list_fitable() throws Throwable {
        RangedResultSetInfo<TaskInstanceInfo> results = new RangedResultSetInfo<>();
        results.setResults(Collections.singletonList(TASK_INSTANCE_INFO));
        results.setRange(new RangeResultInfo(0L, 1, 1L));
        when(this.invoker.invoke(any())).thenReturn(results);

        PagedResultSet<TaskInstance> instances = this.repo.list(FILTER, 0L, 1, CONTEXT);
        assertGenericableId(this.brokerClient, "805d46f4137e41909d81a7e469e2534a");
        assertFitableId(this.router, LIST_FITABLE_ID);
        verify(this.invoker, times(1)).invoke(argThat(RefreshInTimeTaskInstanceRepoTest::checkTaskSource),
                argThat(RefreshInTimeTaskInstanceRepoTest::checkInstanceFilter),
                argThat(arg -> Objects.equals(new RangeInfo(0L, 1), arg)),
                argThat(RefreshInTimeTaskInstanceRepoTest::checkOperationContext));
        assertEquals(1, instances.results().size());
        assertTaskInstance(instances.results().get(0));
    }

    private static void assertGenericableId(BrokerClient client, String genericableId) {
        verify(client, times(1)).getRouter(eq(SourcedTaskInstanceService.class), eq(genericableId));
    }

    private static void assertFitableId(Router router, String fitableId) {
        verify(router, times(1)).route(argThat(filter -> {
            if (!(filter instanceof FitableIdFilter)) {
                return false;
            }
            FitableIdFilter actual = (FitableIdFilter) filter;
            List<String> fitableIds;
            try {
                Field field = FitableIdFilter.class.getDeclaredField("fitableIds");
                field.setAccessible(true);
                fitableIds = cast(field.get(actual));
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                return false;
            }
            return fitableIds.size() == 1 && Objects.equals(fitableIds.get(0), fitableId);
        }));
    }

    private static boolean checkTaskSource(Object arg) {
        if (!(arg instanceof TaskSourceInfo)) {
            return false;
        }
        TaskSourceInfo source = (TaskSourceInfo) arg;
        if (!Objects.equals(source.getTypeId(), TYPE_ID)) {
            return false;
        }
        Map<String, Object> metadata = source.getMetadata();
        if (metadata.size() != 1 || !Objects.equals(metadata.get("project"), "hello")) {
            return false;
        }
        return checkTask(source.getOwningTask());
    }

    private static <I, E, K> boolean checkList(List<I> infos, List<E> entities, Function<I, K> infoKeyMapper,
            Function<E, K> entityKeyMapper, BiPredicate<I, E> predicate) {
        if (infos.size() != entities.size()) {
            return false;
        }
        Map<K, E> indexedEntities = entities.stream().collect(Collectors.toMap(entityKeyMapper, Function.identity()));
        for (I info : infos) {
            K key = infoKeyMapper.apply(info);
            E entity = indexedEntities.get(key);
            if (entity == null || !predicate.test(info, entity)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkTask(TaskInfo info) {
        return Objects.equals(info.getId(), RefreshInTimeTaskInstanceRepoTest.TASK_ENTITY.getId()) && Objects.equals(
                info.getName(),
                RefreshInTimeTaskInstanceRepoTest.TASK_ENTITY.getName()) && checkProperties(info.getProperties(),
                RefreshInTimeTaskInstanceRepoTest.TASK_ENTITY.getProperties()) && checkTypes(info.getTypes(),
                RefreshInTimeTaskInstanceRepoTest.TASK_ENTITY.getTypes());
    }

    private static boolean checkProperty(TaskPropertyInfo info, TaskProperty entity) {
        return Objects.equals(info.getId(), entity.id()) && Objects.equals(info.getName(), entity.name())
                && Objects.equals(info.getDescription(), entity.description()) && Objects.equals(info.getDataType(),
                Enums.toString(entity.dataType())) && Objects.equals(info.getRequired(), entity.required())
                && Objects.equals(info.getIdentifiable(), entity.identifiable()) && Objects.equals(info.getScope(),
                Enums.toString(entity.scope()));
    }

    private static boolean checkProperties(List<TaskPropertyInfo> infos, List<TaskProperty> entities) {
        return checkList(infos,
                entities,
                TaskPropertyInfo::getId,
                TaskProperty::id,
                RefreshInTimeTaskInstanceRepoTest::checkProperty);
    }

    private static boolean checkTypes(List<TaskTypeInfo> infos, List<TaskType> entities) {
        return checkList(infos,
                entities,
                TaskTypeInfo::getId,
                TaskType::id,
                RefreshInTimeTaskInstanceRepoTest::checkType);
    }

    private static boolean checkType(TaskTypeInfo info, TaskType entity) {
        return Objects.equals(info.getId(), entity.id()) && Objects.equals(info.getName(), entity.name()) && checkTypes(
                info.getChildren(),
                entity.children());
    }

    private static boolean checkInfo(Object arg) {
        if (!(arg instanceof Map)) {
            return false;
        }
        Map<String, ?> info = cast(arg);
        if (info.size() != INFO.size()) {
            return false;
        }
        for (Map.Entry<String, ?> entry : info.entrySet()) {
            Object actual = entry.getValue();
            Object expected = INFO.get(entry.getKey());
            if (!Objects.equals(expected, actual)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkOperationContext(Object arg) {
        if (!(arg instanceof com.huawei.fit.jober.entity.OperationContext)) {
            return false;
        }
        com.huawei.fit.jober.entity.OperationContext context = cast(arg);
        return Objects.equals(context.getOperator(), CONTEXT.operator()) && Objects.equals(context.getOperatorIp(),
                CONTEXT.operatorIp()) && Objects.equals(context.getTenantId(), CONTEXT.tenantId()) && Objects.equals(
                context.getSourcePlatform(),
                CONTEXT.sourcePlatform());
    }

    private static void assertTaskInstance(TaskInstance instance) {
        assertEquals("urn:demos:123", instance.id());
        assertEquals(TYPE_ID, instance.type().id());
        assertEquals(SOURCE_ID, instance.source().getId());
        assertTrue(instance.tags().isEmpty());
        assertTrue(instance.categories().isEmpty());
        assertEquals(3, TASK_INSTANCE_INFO.getInfo().size());
        assertEquals("123", TASK_INSTANCE_INFO.getInfo().get("id"));
        assertEquals("demo", TASK_INSTANCE_INFO.getInfo().get("name"));
        assertEquals(LocalDateTime.of(2023, 11, 23, 9, 12, 23), instance.info().get("birthday"));
    }

    private static boolean checkInstanceFilter(Object arg) {
        if (!(arg instanceof TaskInstanceFilterInfo)) {
            return false;
        }
        TaskInstanceFilterInfo filter = cast(arg);
        return filter.getInfos() == FILTER.infos() && filter.getCategories() == FILTER.categories();
    }
}