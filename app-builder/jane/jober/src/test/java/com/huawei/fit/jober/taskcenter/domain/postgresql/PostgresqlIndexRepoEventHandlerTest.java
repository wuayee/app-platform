/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fit.jober.Tests.matchArguments;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyDeletingEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyIndexedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyModifiedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyModifyingEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyUnindexedEvent;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fitframework.event.Event;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@DisplayName("测试基于 Postgresql 的索引持久化中的事件处理逻辑")
class PostgresqlIndexRepoEventHandlerTest {
    private static final String TASK_ID = "e90c6517762347f18ccfa8f00d69dd57";

    private static final String PROPERTY_ID = "18efdabb87bd436bb191001dbf392455";

    private static final String PROPERTY_COLUMN = "text_1";

    private static final String BUILD_INDEX_SQL;

    private static final String BUILD_LIST_INDEX_SQL;

    private static final String REMOVE_INDEX_SQL;

    private static final String COUNT_PROPERTIES_BY_INDEX_SQL;

    private static final String REMOVE_PROPERTY_SQL;

    private static final String DELETE_INDEX_SQL;

    private static final String DELETE_VALUES_BY_PROPERTY_SQL;

    private static final String INSERT_VALUES_BY_PROPERTY_SQL;

    private static final String SELECT_TASK_ID_BY_PROPERTY_SQL;

    private static final String CHECK_PROPERTY_INDEXED;

    static {
        ClassLoader loader = PostgresqlIndexRepoEventHandlerTest.class.getClassLoader();
        try {
            BUILD_INDEX_SQL = IoUtils.content(loader, "sql/index-build.sql");
            BUILD_LIST_INDEX_SQL = IoUtils.content(loader, "sql/index-build-list.sql");
            REMOVE_INDEX_SQL = IoUtils.content(loader, "sql/index-remove.sql");
            COUNT_PROPERTIES_BY_INDEX_SQL = IoUtils.content(loader, "sql/index-property-count-by-index.sql");
            REMOVE_PROPERTY_SQL = IoUtils.content(loader, "sql/index-remove-property.sql");
            DELETE_INDEX_SQL = IoUtils.content(loader, "sql/index-delete.sql");
            DELETE_VALUES_BY_PROPERTY_SQL = IoUtils.content(loader, "sql/index-delete-values-by-property.sql");
            INSERT_VALUES_BY_PROPERTY_SQL = IoUtils.content(loader, "sql/index-insert-values-by-property.sql");
            SELECT_TASK_ID_BY_PROPERTY_SQL = IoUtils.content(loader, "sql/select-task-id-by-property.sql");
            CHECK_PROPERTY_INDEXED = IoUtils.content(loader, "sql/index-property-indexed.sql");
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load SQLs from embedded resources.", ex);
        }
    }

    private DynamicSqlExecutor executor;

    private TaskEntity task;

    private TaskProperty property;

    @BeforeEach
    void setup() {
        this.executor = mock(DynamicSqlExecutor.class);
        this.task = new TaskEntity();
        this.task.setId(TASK_ID);
        this.property = mock(TaskProperty.class);
    }

    @Nested
    @DisplayName("测试属性建立索引的事件处理程序")
    class IndexedTest {
        private PostgresqlIndexRepo.IndexedEventHandler handler;

        @BeforeEach
        void setup() {
            this.handler = new PostgresqlIndexRepo.IndexedEventHandler(executor);
        }

        @Test
        @DisplayName("当属性的数据类型不支持索引时抛出异常")
        void should_throw_when_data_type_does_not_support_index() {
            when(property.dataType()).thenReturn(PropertyDataType.BOOLEAN);
            TaskPropertyIndexedEvent event = new TaskPropertyIndexedEvent(null, task, property);
            ServerInternalException ex = assertThrows(ServerInternalException.class,
                    () -> this.handler.handleEvent(event));
            assertEquals("Unsupported data type to build index.", ex.getMessage());
        }

        @Test
        @DisplayName("当建立索引时执行 SQL 填充索引数据")
        void should_execute_sql_to_build_index_values() {
            when(property.id()).thenReturn(PROPERTY_ID);
            when(property.dataType()).thenReturn(PropertyDataType.TEXT);
            when(property.column()).thenReturn(PROPERTY_COLUMN);
            TaskPropertyIndexedEvent event = new TaskPropertyIndexedEvent(null, task, property);
            this.handler.handleEvent(event);
            verify(executor, times(1)).executeUpdate(eq(BUILD_INDEX_SQL),
                    argThat(matchArguments(PROPERTY_ID, TASK_ID)));
        }

