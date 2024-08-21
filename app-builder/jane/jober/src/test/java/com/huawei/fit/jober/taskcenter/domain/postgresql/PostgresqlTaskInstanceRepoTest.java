/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fit.jober.Tests.isId;
import static com.huawei.fit.jober.Tests.matchArguments;
import static modelengine.fitframework.util.ObjectUtils.as;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jober.DataService;
import com.huawei.fit.jober.OnInstancesCategoryChanged;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.GoneException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.entity.InstanceCategoryChanged;
import com.huawei.fit.jober.entity.InstanceChanged;
import com.huawei.fit.jober.entity.InstanceMessage;
import com.huawei.fit.jober.taskcenter.domain.CategoryEntity;
import com.huawei.fit.jober.taskcenter.domain.HierarchicalTaskInstance;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskCategoryTriggerEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;
import com.huawei.fit.jober.taskcenter.domain.ViewMode;
import com.huawei.fit.jober.taskcenter.domain.util.AsynchronousRunner;
import com.huawei.fit.jober.taskcenter.domain.util.IndexValueRow;
import com.huawei.fit.jober.taskcenter.domain.util.ListValue;
import com.huawei.fit.jober.taskcenter.domain.util.TaskInstanceRow;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceCreatedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceCreatingEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceDeletedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceModifiedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceModifyingEvent;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.service.TagService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
import com.huawei.fit.jober.taskcenter.validation.InstanceValidator;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.event.Event;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DisplayName("测试基于 Postgresql 的任务实例持久化逻辑")
class PostgresqlTaskInstanceRepoTest {
    private static final String OPERATOR = "admin";

    private static final String TENANT_ID = "f0af8b6abb054ba59afae50e1d12bfdb";

    private static final String TASK_ID = "5ec68f8f29e14df19bbb1097eac992df";

    private static final String INSTANCE_ID = "81fcdf174e8c4607b1b574afafa533eb";

    private static final String CUSTOM_ID = "bb9be258ce464d0ea4fbddc017365201";

    private static final String INSTANCE_FITABLE_ID = "e1ac76c5a49f4be7913b1b5f38dd4124";

    private static final String CATEGORY_FITABLE_ID = "925ff585b34147cc904fd34909fef602";

    private static final TaskEntity TASK;

    private static final OperationContext CONTEXT;

    private static final String SELECT_BY_TEXT_1_SQL = script("instance-select-by-text-1");

    private static final String SELECT_HISTORY_BY_TEXT_1_SQL = script("instance-select-history-by-text-1");

    private static final String SELECT_BY_ID_SQL = script("instance-select-by-id");

    private static final String INSERT_SQL = script("instance-insert");

    private static final String UPDATE_BASIC_SQL = script("instance-update-basic");

    private static final String UPDATE_STATUS_SQL = script("instance-update-status");

    private static final String COUNT_DELETED_BY_ID_SQL = script("instance-count-deleted-by-id");

    private static final String MOVE_HISTORY_SQL = script("instance-move-history");

    private static final String DELETE_BY_ID_SQL = script("instance-delete-by-id");

    private static final String SELECT_IDS_BY_SOURCE_SQL = script("instance-select-ids-by-source");

    private static final String MOVE_HISTORY_BY_SOURCE_SQL = script("instance-move-history-by-source");

    private static final String DELETE_BY_SOURCE_SQL = script("instance-delete-by-source");

    private static final String SELECT_HISTORY_BY_ID_SQL = script("instance-select-history-by-id");

    private static final String LIST_FULL_FILTER_COUNT_SQL = script("instance-count-list-full-filter");

    private static final String LIST_FULL_FILTER_SELECT_SQL = script("instance-select-list-full-filter");

    private static final String TREE_FULL_FILTER_COUNT_SQL = script("instance-count-tree-full-filter");

    private static final String TREE_FULL_FILTER_SELECT_SQL = script("instance-select-tree-full-filter");

    private static final String TREE_SELECT_CHILDREN_SQL = script("instance-select-tree-children");

    private static final String MOVE_HISTORY_BACK_SQL = script("instance-move-history-back");

    private static final String DELETE_HISTORY_BY_ID_SQL = script("instance-delete-history-by-id");

    static {
        TASK = new TaskEntity();
        TASK.setId(TASK_ID);
        TASK.setName("DemoTask");
        TASK.setProperties(Arrays.asList(
                property("p1", "id", PropertyDataType.TEXT, 1, true, true, Collections.emptyMap()),
                property("p2", "status", PropertyDataType.TEXT, 2, true, false, MapBuilder.<String, String>get()
                        .put("ready", "未开始").put("running", "处理中").put("complete", "已完成").build())
        ));
        TASK.setTypes(Arrays.asList(
                type("t1", "type-1", null, MapBuilder.<String, String>get()
                        .put("s11", "source-11").put("s12", "source-12").build(), Collections.emptyList()),
                type("t2", "type-2", null, MapBuilder.<String, String>get()
                        .put("s21", "source-21").put("s22", "source-22").build(), Collections.emptyList())
        ));
        TaskCategoryTriggerEntity categoryTrigger = new TaskCategoryTriggerEntity();
        categoryTrigger.setCategory("已完成");
        categoryTrigger.setFitableIds(Collections.singletonList(CATEGORY_FITABLE_ID));
        TASK.setCategoryTriggers(Collections.singletonList(categoryTrigger));
        SourceEntity source11 = SourceEntity.lookup(TASK.getTypes(), "s11");
        if (source11 == null) {
            throw new IllegalStateException("source 11 not found.");
        }
        source11.setTriggers(Collections.singletonList(new TriggerEntity("trigger1", "p2", INSTANCE_FITABLE_ID)));

        CONTEXT = OperationContext.custom().tenantId(TENANT_ID).operator(OPERATOR).build();
    }

    private static String script(String key) {
        String resourceName = "sql/" + key + ".sql";
        String text;
        try {
            text = IoUtils.content(PostgresqlTaskInstanceRepoTest.class.getClassLoader(), resourceName);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load embedded SQL resource for test: " + resourceName);
        }
        text = text.replace("\r\n", "\n");
        String[] lines = StringUtils.split(text, '\n');
        return Stream.of(lines).map(StringUtils::trim).filter(StringUtils::isNotEmpty).collect(Collectors.joining(" "));
    }

    private PostgresqlTaskInstanceRepo repo;

    private DynamicSqlExecutor executor;

    private InstanceValidator validator;

    private Plugin plugin;

    private TagService tagService;

    private CategoryService categoryService;

    private BrokerClient brokerClient;

    private AsynchronousRunner runner;

    @BeforeEach
    void setup() {
        this.executor = mock(DynamicSqlExecutor.class);
        this.validator = mock(InstanceValidator.class);
        this.plugin = mock(Plugin.class);
        this.tagService = mock(TagService.class);
        this.categoryService = mock(CategoryService.class);
        this.brokerClient = mock(BrokerClient.class);
        this.runner = mock(AsynchronousRunner.class);
        this.repo = new PostgresqlTaskInstanceRepo(this.executor, this.validator, this.plugin, this.tagService,
                this.categoryService, this.brokerClient, this.runner);
    }

    @Nested
    @DisplayName("测试创建逻辑")
    class CreateTest {
        private EventPublisher publisher;

