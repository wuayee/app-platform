/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fit.jober.Tests.earlierUtc;
import static com.huawei.fit.jober.Tests.isId;
import static com.huawei.fit.jober.Tests.matchArguments;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyIndexedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyUnindexedEvent;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.IndexValidator;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@DisplayName("测试基于 Postgresql 的索引持久化逻辑")
class PostgresqlIndexRepoTest {
    private static final String TASK_ID = "5664786fc4b84ed8a9f4c2e5f376bebd";

    private static final String INDEX_ID = "7c802c4412404d8e80299c8b46c9b476";

    private static final String PROPERTY_ID_1 = "f6d9c2f80c724984b472945a31564fdf";

    private static final String PROPERTY_NAME_1 = "p1";

    private static final String PROPERTY_ID_2 = "a1cebac4c06d4473ac193881640ccb31";

    private static final String PROPERTY_NAME_2 = "p2";

    private static final String INDEX_NAME = "my-index";

    private static final String OPERATOR = "test";

    private static final OperationContext CONTEXT = OperationContext.custom()
            .operator(OPERATOR).operatorIp("localhost").build();

    private static final String INSERT_SQL;

    private static final String INSERT_PROPERTY_SQL;

    private static final String INSERT_PROPERTY_SQL_2;

    private static final String UPDATE_SQL;

    private static final String DELETE_PROPERTY_SQL;

    private static final String DELETE_SQL;

    private static final String DELETE_ALL_PROPERTIES_SQL;

    private static final String SELECT_INDEX_SQL;

    private static final String SELECT_INDEX_PROPERTY_SQL;

    private static final String UPDATE_NONE_SQL;

    private static final String SELECT_BY_ID_SQL;

    private static final String SELECT_PROPERTIES_BY_INDEX_SQL;

    static {
        ClassLoader loader = PostgresqlIndexRepoTest.class.getClassLoader();
        try {
            INSERT_SQL = IoUtils.content(loader, "sql/index-insert.sql");
            INSERT_PROPERTY_SQL = IoUtils.content(loader, "sql/index-insert-property.sql");
            INSERT_PROPERTY_SQL_2 = IoUtils.content(loader, "sql/index-insert-properties-2.sql");
            UPDATE_SQL = IoUtils.content(loader, "sql/index-patch.sql");
            DELETE_PROPERTY_SQL = IoUtils.content(loader, "sql/index-delete-property.sql");
            DELETE_SQL = IoUtils.content(loader, "sql/index-delete.sql");
            DELETE_ALL_PROPERTIES_SQL = IoUtils.content(loader, "sql/index-delete-all-properties.sql");
            SELECT_INDEX_SQL = IoUtils.content(loader, "sql/index-select.sql");
            SELECT_INDEX_PROPERTY_SQL = IoUtils.content(loader, "sql/index-select-properties.sql");
            UPDATE_NONE_SQL = IoUtils.content(loader, "sql/index-patch-none.sql");
            SELECT_BY_ID_SQL = IoUtils.content(loader, "sql/index-select-by-id.sql");
            SELECT_PROPERTIES_BY_INDEX_SQL = IoUtils.content(loader, "sql/index-select-properties-by-index.sql");
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load SQLs from embedded resources.", ex);
        }
    }

    private DynamicSqlExecutor executor;

    private IndexValidator validator;

    private PostgresqlIndexRepo repo;

    private EventPublisher publisher;

    @BeforeEach
    void setup() {
        this.executor = mock(DynamicSqlExecutor.class);
        this.validator = mock(IndexValidator.class);
        Plugin plugin = mock(Plugin.class);
        this.publisher = mock(EventPublisher.class);
        when(plugin.publisherOfEvents()).thenReturn(this.publisher);
        this.repo = new PostgresqlIndexRepo(this.executor, this.validator, plugin);
    }