        @Test
        @DisplayName("当在列表属性上建立索引时，执行 SQL 填充索引数据")
        void should_execute_sql_to_build_sql_values_in_list_type() {
            when(property.id()).thenReturn(PROPERTY_ID);
            when(property.dataType()).thenReturn(PropertyDataType.LIST_TEXT);
            when(property.column()).thenReturn(PROPERTY_COLUMN);
            TaskPropertyIndexedEvent event = new TaskPropertyIndexedEvent(null, task, property);
            this.handler.handleEvent(event);
            verify(executor, times(1)).executeUpdate(eq(BUILD_LIST_INDEX_SQL),
                    argThat(matchArguments(PROPERTY_ID)));
        }
    }

    @Nested
    @DisplayName("测试属性移除索引的事件处理程序")
    class UnindexedTest {
        private PostgresqlIndexRepo.UnindexedEventHandler handler;

        @BeforeEach
        void setup() {
            this.handler = new PostgresqlIndexRepo.UnindexedEventHandler(executor);
        }

        @Test
        @DisplayName("当数据类型不支持索引时不做任何事情")
        void should_do_nothing_when_data_type_does_not_support_index() {
            when(property.dataType()).thenReturn(PropertyDataType.BOOLEAN);
            TaskPropertyUnindexedEvent event = new TaskPropertyUnindexedEvent(null, task, property);
            assertDoesNotThrow(() -> this.handler.handleEvent(event));
            verify(executor, times(0)).executeUpdate(any(), any());
        }

        @Test
        @DisplayName("当移除索引时执行 SQL 清除索引数据")
        void should_execute_sql_to_remove_index_values() {
            when(property.id()).thenReturn(PROPERTY_ID);
            when(property.dataType()).thenReturn(PropertyDataType.TEXT);
            when(property.column()).thenReturn(PROPERTY_COLUMN);
            TaskPropertyUnindexedEvent event = new TaskPropertyUnindexedEvent(null, task, property);
            this.handler.handleEvent(event);
            verify(executor, times(1)).executeUpdate(eq(REMOVE_INDEX_SQL), argThat(
                    matchArguments(PROPERTY_ID)));
        }
    }

    @Nested
    @DisplayName("测试属性被修改前的事件处理程序")
    class PropertyModifyingTest {
        private PostgresqlIndexRepo.PropertyModifyingEventHandler handler;

        private Plugin plugin;

        @BeforeEach
        void setup() {
            this.plugin = mock(Plugin.class);
            TaskService taskService = mock(TaskService.class);
            this.handler = new PostgresqlIndexRepo.PropertyModifyingEventHandler(executor, this.plugin, taskService);
        }

        @Test
        @DisplayName("当任务属性用作索引，且修改后的数据类型不支持索引时，执行 SQL 以将属性从索引中移除")
        void should_execute_sql_to_remove_property_from_index() {
            when(property.id()).thenReturn(PROPERTY_ID);
            when(property.dataType()).thenReturn(PropertyDataType.TEXT);
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().dataType("BOOLEAN").build();
            TaskPropertyModifyingEvent event = new TaskPropertyModifyingEvent(null, property, declaration);

            final String index1Id = "ff198592ef484b35883655358ffa2980";
            final String index2Id = "b04a0336ee1342f28753b6cc46bb9432";

            when(executor.executeQuery(eq(COUNT_PROPERTIES_BY_INDEX_SQL), argThat(matchArguments(PROPERTY_ID))))
                    .thenReturn(Arrays.asList(countRow(index1Id, 1), countRow(index2Id, 2)));
            when(executor.executeUpdate(eq(DELETE_INDEX_SQL), argThat(matchArguments(index1Id)))).thenReturn(1);
            EventPublisher publisher = mockPublisher(plugin);

            this.handler.handleEvent(event);

            verify(publisher, times(1)).publishEvent(argThat(matchPropertyEvent(
                    TaskPropertyUnindexedEvent.class, property)));
            verify(executor, times(1)).executeUpdate(eq(REMOVE_PROPERTY_SQL), argThat(matchArguments(PROPERTY_ID)));
        }

        @Test
        @DisplayName("当数据类型未被修改时，不进行任何操作")
        void should_do_nothing_when_data_type_not_modified() {
            when(property.dataType()).thenReturn(PropertyDataType.TEXT);
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().build();
            TaskPropertyModifyingEvent event = new TaskPropertyModifyingEvent(null, property, declaration);
            this.handler.handleEvent(event);
            verify(executor, times(0)).executeUpdate(any(), any());
            verify(executor, times(0)).executeScalar(any(), any());
            verify(executor, times(0)).executeQuery(any(), any());
        }

