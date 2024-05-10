/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.domain.Tenant;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.enums.JaneCategory;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.dao.TaskMapper;
import com.huawei.fit.jober.taskcenter.dao.po.TaskObject;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.filter.TaskFilter;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.service.PropertyService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.MapSerializer;
import com.huawei.fit.jober.taskcenter.validation.RelationshipValidator;
import com.huawei.fit.jober.taskcenter.validation.TaskValidator;
import com.huawei.fit.jober.taskcenter.validation.impl.TaskValidatorImpl;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * {@link TaskServiceImpl}对应测试类
 *
 * @author 梁致强 l50033199
 * @since 2023-08-09
 */
@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest extends DatabaseBaseTest {
    private final TaskValidator taskValidator = new TaskValidatorImpl(1, 64);

    private final TaskMapper taskMapper = sqlSessionManager.openSession(true).getMapper(TaskMapper.class);

    private TaskServiceImpl taskService;

    @Mock
    private DynamicSqlExecutor dynamicSqlExecutor;

    @Mock
    private PropertyService propertyService;

    @Mock
    private TaskProperty.Repo taskPropertyRepo;

    @Mock
    private TaskType.Repo taskTypeRepo;

    @Mock
    private CategoryService categoryService;

    @Mock
    private RelationshipValidator relationshipValidator;

    @Mock
    private TaskTemplate.Repo taskTemplateRepo;

    private MapSerializer serializer;

    private Tenant.Repo tenantRepo;

    @Mock
    private Index.Repo indexRepo;

    @BeforeEach
    void setUp() {
        serializer = mock(MapSerializer.class);
        tenantRepo = mock(Tenant.Repo.class);
        taskService = new TaskServiceImpl(propertyService, taskPropertyRepo, taskTypeRepo, taskMapper,
                dynamicSqlExecutor, taskValidator, relationshipValidator, this.categoryService, serializer, tenantRepo,
                taskTemplateRepo, this.indexRepo);
    }

    private void mockTenantRepo() {
        when(tenantRepo.retrieve(any(), any())).thenAnswer(args -> {
            Tenant tenant = mock(Tenant.class);
            when(tenant.id()).thenReturn(args.getArgument(0));
            return tenant;
        });
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/task/cleanData.sql");
    }

    /**
     * 将taskObject转换为TaskEntity
     *
     * @param taskObject 表示存储数据库一行数据的具体实例
     * @return 表示将数据实例转换为业务逻辑层存储数据的实例
     */
    private TaskEntity toTaskEntity(TaskObject taskObject) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskObject.getId());
        taskEntity.setName(taskObject.getName());
        taskEntity.setAttributes(Collections.emptyMap());
        taskEntity.setCreator(taskObject.getCreatedBy());
        taskEntity.setCreationTime(taskObject.getCreatedAt());
        taskEntity.setLastModifier(taskObject.getUpdatedBy());
        taskEntity.setLastModificationTime(taskObject.getUpdatedAt());
        return taskEntity;
    }

    @Nested
    @DisplayName("测试创建Task功能")
    class TestCreate {
        private OperationContext context;

        @BeforeEach
        void setup() {
            mockTenantRepo();
            context = OperationContext.custom()
                    .operator("a3a7df7e45f44451970a6a8e138382c2")
                    .tenantId("a3a7df7e45f44451970a6a8e138382c2")
                    .build();
        }

        @Test
        @DisplayName("参数正确，创建成功")
        void create() {
            // when
            TaskDeclaration taskDeclaration = new TaskDeclaration();
            taskDeclaration.setName(UndefinableValue.defined("name"));
            taskDeclaration.setProperties(UndefinableValue.undefined());
            when(serializer.serialize(Collections.emptyMap())).thenReturn("{}");
            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(0L);
            when(taskTemplateRepo.defaultTemplateId()).thenReturn("a3a7df7e45f44451970a6a8e138382c2");
            LocalDateTime now = LocalDateTime.now();
            TaskObject taskObject = new TaskObject();
            taskObject.setId("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setName("name");
            taskObject.setTenantId("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setCreatedBy("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setCreatedAt(now);
            taskObject.setUpdatedBy("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setUpdatedAt(now);

            final TaskEntity taskEntity = toTaskEntity(taskObject);
            taskEntity.setProperties(new ArrayList<>());
            // then
            final TaskEntity res = taskService.create(taskDeclaration, context);
            assertEquals(taskEntity.getName(), res.getName());
            assertEquals(taskEntity.getCreator(), res.getCreator());
            assertEquals(taskEntity.getLastModifier(), res.getLastModifier());
            assertEquals(taskEntity.getProperties(), res.getProperties());
        }

        @Test
        @DisplayName("参数正确，创建成功，包括properties")
        void create2() {
            // when
            TaskDeclaration taskDeclaration = new TaskDeclaration();
            taskDeclaration.setName(UndefinableValue.defined("name"));
            taskDeclaration.setTemplateId(UndefinableValue.defined("a3a7df7e45f44451970a6a8e138382c2"));
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom()
                    .name("pro")
                    .dataType("TEXT")
                    .templateId("a3a7df7e45f44451970a6a8e138382c2")
                    .build();
            taskDeclaration.setProperties(UndefinableValue.defined(Collections.singletonList(declaration)));
            when(serializer.serialize(Collections.emptyMap())).thenReturn("{}");
            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(0L);
            when(taskTemplateRepo.exist(eq("a3a7df7e45f44451970a6a8e138382c2"))).thenReturn(true);
            LocalDateTime now = LocalDateTime.now();
            TaskObject taskObject = new TaskObject();
            taskObject.setId("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setName("name");
            taskObject.setTenantId("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setCreatedBy("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setCreatedAt(now);
            taskObject.setUpdatedBy("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setUpdatedAt(now);

            final TaskEntity taskEntity = toTaskEntity(taskObject);
            taskEntity.setProperties(new ArrayList<>());
            // then
            final TaskEntity res = taskService.create(taskDeclaration, context);
            assertEquals(taskEntity.getName(), res.getName());
            assertEquals(taskEntity.getCreator(), res.getCreator());
            assertEquals(taskEntity.getLastModifier(), res.getLastModifier());
            assertEquals(taskEntity.getProperties(), res.getProperties());
        }

        @Test
        @DisplayName("参数正确，创建成功：使用默认模板")
        void create3() {
            // when
            TaskDeclaration taskDeclaration = new TaskDeclaration();
            taskDeclaration.setName(UndefinableValue.defined("name"));
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom()
                    .name("id")
                    .dataType("TEXT")
                    .scope("SYSTEM")
                    .isRequired(true)
                    .appearance(new HashMap<>())
                    .categories(new ArrayList<>())
                    .isIdentifiable(true)
                    .description("hello")
                    .build();
            taskDeclaration.setProperties(UndefinableValue.defined(Collections.singletonList(declaration)));
            when(serializer.serialize(Collections.emptyMap())).thenReturn("{}");
            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(0L);
            when(taskTemplateRepo.defaultTemplateId()).thenReturn("a3a7df7e45f44451970a6a8e138382c2");
            when(taskTemplateRepo.retrieve(anyString(), any(OperationContext.class))).thenReturn(mockTaskTemplate());
            LocalDateTime now = LocalDateTime.now();
            TaskObject taskObject = new TaskObject();
            taskObject.setId("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setName("name");
            taskObject.setTenantId("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setCreatedBy("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setCreatedAt(now);
            taskObject.setUpdatedBy("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setUpdatedAt(now);

            final TaskEntity taskEntity = toTaskEntity(taskObject);
            taskEntity.setProperties(new ArrayList<>());
            // then
            final TaskEntity res = taskService.create(taskDeclaration, context);
            assertEquals(taskEntity.getName(), res.getName());
            assertEquals(taskEntity.getCreator(), res.getCreator());
            assertEquals(taskEntity.getLastModifier(), res.getLastModifier());
            assertEquals(taskEntity.getProperties(), res.getProperties());
        }

        @Test
        void should_throw_when_task_exist_in_current_tenant() {
            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1L);
            TaskDeclaration declaration = new TaskDeclaration();
            declaration.setName(UndefinableValue.defined("hello"));
            declaration.setProperties(UndefinableValue.undefined());
            declaration.setCategoryTriggers(UndefinableValue.undefined());
            declaration.setAttributes(UndefinableValue.undefined());
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> taskService.create(declaration, context));
            assertEquals(ErrorCodes.TASK_EXIST_IN_CURRENT_TENANT.getErrorCode(), ex.getCode());
        }

        private TaskTemplate mockTaskTemplate() {
            TaskTemplateProperty templateProperty = TaskTemplateProperty.custom()
                    .taskTemplateId("08f8fc32484d408aacbcff35bf8be687")
                    .id("7aad199a001a49149c1a1c8b24ad0908")
                    .name("id")
                    .dataType(PropertyDataType.TEXT)
                    .sequence(1)
                    .build();
            return TaskTemplate.custom()
                    .id("08f8fc32484d408aacbcff35bf8be687")
                    .name("普通任务")
                    .properties(Collections.singletonList(templateProperty))
                    .description("默认模板")
                    .build();
        }
    }

    @Nested
    @DisplayName("测试更新Task功能")
    class TestPatch {
        private static final String TASK_ID = "a3a7df7e45f44451970a6a8e138382c2";

        private TaskDeclaration declaration;

        private OperationContext context;

        @BeforeEach
        void setup() {
            mockTenantRepo();
            declaration = new TaskDeclaration();
            declaration.setName(UndefinableValue.defined("name"));
            declaration.setAttributes(UndefinableValue.defined(Collections.emptyMap()));
            declaration.setProperties(UndefinableValue.undefined());
            declaration.setCategoryTriggers(UndefinableValue.undefined());
            declaration.setTemplateId(UndefinableValue.defined("a3a7df7e45f44451970a6a8e138382c2"));
            context = OperationContext.custom()
                    .operator("a3a7df7e45f44451970a6a8e138382c2")
                    .tenantId("a3a7df7e45f44451970a6a8e138382c2")
                    .build();
        }

        @Test
        @DisplayName("更新已有Task，更新成功")
        void givenExistentTaskIdThenPatchSuccessfully() {
            executeSqlInFile("handler/task/saveData.sql");
            this.mockTaskTemplateRepo();
            Assertions.assertDoesNotThrow(() -> taskService.patch(TASK_ID, declaration, context));
        }

        @Test
        void should_throw_when_name_exist_in_current_tenant() {
            Map<String, Object> row = MapBuilder.<String, Object>get()
                    .put("id", "8a9b5fab440b43549f8f65a0a6ba18b4")
                    .put("tenant_id", "a3a7df7e45f44451970a6a8e138382c2")
                    .build();
            when(dynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.singletonList(row));
            this.mockTaskTemplateRepo();
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> taskService.patch(TASK_ID, declaration, context));
            assertEquals(ErrorCodes.TASK_EXIST_IN_CURRENT_TENANT.getErrorCode(), ex.getCode());
        }

        @Test
        void should_throw_when_template_id_has_been_changed() {
            Map<String, Object> row = MapBuilder.<String, Object>get()
                    .put("id", "8a9b5fab440b43549f8f65a0a6ba18b4")
                    .put("tenant_id", "a3a7df7e45f44451970a6a8e138382c2")
                    .build();
            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn("a3a7df7e45f44451970a6a8e138382c3");
            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> taskService.patch(TASK_ID, declaration, context));
            assertEquals(ErrorCodes.CHANGE_TASK_TEMPLATE_IS_INVALID.getErrorCode(), ex.getCode());
        }

        private void mockTaskTemplateRepo() {
            TaskTemplate taskTemplate = Mockito.mock(TaskTemplate.class);
            when(dynamicSqlExecutor.executeScalar(startsWith("SELECT template_id FROM"), any())).thenReturn(
                    "a3a7df7e45f44451970a6a8e138382c2");
            when(taskTemplateRepo.retrieve(eq("a3a7df7e45f44451970a6a8e138382c2"), any())).thenReturn(taskTemplate);
        }
    }

    @Nested
    @DisplayName("测试删除Task功能")
    class TestDelete {
        @Test
        @DisplayName("删除已有Task，删除成功")
        void givenExistentTaskIdThenDeleteSuccessfully() {
            // given
            executeSqlInFile("handler/task/saveData.sql");
            TaskObject actual = taskMapper.selectById("a3a7df7e45f44451970a6a8e138382c2");
            assertNotNull(actual);
            final OperationContext operationContext = OperationContext.empty();
            // when
            taskService.delete("a3a7df7e45f44451970a6a8e138382c2", operationContext);
            // then
            actual = taskMapper.selectById("a3a7df7e45f44451970a6a8e138382c2");
            Assertions.assertNull(actual);
        }
    }

    @Nested
    @DisplayName("测试检索Task功能")
    class TestRetrieve {
        @Test
        @DisplayName("查询已有Task，查询成功")
        void givenExistentTaskIdThenRetrieveSuccessfully() {
            // given
            executeSqlInFile("handler/task/saveData.sql");
            LocalDateTime now = LocalDateTime.parse("2023-08-11T00:00:00");

            when(serializer.deserialize(any())).thenReturn(Collections.emptyMap());

            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId("a3a7df7e45f44451970a6a8e138382c2");
            taskEntity.setName("a3a7df7e45f44451970a6a8e138382c2");
            taskEntity.setTemplateId("00000000000000000000000000000000");
            taskEntity.setCategory(JaneCategory.TASK);
            taskEntity.setAttributes(Collections.emptyMap());
            taskEntity.setCreator("a3a7df7e45f44451970a6a8e138382c2");
            taskEntity.setCreationTime(now);
            taskEntity.setLastModifier("a3a7df7e45f44451970a6a8e138382c2");
            taskEntity.setLastModificationTime(now);
            taskEntity.setTypes(Collections.emptyList());
            taskEntity.setProperties(Collections.emptyList());
            taskEntity.setCategoryTriggers(Collections.emptyList());
            taskEntity.setTenantId("a3a7df7e45f44451970a6a8e138382c2");
            final OperationContext operationContext = OperationContext.empty();
            // when
            TaskEntity actual = taskService.retrieve("a3a7df7e45f44451970a6a8e138382c2", operationContext);
            // then
            assertNotNull(actual);
            assertEquals(taskEntity, actual);
            verify(taskPropertyRepo).list("a3a7df7e45f44451970a6a8e138382c2", operationContext);
            verify(taskTypeRepo).list(Collections.singletonList("a3a7df7e45f44451970a6a8e138382c2"), operationContext);
        }

        @Test
        @DisplayName("查询已有Task集合，查询成功")
        void givenExistentTaskIdThenListSuccessfully() {
            // given
            executeSqlInFile("handler/task/saveData.sql");
            LocalDateTime now = LocalDateTime.parse("2023-08-11T00:00:00");

            when(serializer.deserialize(any())).thenReturn(Collections.emptyMap());

            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId("a3a7df7e45f44451970a6a8e138382c2");
            taskEntity.setName("a3a7df7e45f44451970a6a8e138382c2");
            taskEntity.setTemplateId("00000000000000000000000000000000");
            taskEntity.setAttributes(Collections.emptyMap());
            taskEntity.setCreator("a3a7df7e45f44451970a6a8e138382c2");
            taskEntity.setCategory(JaneCategory.TASK);
            taskEntity.setTenantId("a3a7df7e45f44451970a6a8e138382c2");
            final OperationContext operationContext = OperationContext.empty();
            taskEntity.setCreationTime(now);
            taskEntity.setLastModificationTime(now);
            taskEntity.setTypes(Collections.emptyList());
            taskEntity.setLastModifier("a3a7df7e45f44451970a6a8e138382c2");
            taskEntity.setProperties(Collections.emptyList());
            taskEntity.setCategoryTriggers(Collections.emptyList());
            // when
            List<TaskEntity> actual = taskService.listTaskEntities(
                    Collections.singletonList("a3a7df7e45f44451970a6a8e138382c2"), operationContext);
            // then
            assertNotNull(actual);
            assertEquals(Collections.singletonList(taskEntity), actual);
            verify(taskPropertyRepo).list("a3a7df7e45f44451970a6a8e138382c2", operationContext);
            verify(taskTypeRepo).list(Collections.singletonList("a3a7df7e45f44451970a6a8e138382c2"), operationContext);
        }

        @Test
        @DisplayName("查询没有Task，返回Null")
        void givenNonExistentTaskIdThenRetrieveNullResult() {
            // when
            final OperationContext operationContext = OperationContext.empty();
            NotFoundException actual = Assertions.assertThrows(NotFoundException.class,
                    () -> taskService.retrieve("a3a7df7e45f44451970a6a8e138382c7", operationContext));
            // then
            assertEquals(ErrorCodes.TASK_NOT_FOUND.getErrorCode(), actual.getCode());
        }
    }

    @Nested
    @DisplayName("测试分页检索Tasks功能")
    class TestList {
        @Test
        @DisplayName("查询已有Tasks，查询成功")
        void givenExistentTaskIdsThenRetrieveSuccessfully() {
            final String id = "a3a7df7e45f44451970a6a8e138382c2";
            final String name = "my-task";
            final String category = "TASK";
            final String tenantId = "282b963640544f148dbdfa49718075bc";
            final String creator = "Zhang San";
            final LocalDateTime creationTime = LocalDateTime.of(2023, 1, 10, 10, 12, 23);
            final String lastModifier = "Li Si";
            final LocalDateTime lastModificationTime = LocalDateTime.of(2023, 3, 13, 18, 22, 51);

            List<Map<String, Object>> rows = new LinkedList<>();
            rows.add(MapBuilder.<String, Object>get()
                    .put("id", id)
                    .put("name", name)
                    .put("category", category)
                    .put("tenant_id", tenantId)
                    .put("attributes", "{}")
                    .put("created_by", creator)
                    .put("created_at", Timestamp.valueOf(creationTime))
                    .put("updated_by", lastModifier)
                    .put("updated_at", Timestamp.valueOf(lastModificationTime))
                    .build());

            final OperationContext operationContext = OperationContext.empty();

            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(2);
            when(dynamicSqlExecutor.executeQuery(any(), any())).thenReturn(rows);
            mockTenantRepo();

            // when
            TaskFilter taskFilter = new TaskFilter();
            taskFilter.setIds(UndefinableValue.defined(
                    Arrays.asList("a3a7df7e45f44451970a6a8e138382c2", "a3a7df7e45f44451970a6a8e138382c3")));
            taskFilter.setNames(UndefinableValue.undefined());
            RangedResultSet<TaskEntity> actual = taskService.list(taskFilter, 0, 2, operationContext);
            // then
            assertNotNull(actual);
            assertEquals(2, actual.getRange().getTotal());
            assertEquals(1, actual.getResults().size());
            TaskEntity task = actual.getResults().get(0);
            assertEquals(id, task.getId());
            assertEquals(name, task.getName());
            assertEquals(creator, task.getCreator());
            assertEquals(creationTime, task.getCreationTime());
            assertEquals(lastModifier, task.getLastModifier());
            assertEquals(lastModificationTime, task.getLastModificationTime());

            verify(taskPropertyRepo).list(Collections.singletonList(id), operationContext);
            verify(taskTypeRepo).list(Collections.singletonList(id), operationContext);
        }

        @Test
        @DisplayName("查询没有Tasks，返回Null")
        void givenNonExistentTaskIdsThenRetrieveNullResult() {
            // when
            TaskFilter taskFilter = new TaskFilter();
            taskFilter.setIds(UndefinableValue.defined(Arrays.asList("uuid1", "uuid2")));
            taskFilter.setNames(UndefinableValue.defined(Arrays.asList("name1", "name2")));
            final OperationContext operationContext = OperationContext.empty();
            when(tenantRepo.retrieve(any(), any())).thenThrow(new NotFoundException(ErrorCodes.TENANT_NOT_FOUND));

            RangedResultSet<TaskEntity> actual = taskService.list(taskFilter, 0, 2, operationContext);
            RangedResultSet<TaskEntity> target = Entities.emptyRangedResultSet(0, 2);
            // then
            assertEquals(target.getResults(), actual.getResults());
        }
    }

    @Nested
    @DisplayName("测试分页检索Tasks功能--特殊提供给未完成租户切换的下游应用")
            // FIXME: 同com.huawei.fit.jober.taskcenter.service.TaskService.listForApplication一起移除
    class TestListForApplication {
        @Test
        @DisplayName("查询已有Tasks，查询成功")
        void givenExistentTaskIdsThenRetrieveSuccessfully() {
            final String id = "a3a7df7e45f44451970a6a8e138382c2";
            final String name = "my-task";
            final String templateId = "12345678123456781234567812345678";
            final String category = "TASK";
            final String tenantId = "282b963640544f148dbdfa49718075bc";
            final String creator = "Zhang San";
            final LocalDateTime creationTime = LocalDateTime.of(2023, 1, 10, 10, 12, 23);
            final String lastModifier = "Li Si";
            final LocalDateTime lastModificationTime = LocalDateTime.of(2023, 3, 13, 18, 22, 51);

            List<Map<String, Object>> rows = new LinkedList<>();
            rows.add(MapBuilder.<String, Object>get()
                    .put("id", id)
                    .put("name", name)
                    .put("templateId", templateId)
                    .put("category", category)
                    .put("tenant_id", tenantId)
                    .put("attributes", "{}")
                    .put("created_by", creator)
                    .put("created_at", Timestamp.valueOf(creationTime))
                    .put("updated_by", lastModifier)
                    .put("updated_at", Timestamp.valueOf(lastModificationTime))
                    .build());

            final OperationContext operationContext = OperationContext.empty();

            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(2);
            when(dynamicSqlExecutor.executeQuery(any(), any())).thenReturn(rows);
            mockTenantRepo();

            // when
            TaskFilter taskFilter = new TaskFilter();
            taskFilter.setIds(UndefinableValue.defined(
                    Arrays.asList("a3a7df7e45f44451970a6a8e138382c2", "a3a7df7e45f44451970a6a8e138382c3")));
            taskFilter.setNames(UndefinableValue.undefined());
            taskFilter.setTemplateIds(UndefinableValue.defined(Collections.singletonList(templateId)));
            taskFilter.setCategories(UndefinableValue.defined(Collections.singletonList("TASK")));
            taskFilter.setCreators(UndefinableValue.defined(Collections.singletonList("Zhang San")));
            RangedResultSet<TaskEntity> actual = taskService.listForApplication(taskFilter, 0, 2, operationContext);
            // then
            assertNotNull(actual);
            assertEquals(2, actual.getRange().getTotal());
            assertEquals(1, actual.getResults().size());
            TaskEntity task = actual.getResults().get(0);
            assertEquals(id, task.getId());
            assertEquals(name, task.getName());
            assertEquals(creator, task.getCreator());
            assertEquals(creationTime, task.getCreationTime());
            assertEquals(lastModifier, task.getLastModifier());
            assertEquals(lastModificationTime, task.getLastModificationTime());

            verify(taskPropertyRepo).list(Collections.singletonList(id), operationContext);
            verify(taskTypeRepo).list(Collections.singletonList(id), operationContext);
        }

        @Test
        @DisplayName("查询没有Tasks，返回Null")
        void givenNonExistentTaskIdsThenRetrieveNullResult() {
            // when
            TaskFilter taskFilter = new TaskFilter();
            taskFilter.setIds(UndefinableValue.defined(Arrays.asList("uuid1", "uuid2")));
            taskFilter.setNames(UndefinableValue.defined(Arrays.asList("name1", "name2")));
            final OperationContext operationContext = OperationContext.empty();
            when(tenantRepo.retrieve(any(), any())).thenThrow(new NotFoundException(ErrorCodes.TENANT_NOT_FOUND));

            RangedResultSet<TaskEntity> actual = taskService.list(taskFilter, 0, 2, operationContext);
            RangedResultSet<TaskEntity> target = Entities.emptyRangedResultSet(0, 2);
            // then
            assertEquals(target.getResults(), actual.getResults());
        }
    }

    @Nested
    @DisplayName("测试分页检索多版本Task功能")
    class TestListMeta {
        @BeforeEach
        public void before() {
            Tenant tenant = Tenant.custom().id("87345786d6d344e4474078ff9634ae7f").build();
            when(tenantRepo.retrieve(anyString(), any())).thenReturn(tenant);
        }

        @Test
        @DisplayName("使用lastOnly=true，测试成功")
        public void lastOnlySuccess() {
            MetaFilter filter = createFilter();
            when(dynamicSqlExecutor.executeScalar(anyString(), anyList())).thenReturn(1);
            when(dynamicSqlExecutor.executeQuery(anyString(), anyList())).thenReturn(mockRows());
            when(taskPropertyRepo.list(anyList(), any())).thenReturn(mockProperties());
            RangedResultSet<TaskEntity> result = taskService.listMeta(filter, true, 0, 10, generateContext());
            Assertions.assertEquals(1, result.getRange().getTotal());
            TaskEntity taskEntity = result.getResults().get(0);
            Assertions.assertEquals(JaneCategory.META, taskEntity.getCategory());
            Assertions.assertEquals("fruit|1.0.0", taskEntity.getName());
        }

        private List<Map<String, Object>> mockRows() {
            Map<String, Object> result = new HashMap<>();
            result.put("id", "9fd4c786d6d344e99a4078ff9634ae7d");
            result.put("name", "fruit|1.0.0");
            result.put("template_id", "87345786d6d344e99a4078ff9634ae7f");
            result.put("category", "META");
            result.put("attributes", "{\"publish\":\"true\"}");
            result.put("created_by", "ywx1299574");
            long epochMilli = LocalDateTime.of(2024, 2, 28, 12, 12, 35)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            result.put("created_at", new Timestamp(epochMilli));
            result.put("updated_by", "ywx1299574");
            result.put("updated_at", new Timestamp(epochMilli));
            return new ArrayList<Map<String, Object>>() {{add(result);}};
        }

        private Map<String, List<TaskProperty>> mockProperties() {
            PropertyCategory category = new PropertyCategory("1", "JANE");
            TaskProperty property = TaskProperty.custom().categories(new ArrayList<PropertyCategory>() {{
                add(category);
            }}).name("hello").id("7601cb20e097430dbcb82da7acaf9eec").build();

            return new HashMap<String, List<TaskProperty>>() {{
                put("9fd4c786d6d344e99a4078ff9634ae7d", new ArrayList<TaskProperty>() {{add(property);}});
            }};
        }

        private OperationContext generateContext() {
            return OperationContext.custom().operator("yaojiang ywx1299574").tenantId("public").build();
        }

        private MetaFilter createFilter() {
            MetaFilter filter = new MetaFilter();
            filter.setCategories(new ArrayList<String>() {{
                add("META");
            }});
            filter.setVersions(new ArrayList<String>() {{
                add("1.0.0");
            }});
            filter.setCreators(new ArrayList<String>() {{
                add("姚江 WX1299574");
            }});
            filter.setNames(new ArrayList<String>() {{
                add("fruit");
            }});
            filter.setAttributes(new HashMap<String, List<String>>() {{
                put("publish", new ArrayList<String>() {{
                    add("true");
                }});
            }});

            filter.setMetaIds(new ArrayList<String>() {{
                add("4c9989421364404eb80ddf71271e9cd7");
            }});
            filter.setVersionIds(new ArrayList<String>() {{
                add("c40459cb27b246dda92e3c858bf10ab4");
            }});
            return filter;
        }
    }
}
