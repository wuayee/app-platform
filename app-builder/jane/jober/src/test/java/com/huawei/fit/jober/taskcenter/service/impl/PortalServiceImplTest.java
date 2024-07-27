/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static com.huawei.fit.jober.Tests.matchArguments;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.PaginationResult;
import com.huawei.fit.jober.taskcenter.domain.CategoryEntity;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.service.NodeService;
import com.huawei.fit.jober.taskcenter.service.PortalService;
import com.huawei.fit.jober.taskcenter.service.SourceService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.service.TreeService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PortalServiceImplTest {
    private static final String COUNT_TASKS_BY_GROUP_SQL;

    static {
        ClassLoader loader = PortalServiceImplTest.class.getClassLoader();
        try {
            COUNT_TASKS_BY_GROUP_SQL = IoUtils.content(loader, "sql/count-tasks-by-group.sql");
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load embedded resources.", ex);
        }
    }

    private PortalService portalService;

    private TaskProperty.Repo taskPropertyRepo;

    @Mock
    private DynamicSqlExecutor executor;

    @Mock
    private TaskInstance.Repo taskInstanceRepo;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TaskTemplate.Repo taskTemplateRepo;

    @Mock
    private Index.Repo indexRepo;

    private OperationContext context;

    @BeforeEach
    void setup() {
        this.taskPropertyRepo = mock(TaskProperty.Repo.class);
        this.portalService = new PortalServiceImpl(
                mock(TreeService.class),
                mock(NodeService.class),
                mock(TaskService.class),
                mock(TaskType.Repo.class),
                mock(SourceService.class),
                executor,
                this.taskPropertyRepo,
                this.taskInstanceRepo,
                this.categoryService,
                this.taskTemplateRepo, this.indexRepo);
        this.context = mock(OperationContext.class);
    }

    @Test
    @DisplayName("批量保存属性时逐个保存")
    void should_save_properties_when_batch() {
        String taskId = "94e89246b5004926b34a173608f0c226";
        String propertyId1 = "02699c289f2b4256b03746ce798208a7";
        TaskProperty.Declaration declaration1 = mock(TaskProperty.Declaration.class);
        String propertyId2 = "70f5edb8d41c40f9a7014aa7c57289e2";
        TaskProperty.Declaration declaration2 = mock(TaskProperty.Declaration.class);
        Map<String, TaskProperty.Declaration> declarations = MapBuilder.<String, TaskProperty.Declaration>get()
                .put(propertyId1, declaration1)
                .put(propertyId2, declaration2)
                .build();
        OperationContext context = mock(OperationContext.class);
        this.portalService.patchProperties(taskId, declarations, context);
        verify(this.taskPropertyRepo, times(1)).patch(taskId, propertyId1, declaration1, context);
        verify(this.taskPropertyRepo, times(1)).patch(taskId, propertyId2, declaration2, context);
    }

    @Test
    @DisplayName("测试异步Count调用正常")
    void invokeCountRunAsyncSuccessfully() {
        TaskTemplate template = mock(TaskTemplate.class);
        when(template.id()).thenReturn("6bc6a655a3134284bf9ba44f9947fc9d");
        when(template.name()).thenReturn("普通任务");
        TaskTemplateProperty ownerProperty = templateProperty("text_1");
        TaskTemplateProperty creatorProperty = templateProperty("text_2");
        when(template.property(eq("owner"))).thenReturn(ownerProperty);
        when(template.property(eq("created_by"))).thenReturn(creatorProperty);
        RangedResultSet<TaskTemplate> results = results(template);
        when(taskTemplateRepo.list(any(), anyLong(), anyInt(), any())).thenReturn(results);
        List<String> taskIds = new ArrayList<>();
        List<Map<String, Object>> groupRows = Arrays.asList(groupRow("c6d87018aafb4d9a8f659f1584992ea7", "t1", 10),
                groupRow("845400833479490da4e06c6d7c5d0a71", "t2", 20));
        when(this.executor.executeQuery(any(), any())).thenReturn(groupRows);
        List<PortalService.TagCountEntity> list = this.portalService
                .count(Arrays.asList("owner1", "owner2"), null, Collections.emptyList(), taskIds, null);
        Assertions.assertEquals(list.size(), 3);
    }

    @Test
    @DisplayName("测试owner类型LIST_TEXT查询count正常")
    void invokeCountRunAsyncListTextSuccessfully() {
        TaskTemplate template = mock(TaskTemplate.class);
        when(template.id()).thenReturn("6bc6a655a3134284bf9ba44f9947fc9d");
        when(template.name()).thenReturn("普通任务");
        TaskTemplateProperty ownerProperty = templateProperty("list_text_1");
        TaskTemplateProperty creatorProperty = templateProperty("text_2");
        when(template.property(eq("owner"))).thenReturn(ownerProperty);
        when(template.property(eq("created_by"))).thenReturn(creatorProperty);
        RangedResultSet<TaskTemplate> results = results(template);
        when(taskTemplateRepo.list(any(), anyLong(), anyInt(), any())).thenReturn(results);
        List<String> taskIds = new ArrayList<>();
        List<Map<String, Object>> groupRows = Arrays.asList(groupRow("c6d87018aafb4d9a8f659f1584992ea7", "t1", 10),
                groupRow("845400833479490da4e06c6d7c5d0a71", "t2", 20));
        when(this.executor.executeQuery(any(), any())).thenReturn(groupRows);
        List<PortalService.TagCountEntity> list1 = this.portalService
                .count(Arrays.asList("owner1", "owner2"), null, Collections.emptyList(), taskIds, null);
        List<PortalService.TagCountEntity> list2 = this.portalService
                .count(Collections.singletonList("owner1"), null, Collections.emptyList(), taskIds, null);
        Assertions.assertEquals(list1.size(), 3);
        Assertions.assertEquals(list2.size(), 3);
    }

    @Test
    void should_return_groups_associated_with_owner() {
        TaskTemplate template = this.mockTaskTemplate();
        List<CategoryEntity> categories = Arrays.asList(
                category("00713f5c49bb4e22ba5d74bebb21cd5f", "未开始", "status"),
                category("ce36b3d5778f4999802f07e488c9fcc1", "处理中", "status"));
        when(this.categoryService.listByNames(any())).thenReturn(categories);
        RangedResultSet<TaskTemplate> templates = results(template);
        when(this.taskTemplateRepo.list(any(), anyLong(), anyInt(), any())).thenReturn(templates);
        List<Map<String, Object>> groupRows = Arrays.asList(
                groupRow("c6d87018aafb4d9a8f659f1584992ea7", "t1", 10),
                groupRow("845400833479490da4e06c6d7c5d0a71", "t2", 20));
        when(this.executor.executeQuery(eq(COUNT_TASKS_BY_GROUP_SQL), argThat(matchArguments(
                "6bc6a655a3134284bf9ba44f9947fc9d", "00713f5c49bb4e22ba5d74bebb21cd5f",
                "ce36b3d5778f4999802f07e488c9fcc1", "%owner1%", "%owner2%", "%creator%", "vip"))))
                .thenReturn(groupRows);
        List<PortalService.TaskGroup> groups = this.portalService.listTaskGroups(Arrays.asList("owner1", "owner2"),
                Collections.singletonList("creator"), Collections.singletonList("vip"),
                Collections.singletonList("处理中"), Collections.emptyList(), this.context);
        assertEquals(2, groups.size());
    }

    @Test
    void should_throw_when_nether_owner_nor_creator_supplied() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> this.portalService
                .listTaskGroups(null, null, Collections.emptyList(), Collections.singletonList("处理中"),
                        Collections.emptyList(), this.context));
        assertEquals("The owner and creator of task cannot be both empty.", ex.getMessage());
    }

    @Test
    void should_return_number_of_task_instances() {
        TaskTemplate template = this.mockTaskTemplate();
        RangedResultSet<TaskTemplate> templates = results(template);
        when(this.taskTemplateRepo.list(any(), anyLong(), anyInt(), any())).thenReturn(templates);
        List<Map<String, Object>> groupRows = Arrays.asList(
                groupRow("c6d87018aafb4d9a8f659f1584992ea7", "t1", 10),
                groupRow("845400833479490da4e06c6d7c5d0a71", "t2", 20));
        when(this.executor.executeQuery(any(), any())).thenReturn(groupRows);
        when(this.taskInstanceRepo.list(any(),
                any(),
                any(),
                any(),
                any(),
                any())).thenReturn(PagedResultSet.create(new ArrayList<>(), PaginationResult.create(0L, 1, 0)));
        List<PortalService.TagCountEntity> entities = this.portalService.count(Arrays.asList("owner1", "owner2"),
                Collections.singletonList("creator"), Collections.singletonList("vip"), Collections.emptyList(),
                this.context);
        assertEquals(3, entities.size());
        for (PortalService.TagCountEntity entity : entities) {
            assertEquals(30L, entity.getValue());
        }
    }

    private TaskTemplate mockTaskTemplate() {
        TaskTemplate template = mock(TaskTemplate.class);
        when(template.id()).thenReturn("6bc6a655a3134284bf9ba44f9947fc9d");
        when(template.name()).thenReturn("普通任务");
        TaskTemplateProperty ownerProperty = templateProperty("text_1");
        TaskTemplateProperty creatorProperty = templateProperty("text_2");
        when(template.property(eq("owner"))).thenReturn(ownerProperty);
        when(template.property(eq("created_by"))).thenReturn(creatorProperty);
        return template;
    }

    private static RangedResultSet<TaskTemplate> results(TaskTemplate template) {
        RangedResultSet<TaskTemplate> results = cast(mock(RangedResultSet.class));
        when(results.getResults()).thenReturn(Collections.singletonList(template));
        return results;
    }

    private static TaskTemplateProperty templateProperty(String column) {
        TaskTemplateProperty property = mock(TaskTemplateProperty.class);
        when(property.column()).thenReturn(column);
        return property;
    }

    private static Map<String, Object> groupRow(String taskId, String taskName, int taskCount) {
        return MapBuilder.<String, Object>get()
                .put("task_id", taskId)
                .put("task_name", taskName)
                .put("task_count", taskCount)
                .build();
    }

    private static CategoryEntity category(String id, String name, String group) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setGroup(group);
        return entity;
    }
}