    private static TaskEntity mockTask(boolean withProperty) {
        TaskEntity entity = new TaskEntity();
        entity.setId(TASK_ID);
        if (withProperty) {
            TaskProperty property1 = mock(TaskProperty.class);
            when(property1.id()).thenReturn(PROPERTY_ID_1);
            when(property1.name()).thenReturn(PROPERTY_NAME_1);
            when(property1.dataType()).thenReturn(PropertyDataType.TEXT);
            TaskProperty property2 = mock(TaskProperty.class);
            when(property2.id()).thenReturn(PROPERTY_ID_2);
            when(property2.name()).thenReturn(PROPERTY_NAME_2);
            when(property2.dataType()).thenReturn(PropertyDataType.TEXT);
            entity.setProperties(Arrays.asList(property1, property2));
        } else {
            entity.setProperties(Collections.emptyList());
        }
        return entity;
    }

    private static void fillIndex(TaskEntity task) {
        Index index = mock(Index.class);
        when(index.id()).thenReturn(INDEX_ID);
        when(index.name()).thenReturn(INDEX_NAME);
        TaskProperty property = task.getProperties().stream().filter(prop -> Objects.equals(prop.id(), PROPERTY_ID_1))
                .findAny().orElseThrow(IllegalStateException::new);
        when(index.properties()).thenReturn(Collections.singletonList(property));
        task.setIndexes(Collections.singletonList(index));
    }

