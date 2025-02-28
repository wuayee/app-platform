/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("测试任务实例数据模型")
class TaskInstanceTest {
    private static final String ID = "cc8aad39116a4bbcb25d174d0e37323d";

    private static final TaskType TYPE = mock(TaskType.class);

    private static final SourceEntity SOURCE = mock(SourceEntity.class);

    private static final Map<String, Object> INFO = new HashMap<>();

    private static final List<String> TAGS = new ArrayList<>();

    private static final List<String> CATEGORIES = new ArrayList<>();

    @Test
    @DisplayName("返回的任务实例中应包含正确的数据")
    void should_return_instance() {
        TaskInstance instance = TaskInstance.custom().id(ID).type(TYPE).source(SOURCE).info(INFO)
                .tags(TAGS).categories(CATEGORIES).build();
        assertSame(ID, instance.id());
        assertSame(TYPE, instance.type());
        assertSame(SOURCE, instance.source());
        assertSame(INFO, instance.info());
        assertSame(TAGS, instance.tags());
        assertSame(CATEGORIES, instance.categories());
    }

    @Test
    @DisplayName("当任务实例中包含相同数据时返回 true")
    void should_return_true_when_instances_contains_same_data() {
        TaskInstance instance1 = TaskInstance.custom().id(ID).type(TYPE).source(SOURCE).info(INFO)
                .tags(TAGS).categories(CATEGORIES).build();
        TaskInstance instance2 = TaskInstance.custom().id(ID).type(TYPE).source(SOURCE).info(INFO)
                .tags(TAGS).categories(CATEGORIES).build();
        assertEquals(instance1, instance2);
    }

    @Test
    @DisplayName("当任务实例中包含相同数据时，返回相同的哈希值")
    void should_return_same_hash_when_instances_contains_same_data() {
        TaskInstance instance1 = TaskInstance.custom().id(ID).type(TYPE).source(SOURCE).info(INFO)
                .tags(TAGS).categories(CATEGORIES).build();
        TaskInstance instance2 = TaskInstance.custom().id(ID).type(TYPE).source(SOURCE).info(INFO)
                .tags(TAGS).categories(CATEGORIES).build();
        assertEquals(instance1.hashCode(), instance2.hashCode());
    }

    @Test
    @DisplayName("返回友好的任务实例")
    void should_return_string() {
        TaskEntity task = mock(TaskEntity.class);
        when(task.toString()).thenReturn("<TASK>");

        TaskType type = mock(TaskType.class);
        when(type.toString()).thenReturn("<TASK_TYPE>");

        SourceEntity source = mock(SourceEntity.class);
        when(source.toString()).thenReturn("<TASK_SOURCE>");

        TaskInstance instance = TaskInstance.custom().id(ID).task(task).type(type).source(source).info(INFO)
                .tags(TAGS).categories(CATEGORIES).build();

        String expected
                = "[id=cc8aad39116a4bbcb25d174d0e37323d, task=<TASK>, type=<TASK_TYPE>, source=<TASK_SOURCE>, info={}, tags=[], categories=[]]";
        String actual = instance.toString();
        assertEquals(expected, actual);
    }

    @Nested
    @DisplayName("测试声明")
    class DeclarationTest {
        @Test
        @DisplayName("返回的声明中已定义了任务类型")
        void should_return_declaration_with_type() {
            String typeId = "016145da0fc7402d89e021a061bcaa10";
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().type(typeId).build();
            assertTrue(declaration.typeId().defined());
            assertEquals(typeId, declaration.typeId().get());
            assertFalse(declaration.sourceId().defined());
            assertFalse(declaration.info().defined());
            assertFalse(declaration.tags().defined());
        }

        @Test
        @DisplayName("返回的声明中已定义了任务数据源")
        void should_return_declaration_with_source() {
            String sourceId = "e84c4a955ab8482fb5660776e16e0cd0";
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().source(sourceId).build();
            assertFalse(declaration.typeId().defined());
            assertTrue(declaration.sourceId().defined());
            assertEquals(sourceId, declaration.sourceId().get());
            assertFalse(declaration.info().defined());
            assertFalse(declaration.tags().defined());
        }

        @Test
        @DisplayName("返回的声明中已定义了数据内容")
        void should_return_declaration_with_info() {
            Map<String, Object> info = new HashMap<>();
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().info(info).build();
            assertFalse(declaration.typeId().defined());
            assertFalse(declaration.sourceId().defined());
            assertTrue(declaration.info().defined());
            assertSame(info, declaration.info().get());
            assertFalse(declaration.tags().defined());
        }