        @BeforeEach
        void setup() {
            this.publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(this.publisher);

            when(validator.typeId(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(validator.sourceId(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
        }

        @Test
        @DisplayName("当任务类型与数据源都没有提供时，抛出异常")
        void should_throw_when_type_and_source_are_both_supplied() {
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(TASK, TaskInstance.Declaration.custom().build(), CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_TYPE_REQUIRED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当任务类型未提供且数据源不存在时抛出异常")
        void should_throw_when_type_not_supplied_and_source_not_found() {
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().source("none").build();
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(TASK, declaration, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_SOURCE_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当任务类型不存在时抛出异常")
        void should_throw_when_type_not_found() {
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().type("none").build();
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(TASK, declaration, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_TYPE_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当数据源在任务类型中未定义时抛出异常")
        void should_throw_when_source_not_in_type() {
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().type("t1").source("s21").build();
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(TASK, declaration, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_SOURCE_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当没有设置 info 时抛出异常")
        void should_throw_when_info_not_supplied() {
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().type("t1").source("s11").build();
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(TASK, declaration, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_INFO_REQUIRED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当已存在相同任务实例时抛出异常")
        void should_throw_when_instance_exist() {
            final String typeId = "t1";
            final String sourceId = "s11";
            final String instanceId = "94246af359724a5fbcf5535511ac8aef";
            final String customId = "92b69752b02b40a2acad92ae04dce950";
            final String customStatus = "ready";
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().type(typeId).source(sourceId)
                    .info(MapBuilder.<String, Object>get().put("id", customId).put("status", customStatus).build())
                    .build();
            List<Map<String, Object>> rows = Collections.singletonList(
                    row(typeId, sourceId, instanceId, customId, customStatus));
            when(executor.executeQuery(eq(SELECT_BY_TEXT_1_SQL), argThat(matchArguments(TASK_ID, customId))))
                    .thenReturn(rows);
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> repo.create(TASK, declaration, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_EXISTS.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当已存在相同已删除任务实例时抛出异常")
        void should_throw_when_deleted_instance_exist() {
            final String typeId = "t1";
            final String sourceId = "s11";
            final String instanceId = "a1b25a0e65ef459296ab3dec334dac4b";
            final String customId = "b1fccb78cdb649629d98e199ebc512f9";
            final String customStatus = "complete";
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().type(typeId).source(sourceId)
                    .info(MapBuilder.<String, Object>get().put("id", customId).put("status", customStatus).build())
                    .build();
            List<Map<String, Object>> rows = Collections.singletonList(
                    row(typeId, sourceId, instanceId, customId, customStatus));
            when(executor.executeQuery(eq(SELECT_BY_TEXT_1_SQL), argThat(matchArguments(TASK_ID, customId))))
                    .thenReturn(Collections.emptyList());
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_TEXT_1_SQL), argThat(matchArguments(TASK_ID, customId))))
                    .thenReturn(rows);
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            GoneException ex = assertThrows(GoneException.class,
                    () -> repo.create(TASK, declaration, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_DELETED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当必填属性未提供时抛出异常")
        void should_throw_when_required_property_not_supplied() {
            final String typeId = "t1";
            final String sourceId = "s11";
            final String customId = "e704b34970194302ba4266e8893a56c3";
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().type(typeId).source(sourceId)
                    .info(MapBuilder.<String, Object>get().put("id", customId).build()).build();
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(TASK, declaration, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_PROPERTY_REQUIRED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("输入正确时应执行 SQL 以插入任务实例数据")
        void should_execute_sql_to_insert_instance_row() {
            final String typeId = "t1";
            final String sourceId = "s11";
            final String customId = "e704b34970194302ba4266e8893a56c3";
            final String customStatus = "ready";
            when(executor.executeQuery(eq(SELECT_BY_TEXT_1_SQL), argThat(matchArguments(TASK_ID, customId))))
                    .thenReturn(Collections.emptyList());
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_TEXT_1_SQL), argThat(matchArguments(TASK_ID, customId))))
                    .thenReturn(Collections.emptyList());
            when(executor.executeUpdate(eq(INSERT_SQL), argThat(matchArguments(isId(), TASK_ID, typeId, sourceId,
                    customId, customStatus)))).thenReturn(1);
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            mockSynchronousRunner();
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().source(sourceId)
                    .info(MapBuilder.<String, Object>get().put("id", customId).put("status", customStatus).build())
                    .tags(Collections.singletonList("good")).build();
            TaskInstance instance = repo.create(TASK, declaration, CONTEXT);
            assertTrue(Entities.isId(instance.id()));
            assertSame(TASK, instance.task());
            assertEquals(typeId, instance.type().id());
            assertEquals(sourceId, instance.source().getId());
            assertEquals(2, instance.info().size());
            Map<String, Object> info = instance.info();
            assertEquals(customId, info.get("id"));
            assertEquals(customStatus, info.get("status"));
            verify(publisher, times(1)).publishEvent(argThat(arg -> matchEvent(arg, TaskInstanceCreatingEvent.class,
                    event -> event.task() == TASK && event.declaration() == declaration && event.context() == CONTEXT
                            && Entities.isId(event.instanceId()))));
            verify(tagService, times(1)).save(eq("INSTANCE"), argThat(Entities::isId),
                    argThat(arg -> arg.size() == 1 && Objects.equals("good", arg.get(0))), any());
            verify(categoryService, times(1)).saveUsages(eq("INSTANCE"), argThat(Entities::isId),
                    argThat(arg -> arg.size() == 1 && Objects.equals("未开始", arg.get(0))), any());
            verify(publisher, times(1)).publishEvent(argThat(arg -> matchEvent(arg, TaskInstanceCreatedEvent.class,
                    event -> event.task() == TASK && event.instance() == instance && event.context() == CONTEXT)));
        }
    }

    @Nested
    @DisplayName("测试修改逻辑")
    class PatchTest {
        private EventPublisher publisher;

        private Map<String, Object> values;

        @BeforeEach
        void setup() {
            this.publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(this.publisher);

            when(validator.typeId(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(validator.sourceId(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            this.values = MapBuilder.<String, Object>get()
                    .put(TaskInstanceRow.COLUMN_ID, INSTANCE_ID)
                    .put(TaskInstanceRow.COLUMN_TASK_ID, TASK_ID)
                    .put(TaskInstanceRow.COLUMN_TYPE_ID, "t1")
                    .put(TaskInstanceRow.COLUMN_SOURCE_ID, "s11")
                    .put("info_id", CUSTOM_ID)
                    .put("info_status", "ready")
                    .build();
        }

        @Test
        @DisplayName("当任务实例不存在时抛出异常")
        void should_throw_when_instance_not_exist() {
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.emptyList());
            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> repo.patch(TASK, INSTANCE_ID, TaskInstance.Declaration.custom().build(), CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当将必填属性的值修改为 null 时抛出异常")
        void should_throw_when_modify_required_to_null() {
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(this.values));
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.patch(TASK, INSTANCE_ID, TaskInstance.Declaration.custom()
                            .info(MapBuilder.<String, Object>get().put("status", null).build())
                            .build(), CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_PROPERTY_REQUIRED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当已存在其他任务实例，与修改到的目标具有相同的主键时，抛出异常")
        void should_throw_when_another_instance_with_same_primary_exists() {
            final String anotherCustomId = "674d9d83d80e4b33ac0ae35b2e40a024";
            Map<String, Object> anotherValues = new HashMap<>(this.values);
            anotherValues.put(TaskInstanceRow.COLUMN_ID, "d30cb1a738924cb29f574a1ef119c40c");
            anotherValues.put("info_id", anotherCustomId);
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(this.values));
            when(executor.executeQuery(eq(SELECT_BY_TEXT_1_SQL), argThat(matchArguments(TASK_ID, anotherCustomId))))
                    .thenReturn(Collections.singletonList(anotherValues));
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> repo.patch(TASK, INSTANCE_ID, TaskInstance.Declaration.custom()
                            .info(MapBuilder.<String, Object>get().put("id", anotherCustomId).build())
                            .build(), CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_EXISTS.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当已删除的任务实例，与修改到的目标具有相同的主键时，抛出异常")
        void should_throw_when_deleted_instance_with_same_primary_exists() {
            final String anotherCustomId = "674d9d83d80e4b33ac0ae35b2e40a024";
            Map<String, Object> anotherValues = new HashMap<>(this.values);
            anotherValues.put(TaskInstanceRow.COLUMN_ID, "d30cb1a738924cb29f574a1ef119c40c");
            anotherValues.put("info_id", anotherCustomId);
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(this.values));
            when(executor.executeQuery(eq(SELECT_BY_TEXT_1_SQL), argThat(matchArguments(TASK_ID, anotherCustomId))))
                    .thenReturn(Collections.emptyList());
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_TEXT_1_SQL), argThat(matchArguments(TASK_ID,
                    anotherCustomId)))).thenReturn(Collections.singletonList(anotherValues));
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            GoneException ex = assertThrows(GoneException.class,
                    () -> repo.patch(TASK, INSTANCE_ID, TaskInstance.Declaration.custom()
                            .info(MapBuilder.<String, Object>get().put("id", anotherCustomId).build())
                            .build(), CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_DELETED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当没有任何变更时，不进行任何操作")
        void should_do_nothing_when_nothing_modified() {
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(this.values));
            repo.patch(TASK, INSTANCE_ID, TaskInstance.Declaration.custom().build(), CONTEXT);
            verify(executor, times(0)).executeUpdate(any(), any());
            verify(tagService, times(0)).save(any(), any(), any(), any());
            verify(categoryService, times(0)).saveUsages(any(), any(), any(), any());
        }

        @Test
        @DisplayName("当基础属性被修改时，执行 SQL 更新数据库")
        void should_execute_sql_to_update_when_basic_properties_modified() {
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(this.values));
            when(executor.executeQuery(eq(SELECT_BY_TEXT_1_SQL), any())).thenReturn(Collections.emptyList());
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_TEXT_1_SQL), any())).thenReturn(Collections.emptyList());
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom()
                    .type("t2").source("s21").build();
            when(executor.executeUpdate(eq(UPDATE_BASIC_SQL), argThat(matchArguments("t2", "s21", INSTANCE_ID))))
                    .thenReturn(1);
            repo.patch(TASK, INSTANCE_ID, declaration, CONTEXT);
            verify(tagService, times(0)).save(any(), any(), any(), any());
            verify(categoryService, times(0)).saveUsages(any(), any(), any(), any());
        }

        @Test
        @DisplayName("当自定义属性被修改时，执行 SQL 更新数据库")
        void should_execute_sql_to_update_when_custom_properties_modified() {
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(this.values));
            when(executor.executeQuery(eq(SELECT_BY_TEXT_1_SQL), any())).thenReturn(Collections.emptyList());
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_TEXT_1_SQL), any())).thenReturn(Collections.emptyList());
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom()
                    .info(MapBuilder.<String, Object>get().put("status", "complete").build()).build();
            when(executor.executeUpdate(eq(UPDATE_STATUS_SQL), argThat(matchArguments("complete", INSTANCE_ID))))
                    .thenReturn(1);
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            mockSynchronousRunner();
            CategoryEntity category = new CategoryEntity();
            category.setId("f41dd0404fc74f8db484be3fa2dac025");
            category.setName("已完成");
            category.setGroup("状态");
            when(categoryService.listByNames(argThat(names -> CollectionUtils.equals(names,
                    Collections.singletonList("已完成"))))).thenReturn(Collections.singletonList(category));

            Invoker instancesChanged = mockInvoker(DataService.class,
                    "e2bb4c43e3ff4f649210eb39d3a8fc77", INSTANCE_FITABLE_ID);
            Invoker categoryChanged = mockInvoker(OnInstancesCategoryChanged.class,
                    "e504e51720c242ab8edf5d0ccf97f5cc", CATEGORY_FITABLE_ID);

            repo.patch(TASK, INSTANCE_ID, declaration, CONTEXT);
            verify(tagService, times(0)).save(any(), any(), any(), any());
            verify(categoryService, times(1)).saveUsages(eq("INSTANCE"), eq(INSTANCE_ID),
                    argThat(args -> args.size() == 1 && Objects.equals(args.get(0), "已完成")), any());
            verify(this.publisher, times(1)).publishEvent(argThat(arg -> matchEvent(arg,
                    TaskInstanceModifyingEvent.class, event -> {
                        if (event.task() != TASK || event.context() != CONTEXT || event.declaration() != declaration) {
                            return false;
                        }
                        return StringUtils.equalsIgnoreCase(event.old().id(), INSTANCE_ID) && event.old().task() == TASK
                                && StringUtils.equalsIgnoreCase(event.old().type().id(), "t1")
                                && StringUtils.equalsIgnoreCase(event.old().source().getId(), "s11")
                                && Objects.equals(event.old().info().get("id"), CUSTOM_ID)
                                && Objects.equals(event.old().info().get("status"), "ready");
                    })));
            verify(this.publisher, times(1)).publishEvent(argThat(arg -> matchEvent(arg,
                    TaskInstanceModifiedEvent.class, event -> {
                        if (event.task() != TASK || event.context() != CONTEXT) {
                            return false;
                        }
                        if (!Objects.equals(event.instance().info().get("status"), "complete")) {
                            return false;
                        }
                        return event.values().size() == 1 && Objects.equals(event.values().get("status"), "ready");
                    })));
            verify(categoryChanged, times(1)).invoke(argThat((List<InstanceCategoryChanged> changes) -> {
                if (changes.size() != 1 || !matchInstanceMessage(changes.get(0))) {
                    return false;
                }
                InstanceCategoryChanged change = changes.get(0);
                return StringUtils.equalsIgnoreCase(change.getNewCategory(), "已完成");
            }));
            verify(instancesChanged, times(1)).invoke(argThat((List<InstanceChanged> changes) -> {
                if (changes.size() != 1 || !matchInstanceMessage(changes.get(0))) {
                    return false;
                }
                List<InstanceChanged.ChangedPropertyValue> details = changes.get(0).getChanges();
                if (details.size() != 1) {
                    return false;
                }
                InstanceChanged.ChangedPropertyValue detail = details.get(0);
                return StringUtils.equalsIgnoreCase(detail.getProperty(), "status")
                        && StringUtils.equalsIgnoreCase(detail.getOriginValue(), "ready")
                        && StringUtils.equalsIgnoreCase(detail.getValue(), "complete")
                        && StringUtils.equalsIgnoreCase(detail.getDataType(), "TEXT");
            }));
        }

        @Test
        @DisplayName("当提供了标签时，需要保存标签信息")
        void should_save_tags_when_supplied() {
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(this.values));
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom()
                    .tags(Collections.singletonList("tag2")).build();
            repo.patch(TASK, INSTANCE_ID, declaration, CONTEXT);
            verify(executor, times(0)).executeUpdate(any(), any());
            verify(categoryService, times(0)).saveUsages(any(), any(), any(), any());
            verify(tagService, times(1)).save(eq("INSTANCE"), eq(INSTANCE_ID),
                    argThat(tags -> tags.size() == 1 && StringUtils.equalsIgnoreCase(tags.get(0), "tag2")), any());
        }
    }

    @Nested
    @DisplayName("测试删除逻辑")
    class DeleteTest {
        @BeforeEach
        void setup() {
            Map<String, Object> values = Collections.singletonMap(TaskInstanceRow.COLUMN_ID, INSTANCE_ID);
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(values));
        }

        @Test
        @DisplayName("删除时应将数据移至删除表并删除原始表数据")
        void should_move_to_history_and_delete() {
            when(executor.executeUpdate(eq(MOVE_HISTORY_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(1);
            when(executor.executeUpdate(eq(DELETE_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(1);
            mockSynchronousRunner();
            EventPublisher publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(publisher);
            assertDoesNotThrow(() -> repo.delete(TASK, INSTANCE_ID, CONTEXT));
            verify(tagService, times(0)).save(any(), any(), any(), any());
            verify(categoryService, times(0)).saveUsages(any(), any(), any(), any());
            verify(publisher, times(1)).publishEvent(argThat(arg -> matchEvent(arg,
                    TaskInstanceDeletedEvent.class, event -> event.task() == TASK && event.context() == CONTEXT
                            && StringUtils.equalsIgnoreCase(event.instance().id(), INSTANCE_ID)
            )));
        }

        @Test
        @DisplayName("当将任务实例移动到删除表失败时抛出异常")
        void should_throw_when_fail_to_move_history() {
            when(executor.executeUpdate(eq(MOVE_HISTORY_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(0);
            ServerInternalException ex = assertThrows(ServerInternalException.class,
                    () -> repo.delete(TASK, INSTANCE_ID, CONTEXT));
            assertEquals("Failed to move task instance.", ex.getMessage());
        }

        @Test
        @DisplayName("当删除任务失败时抛出异常")
        void should_throw_when_fail_to_delete_instance() {
            when(executor.executeUpdate(eq(MOVE_HISTORY_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(1);
            when(executor.executeUpdate(eq(DELETE_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(0);
            ServerInternalException ex = assertThrows(ServerInternalException.class,
                    () -> repo.delete(TASK, INSTANCE_ID, CONTEXT));
            assertEquals("Failed to delete task instance.", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("测试检索逻辑")
    class RetrieveTest {
        @Test
        @DisplayName("当任务实例不存在时抛出异常")
        void should_throw_when_instance_not_exist() {
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.emptyList());
            when(executor.executeScalar(eq(COUNT_DELETED_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(0);
            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> repo.retrieve(TASK, INSTANCE_ID, false, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当任务实例呗删除时抛出异常")
        void should_throw_when_instance_deleted() {
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.emptyList());
            when(executor.executeScalar(eq(COUNT_DELETED_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(1);
            GoneException ex = assertThrows(GoneException.class,
                    () -> repo.retrieve(TASK, INSTANCE_ID, false, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_DELETED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当查询的已删除实例不存在时抛出异常")
        void should_throw_when_retrieve_deleted_but_not_exist() {
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_TEXT_1_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.emptyList());
            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> repo.retrieve(TASK, INSTANCE_ID, true, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_NOT_FOUND.getErrorCode(), ex.getCode());
            verify(executor, times(0)).executeScalar(any(), any());
            verify(executor, times(1)).executeQuery(any(), any());
        }

        @Test
        @DisplayName("执行 SQL 以查询任务实例")
        void should_execute_sql_to_retrieve_instance() {
            Map<String, Object> values = MapBuilder.<String, Object>get()
                    .put(TaskInstanceRow.COLUMN_ID, INSTANCE_ID)
                    .put(TaskInstanceRow.COLUMN_TASK_ID, TASK_ID)
                    .put(TaskInstanceRow.COLUMN_TYPE_ID, "t1")
                    .put(TaskInstanceRow.COLUMN_SOURCE_ID, "s11")
                    .put("info_id", CUSTOM_ID).put("info_status", "complete").build();
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(values));
            when(tagService.list(eq("INSTANCE"), eq(INSTANCE_ID), any()))
                    .thenReturn(Collections.singletonList("tag1"));
            when(categoryService.listUsages(eq("INSTANCE"), eq(INSTANCE_ID), any()))
                    .thenReturn(Collections.singletonList("已完成"));
            TaskInstance instance = repo.retrieve(TASK, INSTANCE_ID, false, CONTEXT);
            assertEquals(INSTANCE_ID, instance.id());
            assertSame(TASK, instance.task());
            assertEquals("t1", instance.type().id());
            assertEquals("s11", instance.source().getId());
            assertEquals(2, instance.info().size());
            Map<String, Object> info = instance.info();
            assertEquals(CUSTOM_ID, info.get("id"));
            assertEquals("complete", info.get("status"));
            assertIterableEquals(Collections.singletonList("tag1"), instance.tags());
            assertIterableEquals(Collections.singletonList("已完成"), instance.categories());
        }
    }

    @Nested
    @DisplayName("测试删除数据源中所有任务实例的逻辑")
    class DeleteBySourceTest {
        private final String s11 = "f598e2b59de14b63b61cfbc1fe60e667";

        @BeforeEach
        void setup() {
            TASK.getTypes().get(0).sources().get(0).setId(s11);
        }

        @AfterEach
        void teardown() {
            TASK.getTypes().get(0).sources().get(0).setId("s11");
        }

        @Test
        @DisplayName("当数据源的唯一标识格式不正确时抛出异常")
        void should_throw_when_source_id_invalid() {
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.deleteBySource(TASK, "hello", CONTEXT));
            assertEquals(ErrorCodes.SOURCE_SOURCE_INVALID.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当数据源不存在时抛出异常")
        void should_throw_when_source_not_found() {
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.deleteBySource(TASK, "2b0d66759028442e839486c53c7c376d", CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_SOURCE_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("执行 SQL 删除数据源中的所有任务实例")
        void should_delete_instances_in_source() {
            when(executor.executeQuery(eq(SELECT_IDS_BY_SOURCE_SQL), argThat(matchArguments(s11)))).thenReturn(
                    Collections.singletonList(Collections.singletonMap(TaskInstanceRow.COLUMN_ID, INSTANCE_ID)));
            when(executor.executeUpdate(eq(MOVE_HISTORY_BY_SOURCE_SQL), argThat(matchArguments(s11)))).thenReturn(1);
            when(executor.executeUpdate(eq(DELETE_BY_SOURCE_SQL), argThat(matchArguments(s11)))).thenReturn(1);
            Map<String, Object> values = MapBuilder.<String, Object>get()
                    .put(TaskInstanceRow.COLUMN_ID, INSTANCE_ID)
                    .put(TaskInstanceRow.COLUMN_TASK_ID, TASK_ID)
                    .put(TaskInstanceRow.COLUMN_TYPE_ID, "t1")
                    .put(TaskInstanceRow.COLUMN_SOURCE_ID, s11)
                    .put("info_id", CUSTOM_ID).put("info_status", "complete").build();
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(values));
            when(tagService.list(eq("INSTANCE"), argThat((List<String> ids) -> ids.size() == 1
                    && StringUtils.equalsIgnoreCase(ids.get(0), INSTANCE_ID)), any()))
                    .thenReturn(Collections.emptyMap());
            when(categoryService.listUsages(eq("INSTANCE"), argThat((List<String> ids) -> ids.size() == 1
                    && StringUtils.equalsIgnoreCase(ids.get(0), INSTANCE_ID)), any()))
                    .thenReturn(Collections.emptyMap());
            mockSynchronousRunner();
            EventPublisher publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(publisher);
            assertDoesNotThrow(() -> repo.deleteBySource(TASK, s11, CONTEXT));
            verify(publisher, times(1)).publishEvent(argThat(arg -> matchEvent(arg,
                    TaskInstanceDeletedEvent.class, event -> event.task() == TASK && event.context() == CONTEXT
                            && StringUtils.equalsIgnoreCase(event.instance().id(), INSTANCE_ID))));
        }
    }

    @Nested
    @DisplayName("测试统计逻辑")
    class StatisticTest {
        private TaskInstance.Filter filter;

        @BeforeEach
        void before() {
            this.filter = TaskInstance.Filter.custom()
                    .ids(Arrays.asList("instanceId1", "instanceId2"))
                    .typeIds(Collections.singletonList("t1"))
                    .sourceIds(Arrays.asList("s11", "s12"))
                    .infos(MapBuilder.<String, List<String>>get()
                            .put("id", Collections.singletonList("id1"))
                            .put("status", Arrays.asList("status1", "status2"))
                            .build())
                    .tags(Arrays.asList("tag1", "tag2"))
                    .build();
        }

        @Test
        void statistics() {
            String column = "owner";
            repo.statistics(TASK, this.filter, column, CONTEXT);
        }
    }

    @Nested
    @DisplayName("测试查询逻辑")
    class ListTest {
        private Pagination pagination;

        private List<Object> baseArgs;

        private List<Object> fullArgs;

        private List<Map<String, Object>> rows;

        private TaskInstance.Filter filter;

        private List<String> categoryNames;

        private List<String> tagNames;

        private List<OrderBy> orderBys;

        @BeforeEach
        void setup() {
            this.pagination = Pagination.create(0, 200);
            this.baseArgs = Arrays.asList("INSTANCE", "INSTANCE",
                    TASK_ID, "instanceId1", "instanceId2", "t1", "s11", "s12", "ready", "processing", "risk",
                    "tag1-id", "tag2-id", "%id1%", "%status1%", "%status2%");
            this.fullArgs = new ArrayList<>(this.baseArgs.size() + 2);
            this.fullArgs.addAll(this.baseArgs);
            this.fullArgs.addAll(Arrays.asList(this.pagination.offset(), this.pagination.limit()));
            this.rows = Collections.singletonList(MapBuilder.<String, Object>get()
                    .put(TaskInstanceRow.COLUMN_ID, INSTANCE_ID).put(TaskInstanceRow.COLUMN_TASK_ID, TASK_ID)
                    .put(TaskInstanceRow.COLUMN_TYPE_ID, "t1").put(TaskInstanceRow.COLUMN_SOURCE_ID, "s11")
                    .put("info_id", CUSTOM_ID).put("info_status", "ready")
                    .build());
            this.categoryNames = Arrays.asList("未开始", "处理中", "已完成");
            this.tagNames = Arrays.asList("tag1", "tag2");
            this.filter = TaskInstance.Filter.custom()
                    .ids(Arrays.asList("instanceId1", "instanceId2"))
                    .typeIds(Collections.singletonList("t1"))
                    .sourceIds(Arrays.asList("s11", "s12"))
                    .infos(MapBuilder.<String, List<String>>get()
                            .put("id", Collections.singletonList("id1"))
                            .put("status", Arrays.asList("status1", "status2"))
                            .build())
                    .tags(Arrays.asList("tag1", "tag2"))
                    .categories(categoryNames)
                    .build();
            this.orderBys = Arrays.asList(OrderBy.of("info.id"), OrderBy.of("info.status", OrderBy.DESCENDING));

            when(categoryService.listByNames(argThat(names -> CollectionUtils.equals(names, this.categoryNames))))
                    .thenReturn(Arrays.asList(
                            category("ready", "未开始", "cg1"),
                            category("processing", "处理中", "cg1"),
                            category("risk", "风险", "cg2")));
            when(categoryService.listUsages(eq("INSTANCE"), argThat(matchArguments(INSTANCE_ID)), any()))
                    .thenReturn(Collections.singletonMap(INSTANCE_ID, Arrays.asList("未开始", "风险")));
            when(tagService.identify(argThat((List<String> arg) -> CollectionUtils.equals(arg, this.tagNames)), any()))
                    .thenReturn(MapBuilder.<String, String>get()
                            .put("tag1", "tag1-id")
                            .put("tag2", "tag2-id").build());
            when(tagService.list(eq("INSTANCE"), argThat(matchArguments(INSTANCE_ID)), any()))
                    .thenReturn(Collections.singletonMap(INSTANCE_ID, Collections.singletonList("tag1")));
        }

        private ArgumentMatcher<List<Object>> matchBaseArgs() {
            return matchArguments(this.baseArgs.toArray());
        }

        private ArgumentMatcher<List<Object>> matchFullArgs() {
            return matchArguments(this.fullArgs.toArray());
        }

        @Test
        @DisplayName("当所有过滤条件都被设置时，可以返回正确的常规列表视图")
        void should_return_list_view_with_full_filter() {
            when(executor.executeScalar(eq(LIST_FULL_FILTER_COUNT_SQL), argThat(this.matchBaseArgs())))
                    .thenReturn(1);
            when(executor.executeQuery(eq(LIST_FULL_FILTER_SELECT_SQL), argThat(this.matchFullArgs())))
                    .thenReturn(this.rows);

            PagedResultSet<TaskInstance> instances = repo.list(TASK, this.filter, this.pagination, this.orderBys,
                    ViewMode.LIST, CONTEXT);
            assertPagedResultSet(instances);
        }

        private void assertPagedResultSet(PagedResultSet<TaskInstance> instances) {
            assertEquals(this.pagination.offset(), instances.pagination().offset());
            assertEquals(this.pagination.limit(), instances.pagination().limit());
            assertEquals(1, instances.pagination().total());
            assertEquals(1, instances.results().size());
            TaskInstance instance = instances.results().get(0);
            assertEquals(INSTANCE_ID, instance.id());
            assertSame(TASK, instance.task());
            assertEquals("t1", instance.type().id());
            assertEquals("s11", instance.source().getId());
            assertEquals(CUSTOM_ID, instance.info().get("id"));
            assertEquals("ready", instance.info().get("status"));
            assertIterableEquals(Collections.singletonList("tag1"), instance.tags());
            assertIterableEquals(Arrays.asList("未开始", "风险"), instance.categories());
        }

        @Nested
        @DisplayName("测试树形列表")
        class TreeTest {
            private List<TaskProperty> properties;

            @BeforeEach
            void setup() {
                this.properties = TASK.getProperties();
                List<TaskProperty> newProperties = new ArrayList<>(this.properties.size() + 1);
                newProperties.addAll(properties);
                newProperties.add(property("p3", "decomposed_from", PropertyDataType.TEXT, 3,
                        false, false, Collections.emptyMap()));
                TASK.setProperties(newProperties);
            }

            @AfterEach
            void teardown() {
                TASK.setProperties(this.properties);
            }

            @Test
            @DisplayName("当所有过滤条件都被设置时，可以返回正确的树形列表视图")
            void should_return_tree_view_with_full_filter() {
                final String childId = "b76e75e3b41944e4af324377e66db828";
                when(executor.executeScalar(eq(TREE_FULL_FILTER_COUNT_SQL), argThat(matchBaseArgs())))
                        .thenReturn(1);
                when(executor.executeQuery(eq(TREE_FULL_FILTER_SELECT_SQL), argThat(matchFullArgs())))
                        .thenReturn(rows);
                List<Map<String, Object>> childRows = Collections.singletonList(MapBuilder.<String, Object>get()
                        .put(TaskInstanceRow.COLUMN_ID, "bdfca603a6f5472d83513a0cad940130")
                        .put(TaskInstanceRow.COLUMN_TASK_ID, TASK_ID)
                        .put(TaskInstanceRow.COLUMN_TYPE_ID, "t1").put(TaskInstanceRow.COLUMN_SOURCE_ID, "s11")
                        .put("info_id", childId).put("info_status", "ready").put("info_decomposed_from", CUSTOM_ID)
                        .build());
                when(executor.executeQuery(eq(TREE_SELECT_CHILDREN_SQL),
                        argThat(matchArguments(childrenArgs(CUSTOM_ID))))).thenReturn(childRows);
                when(executor.executeQuery(eq(TREE_SELECT_CHILDREN_SQL),
                        argThat(matchArguments(childrenArgs(childId))))).thenReturn(Collections.emptyList());
                PagedResultSet<TaskInstance> instances = repo.list(TASK, filter, pagination, orderBys,
                        ViewMode.TREE, CONTEXT);
                assertPagedResultSet(instances);
                HierarchicalTaskInstance parent = as(instances.results().get(0), HierarchicalTaskInstance.class);
                assertNotNull(parent);
                assertEquals(1, parent.children().size());
                HierarchicalTaskInstance child = parent.children().get(0);
                assertEquals("bdfca603a6f5472d83513a0cad940130", child.id());
                assertEquals(TASK, child.task());
                assertEquals("t1", child.type().id());
                assertEquals("s11", child.source().getId());
                assertEquals(3, child.info().size());
                assertEquals(childId, child.info().get("id"));
                assertEquals("ready", child.info().get("status"));
                assertEquals(CUSTOM_ID, child.info().get("decomposed_from"));
                assertTrue(child.tags().isEmpty());
                assertTrue(child.categories().isEmpty());
            }

            private Object[] childrenArgs(String parentId) {
                List<Object> childrenArgs = new ArrayList<>(baseArgs.size() + 1);
                childrenArgs.addAll(baseArgs);
                childrenArgs.add(parentId);
                return childrenArgs.toArray();
            }
        }
    }

    @Nested
    @DisplayName("测试恢复逻辑")
    class RecoverTest {
        @Test
        @DisplayName("当没有已删除的实例时抛出异常")
        void should_throw_when_no_deleted_instance() {
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.emptyList());
            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> repo.recover(TASK, INSTANCE_ID, CONTEXT));
            assertEquals(ErrorCodes.INSTANCE_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("执行 SQL 以恢复任务实例")
        void should_execute_sql_to_recover() {
            Map<String, Object> row = Collections.singletonMap(TaskInstanceRow.COLUMN_ID, INSTANCE_ID);
            when(executor.executeQuery(eq(SELECT_HISTORY_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(Collections.singletonList(row));
            when(executor.executeUpdate(eq(MOVE_HISTORY_BACK_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(1);
            when(executor.executeUpdate(eq(DELETE_HISTORY_BY_ID_SQL), argThat(matchArguments(INSTANCE_ID, TASK_ID))))
                    .thenReturn(1);
            when(tagService.list(eq("INSTANCE"), argThat(matchArguments(INSTANCE_ID)), any()))
                    .thenReturn(Collections.singletonMap(INSTANCE_ID, Collections.singletonList("tag1")));
            when(categoryService.listUsages(eq("INSTANCE"), argThat(matchArguments(INSTANCE_ID)), any()))
                    .thenReturn(Collections.singletonMap(INSTANCE_ID, Collections.singletonList("category1")));
            mockSynchronousRunner();
            EventPublisher publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(publisher);
            assertDoesNotThrow(() -> repo.recover(TASK, INSTANCE_ID, CONTEXT));
            verify(publisher, times(1)).publishEvent(argThat(arg -> matchEvent(arg, TaskInstanceCreatedEvent.class,
                    event -> event.task() == TASK && StringUtils.equalsIgnoreCase(event.instance().id(), INSTANCE_ID)
                            && CollectionUtils.equals(event.instance().tags(), Collections.singletonList("tag1"))
                            && CollectionUtils.equals(event.instance().categories(),
                            Collections.singletonList("category1"))
            )));
        }
    }

    @Nested
    @DisplayName("测试索引逻辑")
    class IndexTest {
        private TaskEntity task;

        private final String insertTextValuesSql = script("instance-index-insert-text-values");

        private final String insertIntegerValuesSql = script("instance-index-insert-integer-values");

        private final String updateTextValuesSql = script("instance-index-update-text-values");

        private final String updateIntegerValuesSql = script("instance-index-update-integer-values");

        private final String selectWithSql = script("instance-index-select-with");

        @BeforeEach
        void setup() {
            this.task = new TaskEntity();
            this.task.setId(TASK_ID);
            this.task.setName("index-test");
            this.task.setTypes(TASK.getTypes());
            this.task.setCategoryTriggers(Collections.emptyList());
            this.task.setProperties(Arrays.asList(
                    property("p1", "id", PropertyDataType.TEXT, 1, true, true),
                    property("p2", "name", PropertyDataType.TEXT, 2, true, false),
                    property("p3", "category", PropertyDataType.TEXT, 3, false, false),
                    property("p4", "priority", PropertyDataType.INTEGER, 1, true, false)
            ));
            this.task.setIndexes(Arrays.asList(
                    this.index("idx1", "id", "id"),
                    this.index("idx2", "priority", "category", "priority")
            ));
            EventPublisher publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(publisher);
        }

        private Index index(String id, String name, String... properties) {
            return Index.custom().id(id).task(this.task).name(name)
                    .properties(Optional.ofNullable(properties).map(Stream::of).orElseGet(Stream::empty)
                            .map(this.task::getPropertyByName).collect(Collectors.toList()))
                    .build();
        }

        @Test
        @DisplayName("当创建任务实例时，插入索引数据")
        void should_insert_index_values_when_create_instance() {
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().type("t1").source("s11")
                    .info(MapBuilder.<String, Object>get().put("id", "ins-01").put("name", "index-test-instance")
                            .put("category", "indexed").put("priority", 10).build())
                    .build();
            when(validator.typeId(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(validator.sourceId(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            String insertInstanceSqlPrefix = "INSERT INTO \"task_instance_wide\"";
            when(executor.executeUpdate(argThat(arg -> StringUtils.startsWithIgnoreCase(arg, insertInstanceSqlPrefix)),
                    any())).thenReturn(1);
            when(executor.executeUpdate(eq(this.insertTextValuesSql), argThat(matchArguments(isId(), isId(), "p1",
                    "ins-01", isId(), isId(), "p3", "indexed")))).thenReturn(2);
            when(executor.executeUpdate(eq(this.insertIntegerValuesSql), argThat(matchArguments(isId(), isId(), "p4",
                    10L)))).thenReturn(1);
            repo.create(this.task, declaration, CONTEXT);
        }

        @Test
        @DisplayName("当修改任务实例时，更新索引数据")
        void should_update_index_values_when_patch_instance() {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("id", "ins-02");
            info.put("name", "index-test-update");
            info.put("category", "indexed-update");
            info.put("priority", 5);
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().info(info).build();
            Map<String, Object> instanceRow = MapBuilder.<String, Object>get()
                    .put(TaskInstanceRow.COLUMN_ID, INSTANCE_ID)
                    .put(TaskInstanceRow.COLUMN_TASK_ID, TASK_ID)
                    .put(TaskInstanceRow.COLUMN_TYPE_ID, "t1")
                    .put(TaskInstanceRow.COLUMN_SOURCE_ID, "s11")
                    .put(TaskInstanceRow.INFO_PREFIX + "id", "ins-01")
                    .put(TaskInstanceRow.INFO_PREFIX + "name", "index-test-instance")
                    .put(TaskInstanceRow.INFO_PREFIX + "category", "indexed")
                    .put(TaskInstanceRow.INFO_PREFIX + "priority", 10L)
                    .build();
            when(validator.typeId(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(validator.sourceId(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(executor.executeQuery(argThat(arg -> StringUtils.startsWithIgnoreCase(arg, "SELECT")
                    && arg.contains(TaskInstanceRow.TABLE)), any())).thenReturn(Collections.singletonList(instanceRow));
            when(executor.executeQuery(argThat(arg -> StringUtils.startsWithIgnoreCase(arg, "SELECT")
                    && arg.contains(TaskInstanceRow.TABLE_DELETED)), any())).thenReturn(Collections.emptyList());
            when(executor.executeUpdate(argThat(arg -> StringUtils.startsWithIgnoreCase(arg, "UPDATE")
                    && arg.contains(TaskInstanceRow.TABLE)), any())).thenReturn(1);
            when(executor.executeUpdate(eq(this.updateTextValuesSql), argThat(args -> {
                if (args.size() != 5) {
                    return false;
                }
                Set<List<Object>> rows = new HashSet<>(2);
                rows.add(Arrays.asList(args.get(0), args.get(1)));
                rows.add(Arrays.asList(args.get(2), args.get(3)));
                return rows.contains(Arrays.asList("p1", "ins-02"))
                        && rows.contains(Arrays.asList("p3", "indexed-update"))
                        && args.get(4).equals(INSTANCE_ID);
            }))).thenReturn(2);
            when(executor.executeUpdate(eq(this.updateIntegerValuesSql), argThat(matchArguments("p4", 5L,
                    INSTANCE_ID)))).thenReturn(1);
            List<Map<String, Object>> textIndexRows = Arrays.asList(
                    rowOfIndexValue("idx-01", "p1", "ins-01"),
                    rowOfIndexValue("idx-02", "p3", "indexed"));
            when(executor.executeQuery(
                    argThat(arg -> arg != null && arg.contains("SELECT") && arg.contains("index_text")),
                    argThat(matchArguments(INSTANCE_ID, "p1", "p3")))).thenReturn(textIndexRows);
            when(executor.executeUpdate(argThat(containsAll("UPDATE", "index_text")),
                    argThat(matchArguments("idx-01", "ins-02", "idx-02", "indexed-update")))).thenReturn(2);
            List<Map<String, Object>> integerIndexRows = Collections.singletonList(
                    rowOfIndexValue("idx-01", "p4", 10));
            when(executor.executeQuery(argThat(containsAll("SELECT", "index_integer")),
                    argThat(matchArguments(INSTANCE_ID, "p4")))).thenReturn(integerIndexRows);
            when(executor.executeUpdate(argThat(arg -> arg.contains("UPDATE") && arg.contains("index_integer")),
                    argThat(matchArguments("idx-01", 5L)))).thenReturn(1);
            repo.patch(this.task, INSTANCE_ID, declaration, CONTEXT);
        }

        @Test
        @DisplayName("当查询任务实例时，使用索引提升效率")
        void should_list_instances_use_index() {
            Map<String, List<String>> infos = new LinkedHashMap<>();
            infos.put("id", Collections.singletonList("eq(123)"));
            infos.put("name", Collections.singletonList("hello"));
            infos.put("category", Arrays.asList("ok", "nok"));
            infos.put("priority", Collections.singletonList("1"));
            TaskInstance.Filter filter = TaskInstance.Filter.custom().infos(infos).build();
            String countSql = "WITH \"w_ins\" AS (" + this.selectWithSql + ") SELECT COUNT(1) FROM \"w_ins\"";
            when(executor.executeScalar(eq(countSql),
                    argThat(matchArguments("p1", "p4", TASK_ID, "123", 1L, "%hello%", "%ok%", "%nok%")))).thenReturn(1);
            PagedResultSet<TaskInstance> instances = repo.list(this.task, filter, Pagination.create(0, 200),
                    Collections.singletonList(OrderBy.of("info.id")), ViewMode.LIST, CONTEXT);
            assertEquals(1, instances.pagination().total());
        }
    }

    @Nested
    @DisplayName("测试列表类型")
    class ListTypeTest {
        private final String insertListValuesSql = script("instance-list-value-insert");

        private final String selectListValuesByPropertiesSql = script("instance-list-value-select-by-properties");

        private final String selectListValuesByInstanceSql = script("instance-list-value-select-by-instance");

        private final String deleteListValuesByInstanceSql = script("instance-list-value-delete-by-instance");

        private final String filterUnindexedSql = script("instance-list-filter-unindexed");

        private final String filterIndexedSql = script("instance-list-filter-indexed");

        private TaskEntity task;

        @BeforeEach
        void setup() {
            this.task = new TaskEntity();
            this.task.setId(TASK_ID);
            this.task.setTypes(TASK.getTypes());
            this.task.setProperties(Arrays.asList(
                    property("p1", "id", PropertyDataType.TEXT, 1, true, true),
                    property("p2", "owner", PropertyDataType.LIST_TEXT, 0, false, false)));
        }

        @Test
        @DisplayName("当创建任务实例时，插入列表值")
        void should_execute_sql_to_insert_list_values_when_create_instance() {
            EventPublisher publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(publisher);
            when(validator.typeId(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.sourceId(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(executor.executeUpdate(argThat(arg -> StringUtils.startsWithIgnoreCase(arg, "INSERT")
                    && arg.contains("task_instance_wide")), any())).thenReturn(1);
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom()
                    .type("t1")
                    .source("s11")
                    .info(MapBuilder.<String, Object>get().put("id", "list-01").put("owner", "hello,world").build())
                    .build();
            when(executor.executeUpdate(eq(this.insertListValuesSql), argThat(matchArguments(
                    isId(), isId(), "p2", 1, "hello", isId(), isId(), "p2", 2, "world")))).thenReturn(2);
            repo.create(this.task, declaration, CONTEXT);
        }

        @Test
        @DisplayName("当修改任务实例时，保存列表值")
        void should_execute_sql_to_save_list_values_when_patch_instance() {
            EventPublisher publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(publisher);
            when(validator.typeId(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.sourceId(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.info(any())).thenAnswer(invocation -> invocation.getArgument(0));
            // mock to retrieve current task instance.
            when(executor.executeQuery(argThat(arg -> StringUtils.startsWithIgnoreCase(arg, "SELECT")
                    && arg.contains(TaskInstanceRow.TABLE)), any())).thenReturn(this.mockInstanceRows());
            when(executor.executeQuery(eq(this.selectListValuesByPropertiesSql), any()))
                    .thenReturn(this.mockListValueRows());
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom()
                    .info(MapBuilder.<String, Object>get().put("owner", "hello,world").build()).build();

            when(executor.executeUpdate(eq(this.insertListValuesSql), argThat(matchArguments(
                    isId(), isId(), "p2", 1, "hello", isId(), isId(), "p2", 2, "world")))).thenReturn(2);

            repo.patch(this.task, INSTANCE_ID, declaration, CONTEXT);
            verify(executor, times(1)).executeUpdate(eq(this.deleteListValuesByInstanceSql),
                    argThat(matchArguments(INSTANCE_ID)));
        }

        @Test
        @DisplayName("当检索任务实例时，返回的用户数据中有列表数据")
        void should_return_instance_with_list_values_when_retrieve() {
            when(executor.executeQuery(argThat(arg -> StringUtils.startsWithIgnoreCase(arg, "SELECT")
                    && arg.contains(TaskInstanceRow.TABLE)), any())).thenReturn(this.mockInstanceRows());
            when(executor.executeQuery(eq(this.selectListValuesByInstanceSql), argThat(matchArguments(INSTANCE_ID))))
                    .thenReturn(this.mockListValueRows());
            TaskInstance instance = repo.retrieve(this.task, INSTANCE_ID, false, CONTEXT);
            this.assertListValues(cast(instance.info().get("owner")));
        }

        @Test
        @DisplayName("当查询任务时，返回的用户数据中有列表数据")
        void should_return_instances_with_list_values_when_list() {
            ArgumentMatcher<String> withMatcher = arg -> StringUtils.startsWithIgnoreCase(arg, "WITH \"w_ins\" AS");
            when(executor.executeScalar(argThat(withMatcher), any())).thenReturn(1);
            when(executor.executeQuery(argThat(withMatcher), any())).thenReturn(mockInstanceRows());
            when(executor.executeQuery(eq(this.selectListValuesByInstanceSql), argThat(matchArguments(INSTANCE_ID))))
                    .thenReturn(mockListValueRows());
            PagedResultSet<TaskInstance> instances = repo.list(this.task, TaskInstance.Filter.custom().build(),
                    Pagination.create(0, 200), Collections.emptyList(), ViewMode.LIST, CONTEXT);
            assertEquals(1L, instances.pagination().total());
            assertEquals(1, instances.results().size());
            List<Object> values = cast(instances.results().get(0).info().get("owner"));
            assertNotNull(values);
            this.assertListValues(values);
        }

        @Test
        @DisplayName("当根据列表属性进行查询时，执行正确的 SQL")
        void should_execute_sql_when_filter_list_values() {
            String baseSql = "WITH \"w_ins\" AS (" + this.filterUnindexedSql + ") ";
            String countSql = baseSql + "SELECT COUNT(1) FROM \"w_ins\"";
            when(executor.executeScalar(eq(countSql), argThat(matchArguments(TASK_ID, "p2", "%hello%", "world"))))
                    .thenReturn(1);
            String selectSql = baseSql + "SELECT * FROM \"w_ins\" OFFSET ? LIMIT ?";
            when(executor.executeQuery(eq(selectSql), argThat(matchArguments(TASK_ID, "p2", "%hello%", "world",
                    0L, 200)))).thenReturn(mockInstanceRows());
            when(executor.executeQuery(eq(this.selectListValuesByInstanceSql), argThat(matchArguments(INSTANCE_ID))))
                    .thenReturn(Collections.emptyList());
            Map<String, List<String>> infos = MapBuilder.<String, List<String>>get()
                    .put("owner", Arrays.asList("hello", "eq(world)")).build();
            PagedResultSet<TaskInstance> instances = repo.list(this.task, TaskInstance.Filter.custom()
                    .infos(infos).build(), Pagination.create(0, 200), Collections.emptyList(), ViewMode.LIST, CONTEXT);
            assertEquals(1L, instances.pagination().total());
            assertEquals(1, instances.results().size());
        }

        @Test
        @DisplayName("当列表属性被索引时，通过索引进行查询")
        void should_filter_list_by_index() {
            this.task.setIndexes(Collections.singletonList(Index.custom()
                    .id("17c1ebb785bf44ba8b2db000158a15b1").name("owner_index").task(this.task)
                    .properties(Collections.singletonList(this.task.getPropertyByName("owner"))).build()));
            String baseSql = "WITH \"w_ins\" AS (" + this.filterIndexedSql + ") ";
            String countSql = baseSql + "SELECT COUNT(1) FROM \"w_ins\"";
            when(executor.executeScalar(eq(countSql), argThat(matchArguments(TASK_ID, "p2", "hello", "ins-01"))))
                    .thenReturn(1);
            String selectSql = baseSql + "SELECT * FROM \"w_ins\" OFFSET ? LIMIT ?";
            when(executor.executeQuery(eq(selectSql), argThat(matchArguments(TASK_ID, "p2", "hello", "ins-01",
                    0L, 200)))).thenReturn(mockInstanceRows());
            when(executor.executeQuery(eq(this.selectListValuesByInstanceSql), argThat(matchArguments(INSTANCE_ID))))
                    .thenReturn(Collections.emptyList());
            Map<String, List<String>> infos = new LinkedHashMap<>(); // 使用 LinkedHashMap 保证遍历顺序
            infos.put("id", Collections.singletonList("eq(ins-01)"));
            infos.put("owner", Collections.singletonList("eq(hello)"));
            PagedResultSet<TaskInstance> instances = repo.list(this.task, TaskInstance.Filter.custom()
                    .infos(infos).build(), Pagination.create(0, 200), Collections.emptyList(), ViewMode.LIST, CONTEXT);
            assertEquals(1L, instances.pagination().total());
            assertEquals(1, instances.results().size());
        }

        private List<Map<String, Object>> mockListValueRows() {
            return Arrays.asList(
                    rowOfListValue("lv3", "p2", 3, "v3"),
                    rowOfListValue("lv1", "p2", -1, "x"),
                    rowOfListValue("lv2", "p2", 1, "v1-not"),
                    rowOfListValue("lv4", "p2", 5, "v5"));
        }

        private void assertListValues(List<Object> values) {
            assertEquals(4, values.size());
            assertEquals("x", values.get(0));
            assertEquals("v1-not", values.get(1));
            assertEquals("v3", values.get(2));
            assertEquals("v5", values.get(3));
        }

        private List<Map<String, Object>> mockInstanceRows() {
            Map<String, Object> instanceRow = MapBuilder.<String, Object>get()
                    .put(TaskInstanceRow.COLUMN_ID, INSTANCE_ID)
                    .put(TaskInstanceRow.COLUMN_TASK_ID, TASK_ID)
                    .put(TaskInstanceRow.COLUMN_TYPE_ID, "t1")
                    .put(TaskInstanceRow.COLUMN_SOURCE_ID, "s11")
                    .put(TaskInstanceRow.INFO_PREFIX + "id", "ins-01")
                    .build();
            return Collections.singletonList(instanceRow);
        }
    }

    private static boolean matchInstanceMessage(InstanceMessage message) {
        return StringUtils.equalsIgnoreCase(message.getTaskId(), TASK_ID)
                && StringUtils.equalsIgnoreCase(message.getInstanceId(), INSTANCE_ID)
                && StringUtils.equalsIgnoreCase(message.getTaskTypeId(), "t1")
                && StringUtils.equalsIgnoreCase(message.getSourceApp(), "source-11")
                && StringUtils.equalsIgnoreCase(message.getTenant(), TENANT_ID)
                && StringUtils.equalsIgnoreCase(message.getOperator(), OPERATOR)
                && message.getPrimaries().size() == 1
                && StringUtils.equalsIgnoreCase(message.getPrimaries().get(0).getProperty(), "id")
                && StringUtils.equalsIgnoreCase(message.getPrimaries().get(0).getDataType(), "TEXT")
                && StringUtils.equalsIgnoreCase(message.getPrimaries().get(0).getValue(), CUSTOM_ID);
    }

    private static <T extends Event> boolean matchEvent(Event event, Class<T> eventClass, Predicate<T> predicate) {
        return eventClass.isInstance(event) && predicate.test(eventClass.cast(event));
    }

    private static TaskType type(String id, String name, String parentId, Map<String, String> sources,
            List<TaskType> children) {
        List<SourceEntity> actualSources = new ArrayList<>(sources.size());
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            SourceEntity source = new SourceEntity();
            source.setId(entry.getKey());
            source.setApp(entry.getValue());
            actualSources.add(source);
        }
        return TaskType.custom().id(id).name(name).parentId(parentId).sources(actualSources).children(children).build();
    }

    private static TaskProperty property(String id, String name, PropertyDataType dataType, int sequence,
            boolean required, boolean identifiable) {
        return property(id, name, dataType, sequence, required, identifiable, Collections.emptyMap());
    }

    private static TaskProperty property(String id, String name, PropertyDataType dataType, int sequence,
            boolean required, boolean identifiable, Map<String, String> categoryMappings) {
        List<PropertyCategory> categories = categoryMappings.entrySet().stream()
                .map(entry -> new PropertyCategory(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return TaskProperty.custom().id(id).name(name).dataType(dataType).sequence(sequence).isRequired(required)
                .isIdentifiable(identifiable).categories(categories).appearance(Collections.emptyMap()).build();
    }

    private static Map<String, Object> row(String typeId, String sourceId, String instanceId,
            String customId, String customStatus) {
        return MapBuilder.<String, Object>get()
                .put(TaskInstanceRow.COLUMN_ID, instanceId).put(TaskInstanceRow.COLUMN_TASK_ID, TASK_ID)
                .put(TaskInstanceRow.COLUMN_TYPE_ID, typeId).put(TaskInstanceRow.COLUMN_SOURCE_ID, sourceId)
                .put("text_1", customId).put("text_2", customStatus)
                .build();
    }

    private void mockSynchronousRunner() {
        doAnswer(invocation -> {
            Stream.of(invocation.getArguments()).map(Runnable.class::cast).forEach(Runnable::run);
            return null;
        }).when(this.runner).run(any());
    }

    private static boolean matchFitableIdFilter(Router.Filter filter, String fitableId) {
        FitableIdFilter actual = as(filter, FitableIdFilter.class);
        if (actual == null) {
            return false;
        }
        Field field = ReflectionUtils.getDeclaredField(FitableIdFilter.class, "fitableIds");
        if (field == null) {
            throw new IllegalStateException(StringUtils.format("Field fitableIds not found in {0}.",
                    FitableIdFilter.class.getName()));
        }
        field.setAccessible(true);
        List<String> value = cast(ReflectionUtils.getField(actual, field));
        return value.size() == 1 && StringUtils.equalsIgnoreCase(value.get(0), fitableId);
    }

    private Invoker mockInvoker(Class<?> serviceClass, String genericableId, String fitableId) {
        Invoker invoker = mock(Invoker.class);
        Router router = mock(Router.class);
        when(router.route(argThat(filter -> matchFitableIdFilter(filter, fitableId)))).thenReturn(invoker);
        when(this.brokerClient.getRouter(serviceClass, genericableId)).thenReturn(router);
        return invoker;
    }

    private static CategoryEntity category(String id, String name, String group) {
        CategoryEntity category = new CategoryEntity();
        category.setId(id);
        category.setName(name);
        category.setGroup(group);
        return category;
    }

    private static Map<String, Object> rowOfListValue(String id, String propertyId, int index, Object value) {
        return MapBuilder.<String, Object>get()
                .put(ListValue.COLUMN_ID, id)
                .put(ListValue.COLUMN_INSTANCE_ID, INSTANCE_ID)
                .put(ListValue.COLUMN_PROPERTY_ID, propertyId)
                .put(ListValue.COLUMN_INDEX, index)
                .put(ListValue.COLUMN_VALUE, value)
                .build();
    }

    private static Map<String, Object> rowOfIndexValue(String id, String propertyId, Object value) {
        return MapBuilder.<String, Object>get()
                .put(IndexValueRow.COLUMN_ID, id)
                .put(IndexValueRow.COLUMN_INSTANCE_ID, INSTANCE_ID)
                .put(IndexValueRow.COLUMN_PROPERTY_ID, propertyId)
                .put(IndexValueRow.COLUMN_VALUE, value)
                .build();
    }

    private static ArgumentMatcher<String> containsAll(String... values) {
        return arg -> arg != null && Stream.of(values).allMatch(arg::contains);
    }
}