    @Nested
    @DisplayName("测试创建索引")
    class CreateTest {
        @Test
        @DisplayName("当没有指定索引的名称时，抛出异常")
        void should_throw_when_name_not_supplied() {
            TaskEntity task = mockTask(false);
            Index.Declaration declaration = Index.Declaration.custom()
                    .propertyNames(Collections.singletonList(PROPERTY_ID_1)).build();

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(task, declaration, CONTEXT));
            assertEquals(ErrorCodes.INDEX_NAME_REQUIRED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当没有指定索引的属性时，抛出异常")
        void should_throw_when_properties_not_supplied() {
            TaskEntity task = mockTask(false);
            Index.Declaration declaration = Index.Declaration.custom().name(INDEX_NAME).build();
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(task, declaration, CONTEXT));
            assertEquals(ErrorCodes.INDEX_PROPERTY_REQUIRED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当索引的属性不是任务的属性时，抛出异常")
        void should_throw_when_property_not_exists() {
            TaskEntity task = mockTask(false);
            Index.Declaration declaration = Index.Declaration.custom().name(INDEX_NAME)
                    .propertyNames(Collections.singletonList(PROPERTY_NAME_1)).build();
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.create(task, declaration, CONTEXT));
            assertEquals(ErrorCodes.INDEX_UNKNOWN_PROPERTY.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当创建索引时执行 SQL 语句")
        void should_execute_sql_to_insert_index() {
            TaskEntity task = mockTask(true);
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            Index.Declaration declaration = Index.Declaration.custom().name(INDEX_NAME)
                    .propertyNames(Collections.singletonList(PROPERTY_NAME_1)).build();
            when(executor.executeUpdate(any(), any())).thenReturn(1);

            Index index = repo.create(task, declaration, CONTEXT);
            assertEquals(INDEX_NAME, index.name());
            assertSame(task, index.task());
            assertEquals(1, index.properties().size());
            assertSame(task.getProperties().get(0), index.properties().get(0));

            verify(executor, times(1)).executeUpdate(eq(INSERT_SQL), argThat(matchArguments(
                    index.id(), INDEX_NAME, TASK_ID, OPERATOR, earlierUtc(), OPERATOR, earlierUtc())));
            verify(executor, times(1)).executeUpdate(eq(INSERT_PROPERTY_SQL), argThat(
                    matchArguments(isId(), index.id(), PROPERTY_ID_1)));
            verify(publisher, times(1)).publishEvent(argThat(
                    matchEvent(TaskPropertyIndexedEvent.class, task, PROPERTY_ID_1)));
        }

        @Test
        @DisplayName("当插入索引数据的受影响行数为 0 时，抛出异常")
        void should_throw_when_no_row_affected_when_insert_index() {
            TaskEntity task = mockTask(true);
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            Index.Declaration declaration = Index.Declaration.custom().name(INDEX_NAME)
                    .propertyNames(Collections.singletonList(PROPERTY_NAME_1)).build();

            when(executor.executeUpdate(eq(INSERT_SQL), argThat(matchArguments(isId(), INDEX_NAME, TASK_ID, OPERATOR,
                    earlierUtc(), OPERATOR, earlierUtc())))).thenReturn(0);

            ServerInternalException ex = assertThrows(ServerInternalException.class,
                    () -> repo.create(task, declaration, CONTEXT));
            assertEquals("Failed to insert indexes into database.", ex.getMessage());
        }

        @Test
        @DisplayName("当插入索引属性数据的受影响行数为 0 时，抛出异常")
        void should_throw_when_no_row_affected_when_insert_indexed_properties() {
            TaskEntity task = mockTask(true);
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            Index.Declaration declaration = Index.Declaration.custom().name(INDEX_NAME)
                    .propertyNames(Collections.singletonList(PROPERTY_NAME_1)).build();

            when(executor.executeUpdate(eq(INSERT_SQL), argThat(matchArguments(isId(), INDEX_NAME, TASK_ID, OPERATOR,
                    earlierUtc(), OPERATOR, earlierUtc())))).thenReturn(1);
            when(executor.executeUpdate(eq(INSERT_PROPERTY_SQL), argThat(matchArguments(isId(), isId(),
                    PROPERTY_ID_1)))).thenReturn(0);

            ServerInternalException ex = assertThrows(ServerInternalException.class,
                    () -> repo.create(task, declaration, CONTEXT));
            assertEquals("Failed to insert properties of index into database.", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("测试修改索引")
    class PatchTest {
        @Test
        @DisplayName("当修改的索引的唯一标识格式不正确时，抛出异常")
        void should_throw_when_index_id_is_invalid() {
            TaskEntity task = mockTask(false);
            Index.Declaration declaration = Index.Declaration.custom().name(INDEX_NAME).build();
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.patch(task, INDEX_NAME, declaration, CONTEXT));
            assertEquals(ErrorCodes.INDEX_ID_INVALID.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当修改的索引不在任务定义中时，抛出异常")
        void should_throw_when_index_not_found_in_task() {
            TaskEntity task = mockTask(false);
            Index.Declaration declaration = Index.Declaration.custom().name(INDEX_NAME).build();
            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> repo.patch(task, INDEX_ID, declaration, CONTEXT));
            assertEquals(ErrorCodes.INDEX_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当修改索引时执行 SQL")
        void should_execute_sql_to_patch_index() {
            final String newName = "new-index";
            TaskEntity task = mockTask(true);
            fillIndex(task);
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(executor.executeUpdate(any(), any())).thenReturn(1);
            Index.Declaration declaration = Index.Declaration.custom().name(newName)
                    .propertyNames(Collections.singletonList(PROPERTY_NAME_2)).build();
            assertDoesNotThrow(() -> repo.patch(task, INDEX_ID, declaration, CONTEXT));
            verify(executor, times(1)).executeUpdate(eq(UPDATE_SQL), argThat(matchArguments(
                    OPERATOR, earlierUtc(), newName, INDEX_ID)));
            verify(executor, times(1)).executeUpdate(eq(INSERT_PROPERTY_SQL), argThat(
                    matchArguments(isId(), INDEX_ID, PROPERTY_ID_2)));
            verify(executor, times(1)).executeUpdate(eq(DELETE_PROPERTY_SQL), argThat(
                    matchArguments(INDEX_ID, PROPERTY_ID_1)));

            verify(publisher, times(1)).publishEvent(argThat(
                    matchEvent(TaskPropertyIndexedEvent.class, task, PROPERTY_ID_2)));
            verify(publisher, times(1)).publishEvent(argThat(
                    matchEvent(TaskPropertyUnindexedEvent.class, task, PROPERTY_ID_1)));
        }

        @Test
        @DisplayName("当修改索引时执行 SQL")
        void should_throw_when_no_row_affected_to_update_index() {
            final String newName = "new-index";
            TaskEntity task = mockTask(true);
            fillIndex(task);
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(executor.executeUpdate(any(), any())).thenReturn(0);
            Index.Declaration declaration = Index.Declaration.custom().name(newName).build();
            ServerInternalException ex = assertThrows(ServerInternalException.class,
                    () -> repo.patch(task, INDEX_ID, declaration, CONTEXT));
            assertEquals("Failed to patch index into database.", ex.getMessage());
        }

        @Test
        @DisplayName("当修改索引时执行 SQL")
        void should_throw_when_no_row_affected_to_insert_property() {
            final String newName = "new-index";
            TaskEntity task = mockTask(true);
            fillIndex(task);
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(executor.executeUpdate(eq(UPDATE_SQL), argThat(matchArguments(OPERATOR, earlierUtc(), newName,
                    INDEX_ID)))).thenReturn(1);
            when(executor.executeUpdate(eq(INSERT_PROPERTY_SQL), argThat(matchArguments(isId(), INDEX_ID,
                    PROPERTY_ID_2)))).thenReturn(0);
            Index.Declaration declaration = Index.Declaration.custom().name(newName)
                    .propertyNames(Collections.singletonList(PROPERTY_NAME_2)).build();
            ServerInternalException ex = assertThrows(ServerInternalException.class,
                    () -> repo.patch(task, INDEX_ID, declaration, CONTEXT));
            assertEquals("Failed to insert properties of index into database.", ex.getMessage());
        }

        @Test
        @DisplayName("当删除索引列的受响应的行少于需要删除的属性数量时，抛出异常")
        void should_throw_when_no_row_affected_to_delete_property() {
            final String newName = "new-index";
            TaskEntity task = mockTask(true);
            fillIndex(task);
            when(validator.name(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(executor.executeUpdate(eq(UPDATE_SQL), argThat(matchArguments(OPERATOR, earlierUtc(), newName,
                    INDEX_ID)))).thenReturn(1);
            when(executor.executeUpdate(eq(INSERT_PROPERTY_SQL), argThat(matchArguments(isId(), INDEX_ID,
                    PROPERTY_ID_2)))).thenReturn(1);
            when(executor.executeUpdate(eq(DELETE_PROPERTY_SQL), argThat(
                    matchArguments(INDEX_ID, PROPERTY_ID_1)))).thenReturn(0);
            Index.Declaration declaration = Index.Declaration.custom().name(newName)
                    .propertyNames(Collections.singletonList(PROPERTY_NAME_2)).build();
            ServerInternalException ex = assertThrows(ServerInternalException.class,
                    () -> repo.patch(task, INDEX_ID, declaration, CONTEXT));
            assertEquals("Failed to delete properties of index from database.", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("测试删除索引")
    class DeleteTest {
        @Test
        @DisplayName("删除索引时需要执行 SQL")
        void should_execute_sql_when_delete() {
            TaskEntity task = mockTask(true);
            fillIndex(task);
            when(executor.executeUpdate(any(), any())).thenReturn(1);
            assertDoesNotThrow(() -> repo.delete(task, INDEX_ID, CONTEXT));
            verify(executor, times(1)).executeUpdate(eq(DELETE_SQL), argThat(matchArguments(INDEX_ID)));
            verify(executor, times(1)).executeUpdate(eq(DELETE_ALL_PROPERTIES_SQL), argThat(matchArguments(INDEX_ID)));
            verify(publisher, times(1)).publishEvent(argThat(
                    matchEvent(TaskPropertyUnindexedEvent.class, task, PROPERTY_ID_1)));
        }

        @Test
        @DisplayName("当删除索引的唯一标识格式不正确时，抛出异常")
        void should_throw_when_id_is_invalid() {
            TaskEntity task = mockTask(false);
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> repo.delete(task, INDEX_NAME, CONTEXT));
            assertEquals(ErrorCodes.INDEX_ID_INVALID.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当受影响的行数为 0 时，抛出异常")
        void should_throw_when_no_row_affected_to_delete_sql() {
            TaskEntity task = mockTask(false);
            when(executor.executeUpdate(any(), any())).thenReturn(0);
            NotFoundException ex = assertThrows(NotFoundException.class, () -> repo.delete(task, INDEX_ID, CONTEXT));
            assertEquals(ErrorCodes.INDEX_NOT_FOUND.getErrorCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("测试检索索引")
    class Retrieve {
        @Test
        @DisplayName("检索时执行正确的 SQL")
        void should_return_index() {
            TaskEntity task = mockTask(true);
            LocalDateTime operationTime = LocalDateTime.now();
            List<Map<String, Object>> indexRows = Collections.singletonList(row(INDEX_ID, INDEX_NAME, operationTime));
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INDEX_ID)))).thenReturn(indexRows);
            List<Map<String, Object>> propertyRows = Collections.singletonList(propertyRow(INDEX_ID, PROPERTY_ID_1));
            when(executor.executeQuery(eq(SELECT_PROPERTIES_BY_INDEX_SQL), argThat(matchArguments(INDEX_ID))))
                    .thenReturn(propertyRows);
            Index index = repo.retrieve(task, INDEX_ID, CONTEXT);
            assertEquals(INDEX_ID, index.id());
            assertEquals(INDEX_NAME, index.name());
            assertSame(task, index.task());
            assertEquals(1, index.properties().size());
            assertSame(task.getProperties().get(0), index.properties().get(0));
        }

        @Test
        @DisplayName("当未返回数据行时，抛出异常")
        void should_throw_when_no_row_selected() {
            TaskEntity task = mockTask(true);
            when(executor.executeQuery(eq(SELECT_BY_ID_SQL), argThat(matchArguments(INDEX_ID))))
                    .thenReturn(Collections.emptyList());
            NotFoundException ex = assertThrows(NotFoundException.class, () -> repo.retrieve(task, INDEX_ID, CONTEXT));
            assertEquals(ErrorCodes.INDEX_NOT_FOUND.getErrorCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("测试查询索引")
    class ListTest {
        @Test
        @DisplayName("当未提供任务定义时，返回空的索引列表")
        void should_return_empty_list_when_no_task_specified() {
            List<Index> indexes = repo.list(Collections.singletonList(null), CONTEXT);
            assertTrue(indexes.isEmpty());
            verify(executor, times(0)).executeQuery(any(), any());
        }

        @Test
        @DisplayName("当查询到的数据集为空时，返回空的索引列表")
        void should_return_empty_list_when_no_rows_selected() {
            TaskEntity task = mockTask(false);
            when(executor.executeQuery(any(), any())).thenReturn(Collections.emptyList());
            List<Index> indexes = repo.list(Collections.singletonList(task), CONTEXT);
            assertTrue(indexes.isEmpty());
        }

        @Test
        @DisplayName("执行 SQL 以查询索引")
        void should_execute_sql_when_list_indexes() {
            TaskEntity task = mockTask(true);
            LocalDateTime operationTime = LocalDateTime.now();
            LocalDateTime operationTimeUtc = Dates.toUtc(operationTime);
            List<Map<String, Object>> rows = Arrays.asList(
                    row("ad30289f1a6147bd8d48932f37376e6b", "index-01", operationTimeUtc),
                    row("8ba60c23e2374890adb6c374b7da43cc", "index-02", operationTimeUtc));
            when(executor.executeQuery(eq(SELECT_INDEX_SQL), argThat(matchArguments(TASK_ID)))).thenReturn(rows);
            List<Map<String, Object>> propertyRows = Arrays.asList(
                    propertyRow("ad30289f1a6147bd8d48932f37376e6b", PROPERTY_ID_1),
                    propertyRow("ad30289f1a6147bd8d48932f37376e6b", PROPERTY_ID_2),
                    propertyRow("8ba60c23e2374890adb6c374b7da43cc", PROPERTY_ID_1));
            when(executor.executeQuery(eq(SELECT_INDEX_PROPERTY_SQL), argThat(matchArguments(
                    "ad30289f1a6147bd8d48932f37376e6b", "8ba60c23e2374890adb6c374b7da43cc"))))
                    .thenReturn(propertyRows);
            List<Index> indexes = repo.list(task, CONTEXT);
            assertEquals(2, indexes.size());
            assertIndex(indexes.get(0), "ad30289f1a6147bd8d48932f37376e6b", "index-01", operationTime);
            assertIterableEquals(task.getProperties(), indexes.get(0).properties());
            assertIndex(indexes.get(1), "8ba60c23e2374890adb6c374b7da43cc", "index-02", operationTime);
            assertIterableEquals(Collections.singletonList(task.getProperties().get(0)), indexes.get(1).properties());
        }
    }

    @Nested
    @DisplayName("测试保存索引")
    class SaveTest {
        @Test
        @DisplayName("保存索引时执行正确的 SQL")
        void should_execute_sql() {
            TaskEntity task = mockTask(true);
            fillIndex(task);
            List<Index.Declaration> declarations = Arrays.asList(
                    declaration("new-index", Collections.singletonList(PROPERTY_NAME_1)),
                    declaration(INDEX_NAME, Collections.singletonList(PROPERTY_NAME_2)));

            /*
            当前索引：INDEX_NAME(tp1)
            目标索引：INDEX_NAME(tp2), new-index(tp1)
            预期结果；
            1. 更新索引INDEX_NAME的最后修改人和修改时间
            2. 删除（INDEX_NAME,tp1）关系
            3. 创建（new-index）索引
            4. 创建（INDEX_NAME,tp2）、（new-index,tp1）关系
             */

            when(executor.executeUpdate(eq(UPDATE_NONE_SQL), argThat(matchArguments(OPERATOR, earlierUtc(), INDEX_ID))))
                    .thenReturn(1);
            when(executor.executeUpdate(eq(DELETE_PROPERTY_SQL), argThat(matchArguments(INDEX_ID, PROPERTY_ID_1))))
                    .thenReturn(1);
            when(executor.executeUpdate(eq(INSERT_SQL), argThat(matchArguments(isId(), "new-index", TASK_ID,
                    OPERATOR, earlierUtc(), OPERATOR, earlierUtc())))).thenReturn(1);
            when(executor.executeUpdate(eq(INSERT_PROPERTY_SQL_2), argThat(matchArguments(isId(), isId(),
                    PROPERTY_ID_1, isId(), isId(), PROPERTY_ID_2)))).thenReturn(2);

            when(validator.name(any())).thenAnswer(invocation -> invocation.getArgument(0));

            repo.save(task, declarations, CONTEXT);
        }
    }

    private static Map<String, Object> row(String id, String name, LocalDateTime operationTime) {
        return MapBuilder.<String, Object>get().put("id", id).put("name", name).put("task_id", TASK_ID)
                .put("created_by", OPERATOR).put("created_at", operationTime).put("updated_by", OPERATOR)
                .put("updated_at", operationTime).build();
    }

    private static void assertIndex(Index index, String id, String name, LocalDateTime operationTime) {
        assertEquals(id, index.id());
        assertEquals(name, index.name());
        assertNotNull(index.task());
        assertEquals(TASK_ID, index.task().getId());
        assertEquals(OPERATOR, index.creator());
        assertEquals(operationTime, index.creationTime());
        assertEquals(OPERATOR, index.lastModifier());
        assertEquals(operationTime, index.lastModificationTime());
    }

    private static Map<String, Object> propertyRow(String indexId, String propertyId) {
        return MapBuilder.<String, Object>get().put("id", Entities.generateId()).put("index_id", indexId)
                .put("property_id", propertyId).build();
    }

    private static Index.Declaration declaration(String name, List<String> propertyIds) {
        return Index.Declaration.custom().name(name).propertyNames(propertyIds).build();
    }

    private static <T extends TaskPropertyEvent> ArgumentMatcher<T> matchEvent(Class<T> eventClass, TaskEntity task,
            String propertyId) {
        return event -> {
            if (eventClass.isInstance(event)) {
                T actual = eventClass.cast(event);
                return actual.property().id().equals(propertyId);
            } else {
                return false;
            }
        };
    }
}