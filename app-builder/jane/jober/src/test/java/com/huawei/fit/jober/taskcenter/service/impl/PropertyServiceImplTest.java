/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.PropertyScope;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.taskcenter.declaration.TaskPropertiesDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.domain.postgresql.PostgresqlTaskPropertyRepo;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.service.PropertyService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.validation.PropertyValidator;
import com.huawei.fit.jober.taskcenter.validation.impl.PropertyValidatorImpl;
import com.huawei.fitframework.event.EventPublisher;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;

import com.alibaba.fastjson.TypeReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * {@link PropertyServiceImpl}对应测试类
 *
 * @author d30022216
 * @since 2023-08-11
 */
@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {
    @Mock
    private DynamicSqlExecutor mockDynamicSqlExecutor;

    @Mock
    private ObjectSerializer objectSerializer;

    @Mock
    private TaskTemplate.Repo taskTemplateRepo;

    private final PropertyValidator propertyValidator = new PropertyValidatorImpl(1, 64, 512, 16, 16);

    private TaskProperty.Repo taskPropertyRepo;

    private PropertyService propertyService;

    private Plugin plugin;

    @BeforeEach
    void setUp() {
        CategoryService categoryService = mock(CategoryService.class);
        this.plugin = mock(Plugin.class);
        this.taskPropertyRepo = new PostgresqlTaskPropertyRepo(this.mockDynamicSqlExecutor, this.propertyValidator,
                categoryService, this.objectSerializer, this.plugin);
        this.propertyService = new PropertyServiceImpl(this.mockDynamicSqlExecutor, this.objectSerializer,
                categoryService, this.propertyValidator, this.plugin);
    }

    @Test
    @DisplayName("测试添加单个任务属性")
    void testCreate() {
        // Setup
        final TaskProperty.Declaration declaration = TaskProperty.Declaration.custom()
                .name("name")
                .dataType(Enums.toString(PropertyDataType.TEXT))
                .description("")
                .isRequired(false)
                .scope(Enums.toString(PropertyScope.PUBLIC))
                .appearance(new HashMap<>())
                .build();
        final OperationContext context = OperationContext.empty();
        when(objectSerializer.serialize(new HashMap<>(), StandardCharsets.UTF_8)).thenReturn(
                "{}".getBytes(StandardCharsets.UTF_8));
        when(objectSerializer.deserialize("{}".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8,
                new TypeReference<Map<String, Object>>() {}.getType())).thenReturn(new HashMap<>());

        final TaskProperty result = this.taskPropertyRepo.create("a3a7df7e45f44451970a6a8e138382c2",
                declaration, context);

        assertEquals("name", result.name());
        assertFalse(result.required());
        assertFalse(result.identifiable());
        assertEquals("", result.description());
        assertEquals(PropertyScope.PUBLIC, result.scope());
        assertEquals(PropertyDataType.TEXT, result.dataType());
        assertEquals(Collections.emptyMap(), result.appearance());
        assertEquals(1, result.sequence());
        assertEquals(Collections.emptyList(), result.categories());
    }

    @Test
    @DisplayName("测试选择性修改任务属性")
    void testPatch() {
        // Setup
        final TaskProperty.Declaration.Builder declaration = TaskProperty.Declaration.custom();
        declaration.name("name");
        declaration.dataType(Enums.toString(PropertyDataType.TEXT));
        declaration.description("");
        declaration.isRequired(false);
        declaration.scope(Enums.toString(PropertyScope.PUBLIC));
        declaration.appearance(new HashMap<>());
        when(objectSerializer.serialize(new HashMap<>(), StandardCharsets.UTF_8)).thenReturn(
                "{}".getBytes(StandardCharsets.UTF_8));

        final OperationContext context = OperationContext.empty();

        Map<String, Object> values = MapBuilder.<String, Object>get()
                .put("id", "a3a7df7e45f44451970a6a8e138382c3")
                .put("data_type", "TEXT")
                .put("sequence", 1)
                .put("required", false)
                .put("identifiable", false)
                .put("scope", "PUBLIC")
                .put("template_id", "00000000000000000000000000000000")
                .put("appearance", "{}")
                .build();
        when(mockDynamicSqlExecutor.executeQuery(startsWith("SELECT ttp.id, ttp.data_type, ttp.sequence,"),
                any())).thenReturn(Collections.emptyList());
        when(mockDynamicSqlExecutor.executeQuery(startsWith("SELECT \"id\", \"task_id\", \"name\","),
                any())).thenReturn(Collections.singletonList(values));
        EventPublisher publisher = mock(EventPublisher.class);
        when(plugin.publisherOfEvents()).thenReturn(publisher);

        // Run the test
        this.taskPropertyRepo.patch("a3a7df7e45f44451970a6a8e138382c2", "a3a7df7e45f44451970a6a8e138382c3",
                declaration.build(), context);
        // Verify the results
        List<Object> expectedArgs = Arrays.asList("name", "TEXT", "", false,
                "PUBLIC", "{}", "a3a7df7e45f44451970a6a8e138382c3");
        verify(mockDynamicSqlExecutor, times(1)).executeUpdate(
                eq("UPDATE \"task_property\" SET \"name\" = ?, \"data_type\" = ?, \"description\" = ?, \"required\" = ?, \"scope\" = ?, \"appearance\" = ?::JSON WHERE \"id\" = ?"),
                argThat(args -> args.size() == expectedArgs.size() && IntStream.range(0, expectedArgs.size())
                        .allMatch(index -> Objects.equals(expectedArgs.get(index), expectedArgs.get(index)))));
    }

    @Test
    @DisplayName("测试必填项为false")
    void testAcceptIdentifiableIsFalse() {
        final OperationContext context = OperationContext.empty();
        Map<String, Object> values = mockValues();
        values.put("required", false);
        values.put("identifiable", false);
        when(mockDynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.singletonList(values));
        when(mockDynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1);
        ConflictException conflictException = assertThrows(ConflictException.class,
                () -> this.propertyService.batchSave(taskPropertiesDeclarationBuild(), context));
        assertEquals(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES.getErrorCode(), conflictException.getCode());
    }

    @Test
    @DisplayName("测试必填项为true")
    void testAcceptIdentifiableIsTrue() {
        final OperationContext context = OperationContext.empty();
        when(mockDynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.singletonList(mockValues()));
        when(mockDynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1);
        when(objectSerializer.serialize(new HashMap<>(), StandardCharsets.UTF_8)).thenReturn(
                "{}".getBytes(StandardCharsets.UTF_8));
        ConflictException conflictException = assertThrows(ConflictException.class,
                () -> this.propertyService.batchSave(taskPropertiesDeclarationBuild(), context));
        assertEquals(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES.getErrorCode(), conflictException.getCode());
    }

    @Nested
    @DisplayName("batchSave方法的测试")
    class BatchSaveTest {
        String taskId = "cc17782284c34586b24d0684f8a442fe";

        String propertyId = "650f491290f74c3896866bfe6354ee08";

        String taskTemplateId = "ed10a2e9447a4708b5ccea14de37149e";

        String templatePropertyId = "c7e4218056d943a29b761d45b39c05d3";

        @Test
        @DisplayName("新增task创建成功")
        void test01() {
            TaskPropertiesDeclaration taskPropertiesDeclaration = buildTaskPropertyDeclarations();
            when(mockDynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.emptyList());
            when(objectSerializer.serialize(new HashMap<>(), StandardCharsets.UTF_8)).thenReturn(
                    "{}".getBytes(StandardCharsets.UTF_8));
            propertyService.batchSave(taskPropertiesDeclaration, OperationContext.custom().build());
        }

        @Test
        @DisplayName("新增task创建成功，不使用模板")
        void test02() {
            TaskPropertiesDeclaration taskPropertiesDeclaration = buildTaskPropertyDeclarationsNullTemplate();
            when(mockDynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.emptyList());
            when(objectSerializer.serialize(new HashMap<>(), StandardCharsets.UTF_8)).thenReturn(
                    "{}".getBytes(StandardCharsets.UTF_8));
            propertyService.batchSave(taskPropertiesDeclaration, OperationContext.custom().build());
        }

        @Test
        @Disabled
        @DisplayName("更新task创建成功，不使用模板-->使用模板")
        void test03() {
            TaskPropertiesDeclaration taskPropertiesDeclaration = buildTaskPropertyDeclarations();
            when(mockDynamicSqlExecutor.executeQuery(any(), any())).thenReturn(mockPropertyRows());
            when(objectSerializer.serialize(new HashMap<>(), StandardCharsets.UTF_8)).thenReturn(
                    "{}".getBytes(StandardCharsets.UTF_8));
            propertyService.batchSave(taskPropertiesDeclaration, OperationContext.custom().build());
        }

        @Test
        @Disabled
        @DisplayName("更新task创建成功，不使用模板直接更新")
        void test04() {
            TaskPropertiesDeclaration taskPropertiesDeclaration = buildTaskPropertyDeclarationsNullTemplate();
            when(mockDynamicSqlExecutor.executeQuery(any(), any())).thenReturn(mockPropertyRows());
            when(objectSerializer.serialize(new HashMap<>(), StandardCharsets.UTF_8)).thenReturn(
                    "{}".getBytes(StandardCharsets.UTF_8));
            propertyService.batchSave(taskPropertiesDeclaration, OperationContext.custom().build());
        }

        @Test
        @DisplayName("更新task创建失败：使用模板-->更新新模板")
        void test05() {
            TaskPropertiesDeclaration taskPropertiesDeclaration = buildTaskPropertyDeclarations();
            when(mockDynamicSqlExecutor.executeQuery(any(), any())).thenReturn(mockPropertyRows2());

            Assertions.assertThrows(ConflictException.class,
                    () -> propertyService.batchSave(taskPropertiesDeclaration, OperationContext.custom().build()));
        }

        private TaskPropertiesDeclaration buildTaskPropertyDeclarations() {
            TaskPropertiesDeclaration declaration = new TaskPropertiesDeclaration();
            declaration.setTaskId(taskId);
            TaskTemplateProperty templateProperty = TaskTemplateProperty.custom()
                    .taskTemplateId(taskTemplateId)
                    .dataType(PropertyDataType.TEXT)
                    .name("title")
                    .id(templatePropertyId)
                    .sequence(1)
                    .build();
            TaskTemplateProperty templateProperty2 = TaskTemplateProperty.custom()
                    .taskTemplateId(taskTemplateId)
                    .dataType(PropertyDataType.TEXT)
                    .name("title2")
                    .id(propertyId)
                    .sequence(2)
                    .build();
            TaskTemplate template = TaskTemplate.custom()
                    .id(taskTemplateId)
                    .name("template")
                    .description("description")
                    .properties(Arrays.asList(templateProperty, templateProperty2)).build();
            declaration.setTemplate(template);
            TaskProperty.Declaration pd = TaskProperty.Declaration.custom()
                    .name("title")
                    .dataType("TEXT")
                    .templateId(propertyId)
                    .description("标题")
                    .isRequired(true)
                    .scope("PUBLIC")
                    .appearance(new HashMap<>())
                    .build();

            declaration.setProperties(Collections.singletonList(pd));

            return declaration;
        }

        private TaskPropertiesDeclaration buildTaskPropertyDeclarationsNullTemplate() {
            TaskPropertiesDeclaration declaration = new TaskPropertiesDeclaration();
            declaration.setTaskId(taskId);
            TaskProperty.Declaration pd = TaskProperty.Declaration.custom()
                    .name("title")
                    .dataType("TEXT")
                    .description("标题")
                    .isRequired(true)
                    .scope("PUBLIC")
                    .appearance(new HashMap<>())
                    .build();

            declaration.setProperties(Collections.singletonList(pd));

            return declaration;
        }

        private List<Map<String, Object>> mockPropertyRows() {

            Map<String, Object> build = MapBuilder.<String, Object>get()
                    .put("id", propertyId)
                    .put("task_id", taskId)
                    .put("template_id", Entities.emptyId())
                    .put("name", "title")
                    .put("required", true)
                    .put("description", "标题")
                    .put("scope", "PUBLIC")
                    .put("dataType", "TEXT")
                    .put("sequence", 1)
                    .put("identifiable", true)
                    .build();

            return Collections.singletonList(build);
        }

        private List<Map<String, Object>> mockPropertyRows2() {

            Map<String, Object> build = MapBuilder.<String, Object>get()
                    .put("id", propertyId)
                    .put("task_id", taskId)
                    .put("template_id", templatePropertyId)
                    .put("name", "title")
                    .put("required", true)
                    .put("description", "标题")
                    .put("scope", "PUBLIC")
                    .put("dataType", "TEXT")
                    .put("sequence", 1)
                    .put("identifiable", true)
                    .build();

            return Collections.singletonList(build);
        }
    }

    public TaskPropertiesDeclaration taskPropertiesDeclarationBuild() {
        final TaskProperty.Declaration.Builder declaration = TaskProperty.Declaration.custom();
        declaration.name("333");
        declaration.dataType(Enums.toString(PropertyDataType.TEXT));
        declaration.description("");
        declaration.isRequired(true);
        declaration.scope(Enums.toString(PropertyScope.PUBLIC));
        declaration.appearance(new HashMap<>());
        List<TaskProperty.Declaration> builders = Collections.singletonList(declaration.build());
        TaskPropertiesDeclaration taskPropertiesDeclaration = new TaskPropertiesDeclaration();
        taskPropertiesDeclaration.setTaskId("f0676858e3cb469c8202ca55fdc5661a");
        taskPropertiesDeclaration.setProperties(builders);
        return taskPropertiesDeclaration;
    }

    public Map<String, Object> mockValues() {
        Map<String, Object> values = MapBuilder.<String, Object>get()
                .put("id", "f56f718c218141de886a64eca51c3cbb")
                .put("task_id", "f0676858e3cb469c8202ca55fdc5661a")
                .put("data_type", Enums.toString(PropertyDataType.TEXT))
                .put("required", true)
                .put("name", "333")
                .put("sequence", 13)
                .put("identifiable", true)
                .build();
        return values;
    }
}