        @Test
        @DisplayName("当数据类型未被修改时，不进行任何操作")
        void should_do_nothing_when_property_is_not_indexable() {
            PropertyDataType dataType = mock(PropertyDataType.class);
            when(dataType.indexable()).thenReturn(false);
            when(property.dataType()).thenReturn(dataType);
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().dataType("BOOLEAN").build();
            TaskPropertyModifyingEvent event = new TaskPropertyModifyingEvent(null, property, declaration);
            this.handler.handleEvent(event);
            verify(executor, times(0)).executeUpdate(any(), any());
            verify(executor, times(0)).executeScalar(any(), any());
            verify(executor, times(0)).executeQuery(any(), any());
        }
    }

    @Nested
    @DisplayName("测试属性被修改后的事件处理程序")
    class PropertyModifiedTest {
        private PostgresqlIndexRepo.PropertyModifiedEventHandler handler;

        @BeforeEach
        void setup() {
            this.handler = new PostgresqlIndexRepo.PropertyModifiedEventHandler(executor);
        }

        @Test
        @DisplayName("当数据类型变化时，执行 SQL 以重建索引")
        void should_execute_sql_to_rebuild_index_when_data_type_modified() {
            TaskProperty oldProperty = mock(TaskProperty.class);
            when(oldProperty.id()).thenReturn(PROPERTY_ID);
            when(oldProperty.dataType()).thenReturn(PropertyDataType.TEXT);
            TaskProperty newProperty = mock(TaskProperty.class);
            when(newProperty.id()).thenReturn(PROPERTY_ID);
            when(newProperty.dataType()).thenReturn(PropertyDataType.INTEGER);
            when(newProperty.column()).thenReturn("integer_1");
            when(executor.executeScalar(eq(SELECT_TASK_ID_BY_PROPERTY_SQL),
                    argThat(matchArguments(PROPERTY_ID)))).thenReturn(TASK_ID);
            when(executor.executeScalar(eq(CHECK_PROPERTY_INDEXED),
                    argThat(matchArguments(PROPERTY_ID)))).thenReturn(1);
            TaskPropertyModifiedEvent event = new TaskPropertyModifiedEvent(null, newProperty, oldProperty);
            this.handler.handleEvent(event);
            verify(executor, times(1)).executeUpdate(eq(DELETE_VALUES_BY_PROPERTY_SQL),
                    argThat(matchArguments(PROPERTY_ID)));
            verify(executor, times(1)).executeUpdate(eq(INSERT_VALUES_BY_PROPERTY_SQL),
                    argThat(matchArguments(PROPERTY_ID, TASK_ID)));
        }

        @Test
        @DisplayName("当原始属性不支持索引时，不进行任何操作")
        void should_do_nothing_when_old_property_is_not_indexable() {
            PropertyDataType oldDataType = mock(PropertyDataType.class);
            when(oldDataType.indexable()).thenReturn(false);
            TaskProperty oldProperty = mock(TaskProperty.class);
            when(oldProperty.dataType()).thenReturn(oldDataType);
            TaskProperty newProperty = mock(TaskProperty.class);
            TaskPropertyModifiedEvent event = new TaskPropertyModifiedEvent(null, newProperty, oldProperty);
            this.handler.handleEvent(event);
            verify(executor, times(0)).executeUpdate(any(), any());
            verify(executor, times(0)).executeScalar(any(), any());
            verify(executor, times(0)).executeQuery(any(), any());
        }

        @Test
        @DisplayName("当属性的数据类型未发生变化时，不进行任何操作")
        void should_do_nothing_when_data_type_is_not_modified() {
            PropertyDataType dataType = mock(PropertyDataType.class);
            when(dataType.indexable()).thenReturn(true);
            TaskProperty oldProperty = mock(TaskProperty.class);
            when(oldProperty.dataType()).thenReturn(dataType);
            TaskProperty newProperty = mock(TaskProperty.class);
            when(newProperty.dataType()).thenReturn(dataType);
            TaskPropertyModifiedEvent event = new TaskPropertyModifiedEvent(null, newProperty, oldProperty);
            this.handler.handleEvent(event);
            verify(executor, times(0)).executeUpdate(any(), any());
            verify(executor, times(0)).executeScalar(any(), any());
            verify(executor, times(0)).executeQuery(any(), any());
        }

        @Test
        @DisplayName("当属性未被用作索引时，不进行任何操作")
        void should_do_nothing_when_property_is_not_indexed() {
            PropertyDataType oldDataType = mock(PropertyDataType.class);
            when(oldDataType.indexable()).thenReturn(true);
            PropertyDataType newDataType = mock(PropertyDataType.class);
            when(newDataType.indexable()).thenReturn(true);
            TaskProperty oldProperty = mock(TaskProperty.class);
            when(oldProperty.dataType()).thenReturn(oldDataType);
            TaskProperty newProperty = mock(TaskProperty.class);
            when(newProperty.id()).thenReturn(PROPERTY_ID);
            when(newProperty.dataType()).thenReturn(newDataType);
            when(executor.executeScalar(eq(CHECK_PROPERTY_INDEXED), argThat(matchArguments(PROPERTY_ID)))).thenReturn(
                    0);
            TaskPropertyModifiedEvent event = new TaskPropertyModifiedEvent(null, newProperty, oldProperty);
            this.handler.handleEvent(event);
            verify(executor, times(0)).executeUpdate(any(), any());
        }
    }

