/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.task.domain.PropertyDataType;
import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.PagedResultSet;
import modelengine.fit.jane.task.util.Pagination;
import modelengine.fit.jane.task.util.PaginationResult;

import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.SourceType;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.domain.TaskType;
import modelengine.fit.jober.taskcenter.service.CategoryService;
import modelengine.fit.jober.taskcenter.service.TagService;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.sql.OrderBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 通过模板id获取任务测试类
 *
 * @author 罗书强
 * @since 2024-02-06
 */
@ExtendWith(MockitoExtension.class)
class TaskAgendaServiceImplTest {
    @Mock
    private DynamicSqlExecutor mockExecutor;

    @Mock
    private TagService mockTagService;

    @Mock
    private CategoryService mockCategoryService;

    private TaskAgendaServiceImpl taskAgendaServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        taskAgendaServiceImplUnderTest = new TaskAgendaServiceImpl(mockExecutor, mockTagService, mockCategoryService);
    }

    @Test
    @DisplayName("测试获取个人所有待办任务")
    void testListAllAgenda() {
        // Setup
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("info_owner", "wx12345");
        row.put("info_title", Collections.singletonList("测试"));
        row.put("task_id", "12345");
        rows.add(row);
        String propertySql =
                "SELECT name, (LOWER(data_type) || '_' || sequence) AS info FROM task_template_property WHERE task_template_id "
                        + "IN (SELECT find_template_parents(?) as id)";
        String sql =
                "SELECT ins.id, ins.task_id, ins.source_id, ins.task_type_id, \"ins\".\"text_4\" AS \"info_owner\" FROM "
                        + "\"task_instance_wide\" AS \"ins\" WHERE \"ins\".\"task_id\" = ? AND \"ins\".\"id\" IN (SELECT DISTINCT "
                        + "\"instance_id\" FROM \"list_text\" WHERE \"property_id\" IN (SELECT \"id\" FROM \"task_property\" WHERE "
                        + "\"task_id\" IN (SELECT \"id\" FROM \"task\" WHERE \"template_id\" IN (SELECT find_template_children(?) "
                        + "AS id)))  AND \"value\" LIKE ? ESCAPE '\\') ORDER BY \"ins\".\"datetime_1\" DESC OFFSET ? LIMIT ?";
        List<Object> args = new LinkedList<>();
        args.add("12345");
        args.add("templateId");
        args.add("%wx12345%");
        args.add(0L);
        args.add(0);
        MockResult mockResult = getMockResult();
        Map<String, List<String>> map = new HashMap<>();
        when(mockCategoryService.listUsages(anyString(), anyList(), any())).thenReturn(map);
        when(mockTagService.list(anyString(), anyList(), any())).thenReturn(new HashMap<>());
        when(mockExecutor.executeQuery(propertySql, Collections.singletonList("templateId"))).thenReturn(
                mockResult.row);
        when(mockExecutor.executeQuery(sql, args)).thenReturn(rows);
        when(mockExecutor.executeScalar(any(), any())).thenReturn(1L);
        // Run the test
        final PagedResultSet<TaskInstance> result = taskAgendaServiceImplUnderTest.listAllAgenda(mockResult.filter,
                mockResult.pagination, "templateId", mockResult.context, mockResult.taskEntityList,
                mockResult.orderBys);
        // Verify the results
        assertEquals(PagedResultSet.create(result.results(), PaginationResult.create(result.pagination(), 1)), result);
    }

    @Test
    @DisplayName("测试获取个人任务id")
    void testListTaskIds() {
        // Setup
        String propertySql =
                "SELECT name, (LOWER(data_type) || '_' || sequence) AS info FROM task_template_property WHERE task_template_id "
                        + "IN (SELECT find_template_parents(?) as id)";
        MockResult mockResult = getMockResult();
        when(mockExecutor.executeQuery(any(), any())).thenReturn(mockResult.taskIds);
        when(mockExecutor.executeQuery(propertySql, Collections.singletonList("templateId"))).thenReturn(
                mockResult.row);
        // Run the test
        final List<String> result = taskAgendaServiceImplUnderTest.listTaskIds(mockResult.filter, mockResult.pagination,
                "templateId", mockResult.context, mockResult.orderBys);

        // Verify the results
        assertEquals(Collections.singletonList("123456"), result);
    }

    @Test
    @DisplayName("测试无任务id返回")
    void testListTaskIds_DynamicSqlExecutorReturnsNoItems() {
        // Setup
        MockResult mockResult = getMockResult();
        when(mockExecutor.executeQuery(any(), any())).thenReturn(Collections.emptyList());
        // Run the test
        assertThrows(BadRequestException.class,
                () -> taskAgendaServiceImplUnderTest.listTaskIds(mockResult.filter, mockResult.pagination, "templateId",
                        mockResult.context, mockResult.orderBys));
    }

    private static MockResult getMockResult() {
        Map<String, List<String>> info = new HashMap<>();
        info.put("owner", Collections.singletonList("wx12345"));
        TaskInstance.Filter filter = TaskInstance.Filter.custom()
                .infos(info)
                .categories(Collections.singletonList("未开始"))
                .tags(Collections.singletonList("source"))
                .deleted(false)
                .build();
        List<Map<String, Object>> taskIds = new ArrayList<>();
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("task_id", "123456");
        taskIds.add(taskMap);
        final Pagination pagination = Pagination.create(0L, 0);
        final OperationContext context = OperationContext.empty();
        List<TaskEntity> taskEntityList = new ArrayList<>();
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId("12345");
        taskEntity.setName("张三");
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setId("1");
        sourceEntity.setName("任务");
        sourceEntity.setType(SourceType.THIRD_PARTY_PUSH);
        List<TaskType> types = new ArrayList<>();
        TaskType taskType = TaskType.custom()
                .name("任务")
                .id("12345")
                .children(Collections.emptyList())
                .sources(Collections.singletonList(sourceEntity))
                .build();
        types.add(taskType);
        taskEntity.setTypes(types);
        List<TaskProperty> properties = new ArrayList<>();
        TaskProperty taskProperty = TaskProperty.custom()
                .sequence(2)
                .dataType(PropertyDataType.LIST_TEXT)
                .name("title")
                .build();
        TaskProperty taskProperty1 = TaskProperty.custom()
                .sequence(4)
                .dataType(PropertyDataType.TEXT)
                .name("owner")
                .build();
        properties.add(taskProperty);
        properties.add(taskProperty1);
        taskEntity.setProperties(properties);
        taskEntityList.add(taskEntity);
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("name", "owner");
        row.put("info", "list_text_0");
        rows.add(row);
        Map<String, Object> row1 = new HashMap<>();
        row1.put("name", "created_date");
        row1.put("info", "datetime_1");
        rows.add(row1);
        List<OrderBy> orderBys = Collections.singletonList(OrderBy.of("info.created_date", "DESC"));
        return new MockResult(info, filter, pagination, context, taskIds, taskEntityList, rows, orderBys);
    }

    private static class MockResult {
        private final Map<String, List<String>> info;

        private final TaskInstance.Filter filter;

        private final Pagination pagination;

        private final OperationContext context;

        private final List<Map<String, Object>> taskIds;

        private final List<TaskEntity> taskEntityList;

        private final List<Map<String, Object>> row;

        private final List<OrderBy> orderBys;

        private MockResult(Map<String, List<String>> info, TaskInstance.Filter filter, Pagination pagination,
                OperationContext context, List<Map<String, Object>> taskIds, List<TaskEntity> taskEntityList,
                List<Map<String, Object>> row, List<OrderBy> orderBys) {
            this.info = info;
            this.filter = filter;
            this.pagination = pagination;
            this.context = context;
            this.taskIds = taskIds;
            this.taskEntityList = taskEntityList;
            this.row = row;
            this.orderBys = orderBys;
        }
    }
}