        @Test
        @DisplayName("返回的声明中已定义了标签")
        void should_return_declaration_with_tags() {
            List<String> tags = new ArrayList<>();
            TaskInstance.Declaration declaration = TaskInstance.Declaration.custom().tags(tags).build();
            assertFalse(declaration.typeId().defined());
            assertFalse(declaration.sourceId().defined());
            assertFalse(declaration.info().defined());
            assertTrue(declaration.tags().defined());
            assertSame(tags, declaration.tags().get());
        }
    }

    @Nested
    @DisplayName("测试查询条件")
    class FilterTest {
        @Test
        @DisplayName("返回的查询条件中包含唯一标识信息")
        void should_return_filter_with_ids() {
            String id = "ce469b1b742c499681d97fc3f6b9f7e2";
            TaskInstance.Filter filter = TaskInstance.Filter.custom().ids(Collections.singletonList(id)).build();
            assertEquals(1, filter.ids().size());
            assertEquals(id, filter.ids().get(0));
            assertTrue(filter.typeIds().isEmpty());
            assertTrue(filter.sourceIds().isEmpty());
            assertTrue(filter.infos().isEmpty());
            assertTrue(filter.tags().isEmpty());
            assertTrue(filter.categories().isEmpty());
        }

        @Test
        @DisplayName("返回的查询条件中包含类型唯一标识信息")
        void should_return_filter_with_type_ids() {
            String id = "652b83eeba7e4e41805e729183e5beb7";
            TaskInstance.Filter filter = TaskInstance.Filter.custom().typeIds(Collections.singletonList(id)).build();
            assertTrue(filter.ids().isEmpty());
            assertEquals(1, filter.typeIds().size());
            assertEquals(id, filter.typeIds().get(0));
            assertTrue(filter.sourceIds().isEmpty());
            assertTrue(filter.infos().isEmpty());
            assertTrue(filter.tags().isEmpty());
            assertTrue(filter.categories().isEmpty());
        }

        @Test
        @DisplayName("返回的查询条件中包含数据源唯一标识信息")
        void should_return_filter_with_source_ids() {
            String id = "bc998b32852147a08317068109c08402";
            TaskInstance.Filter filter = TaskInstance.Filter.custom().sourceIds(Collections.singletonList(id)).build();
            assertTrue(filter.ids().isEmpty());
            assertTrue(filter.typeIds().isEmpty());
            assertEquals(1, filter.sourceIds().size());
            assertEquals(id, filter.sourceIds().get(0));
            assertTrue(filter.infos().isEmpty());
            assertTrue(filter.tags().isEmpty());
            assertTrue(filter.categories().isEmpty());
        }

        @Test
        @DisplayName("返回的查询条件中包含内容数据信息")
        void should_return_filter_with_infos() {
            String key = "name";
            String value = "jane";
            TaskInstance.Filter filter = TaskInstance.Filter.custom()
                    .infos(Collections.singletonMap(key, Collections.singletonList(value))).build();
            assertTrue(filter.ids().isEmpty());
            assertTrue(filter.typeIds().isEmpty());
            assertTrue(filter.sourceIds().isEmpty());
            assertEquals(1, filter.infos().size());
            List<String> values = filter.infos().get(key);
            assertNotNull(values);
            assertEquals(1, values.size());
            assertEquals(value, values.get(0));
            assertTrue(filter.tags().isEmpty());
            assertTrue(filter.categories().isEmpty());
        }

        @Test
        @DisplayName("返回的查询条件中包含标签信息")
        void should_return_filter_with_tags() {
            String tag = "jane";
            TaskInstance.Filter filter = TaskInstance.Filter.custom().tags(Collections.singletonList(tag)).build();
            assertTrue(filter.ids().isEmpty());
            assertTrue(filter.typeIds().isEmpty());
            assertTrue(filter.sourceIds().isEmpty());
            assertTrue(filter.infos().isEmpty());
            assertEquals(1, filter.tags().size());
            assertEquals(tag, filter.tags().get(0));
            assertTrue(filter.categories().isEmpty());
        }

        @Test
        @DisplayName("返回的查询条件中包含类目信息")
        void should_return_filter_with_categories() {
            String category = "已完成";
            TaskInstance.Filter filter = TaskInstance.Filter.custom()
                    .categories(Collections.singletonList(category)).build();
            assertTrue(filter.ids().isEmpty());
            assertTrue(filter.typeIds().isEmpty());
            assertTrue(filter.sourceIds().isEmpty());
            assertTrue(filter.infos().isEmpty());
            assertTrue(filter.tags().isEmpty());
            assertEquals(1, filter.categories().size());
            assertEquals(category, filter.categories().get(0));
        }
    }
}