    @Nested
    @DisplayName("测试属性被删除前的事件处理程序")
    class PropertyDeletingTest {
        private PostgresqlIndexRepo.PropertyDeletingEventHandler handler;

        private Plugin plugin;

        @BeforeEach
        void setup() {
            this.plugin = mock(Plugin.class);
            TaskService taskService = mock(TaskService.class);
            this.handler = new PostgresqlIndexRepo.PropertyDeletingEventHandler(executor, this.plugin, taskService);
        }

        @Test
        @DisplayName("当删除的属性不能用于索引时，不进行任何操作")
        void should_do_nothing_when_property_is_not_indexable() {
            PropertyDataType dataType = mock(PropertyDataType.class);
            when(dataType.indexable()).thenReturn(false);
            when(property.dataType()).thenReturn(dataType);
            TaskPropertyDeletingEvent event = new TaskPropertyDeletingEvent(null, property);
            this.handler.handleEvent(event);
            verify(executor, times(0)).executeUpdate(any(), any());
            verify(executor, times(0)).executeScalar(any(), any());
            verify(executor, times(0)).executeQuery(any(), any());
        }

        @Test
        @DisplayName("当删除的属性未用于索引时，不进行任何操作")
        void should_do_nothing_when_property_is_not_indexed() {
            PropertyDataType dataType = mock(PropertyDataType.class);
            when(dataType.indexable()).thenReturn(true);
            when(property.dataType()).thenReturn(dataType);
            when(property.id()).thenReturn(PROPERTY_ID);
            TaskPropertyDeletingEvent event = new TaskPropertyDeletingEvent(null, property);

            when(executor.executeQuery(eq(COUNT_PROPERTIES_BY_INDEX_SQL), argThat(matchArguments(PROPERTY_ID))))
                    .thenReturn(Collections.emptyList());
            this.handler.handleEvent(event);
            verify(executor, times(0)).executeUpdate(any(), any());
            verify(executor, times(0)).executeScalar(any(), any());
        }

        @Test
        @DisplayName("执行 SQL 以将属性从索引中移除")
        void should_execute_sql_to_unindex_property() {
            PropertyDataType dataType = mock(PropertyDataType.class);
            when(dataType.indexable()).thenReturn(true);
            when(dataType.tableOfIndex()).thenReturn("index_text");
            when(property.dataType()).thenReturn(dataType);
            when(property.id()).thenReturn(PROPERTY_ID);
            TaskPropertyDeletingEvent event = new TaskPropertyDeletingEvent(null, property);

            final String index1Id = "6bdd441092fc463da4e04fbe37ba2eb5";
            final String index2Id = "3a5e834c3934446ba8e56c4a0e9e1cb1";

            when(executor.executeQuery(eq(COUNT_PROPERTIES_BY_INDEX_SQL), argThat(matchArguments(PROPERTY_ID))))
                    .thenReturn(Arrays.asList(countRow(index1Id, 1), countRow(index2Id, 2)));
            when(executor.executeUpdate(eq(DELETE_INDEX_SQL), argThat(matchArguments(index1Id)))).thenReturn(1);
            EventPublisher publisher = mockPublisher(plugin);

            this.handler.handleEvent(event);
            verify(executor, times(1)).executeUpdate(eq(REMOVE_PROPERTY_SQL), argThat(matchArguments(PROPERTY_ID)));
            verify(publisher, times(1)).publishEvent(argThat(
                    matchPropertyEvent(TaskPropertyUnindexedEvent.class, property)));
        }
    }

    private static Map<String, Object> countRow(String indexId, long count) {
        return MapBuilder.<String, Object>get()
                .put("index_id", indexId)
                .put("count", count)
                .build();
    }

    private static EventPublisher mockPublisher(Plugin plugin) {
        EventPublisher publisher = mock(EventPublisher.class);
        when(plugin.publisherOfEvents()).thenReturn(publisher);
        return publisher;
    }

    private static <T extends TaskPropertyEvent> ArgumentMatcher<Event> matchPropertyEvent(Class<T> eventClass,
            TaskProperty property) {
        return event -> eventClass.isInstance(event) && eventClass.cast(event).property() == property;
    }
}
