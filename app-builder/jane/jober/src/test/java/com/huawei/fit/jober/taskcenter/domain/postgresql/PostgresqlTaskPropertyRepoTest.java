/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fit.jober.Tests.matchArguments;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.huawei.fit.jane.task.domain.PropertyScope;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.PropertyValidator;

import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class PostgresqlTaskPropertyRepoTest {
    private static final String TASK_ID = "4586e518514646898137c51cd689997f";

    private static final String SELECT_FROM = "SELECT \"id\", \"task_id\", \"name\", \"required\", \"description\", "
            + "\"scope\", \"data_type\", \"sequence\", \"appearance\", \"identifiable\", \"template_id\" "
            + "FROM \"task_property\"";

    private static final Map<String, Object> ROW_ID = MapBuilder.<String, Object>get()
            .put("id", "86b068f469cc40f2aa6cab0ee1b4b3e4")
            .put("task_id", TASK_ID)
            .put("name", "id")
            .put("required", true)
            .put("description", "")
            .put("scope", "PUBLIC")
            .put("data_type", "INTEGER")
            .put("sequence", 1)
            .put("appearance", "{}")
            .put("identifiable", true)
            .put("template_id", "00000000000000000000000000000000")
            .build();

    private static final Map<String, Object> ROW_NAME = MapBuilder.<String, Object>get()
            .put("id", "c98f7d6449114d3e9cd857fadc3edfc6")
            .put("task_id", TASK_ID)
            .put("name", "name")
            .put("required", false)
            .put("description", "")
            .put("scope", "PUBLIC")
            .put("data_type", "TEXT")
            .put("sequence", 1)
            .put("appearance", "{}")
            .put("identifiable", false)
            .put("template_id", "00000000000000000000000000000000")
            .build();

    private TaskProperty.Repo repo;

    private DynamicSqlExecutor executor;

    private PropertyValidator validator;

    private ObjectSerializer serializer;

    private Plugin plugin;

    @BeforeEach
    void setup() {
        validator = mock(PropertyValidator.class);
        executor = mock(DynamicSqlExecutor.class);
        CategoryService categoryService = mock(CategoryService.class);
        serializer = mock(ObjectSerializer.class);
        this.plugin = mock(Plugin.class);
        repo = new PostgresqlTaskPropertyRepo(executor, validator, categoryService, serializer, this.plugin);
    }

    private void mockSerializer(Map<String, Object> map, String str) {
        Charset charset = StandardCharsets.UTF_8;
        byte[] bytes = str.getBytes(charset);
        when(this.serializer.serialize(argThat(args -> is(args, map)), eq(charset))).thenReturn(bytes);
        when(this.serializer.deserialize(argThat((byte[] args) -> Arrays.equals(args, bytes)), eq(charset),
                argThat(PostgresqlTaskPropertyRepoTest::isMap))).thenReturn(map);
    }

    @Nested
    @DisplayName("创建任务属性")
    class Create {
        @Test
        @DisplayName("返回新创建的任务属性")
        void should_return_created_task_property() {
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom()
                    .name("priority").dataType("INTEGER").build();
            when(validator.validateTaskId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateName(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateDataType(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateDescription(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            OperationContext context = mock(OperationContext.class);
            mockSerializer(Collections.emptyMap(), "{}");
            mockPropertyRows(executor);

            TaskProperty property = repo.create(TASK_ID, declaration, context);
            assertEquals("priority", property.name());
            assertEquals(PropertyDataType.INTEGER, property.dataType());
            assertEquals(2, property.sequence());
            assertEquals("", property.description());
            assertFalse(property.required());
            assertFalse(property.identifiable());
            assertEquals(PropertyScope.PUBLIC, property.scope());
            assertTrue(property.appearance().isEmpty());

            String sql
                    = "INSERT INTO \"task_property\"(\"id\", \"task_id\", \"template_id\", \"name\", \"data_type\", \"sequence\", \"description\", \"required\", \"identifiable\", \"scope\", \"appearance\") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::JSON)";
            List<Object> args = Arrays.asList(TASK_ID, "00000000000000000000000000000000", "priority", "INTEGER", 2, "",
                    false, false, "PUBLIC", "{}");
            verify(executor, times(1)).executeUpdate(eq(sql),
                    argThat(actualArgs -> CollectionUtils.equals(actualArgs.subList(1, actualArgs.size()), args)));
        }

        @Test
        @DisplayName("如果所属任务定义包含实例，在添加必填属性时抛出异常")
        void should_throw_when_create_required_property_but_has_instances() {
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom()
                    .name("priority").dataType("INTEGER").isRequired(true).build();
            when(validator.validateTaskId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateName(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateDataType(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            OperationContext context = mock(OperationContext.class);
            mockSerializer(Collections.emptyMap(), "{}");
            mockHasInstances(executor, true);
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> repo.create(TASK_ID, declaration, context));
            assertEquals(ErrorCodes.NEW_PROPERTY_REQUIRED_WITH_INSTANCES.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当已存在与新创建属性同名的属性时抛出异常")
        void should_throw_when_property_with_same_name_exists() {
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().name("name").build();
            when(validator.validateTaskId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateName(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            mockPropertyRows(executor);
            OperationContext context = mock(OperationContext.class);
            mockSerializer(Collections.emptyMap(), "{}");
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> repo.create(TASK_ID, declaration, context));
            assertEquals(ErrorCodes.TASK_PROPERTY_NAME_EXIST.getErrorCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("修改任务属性")
    class Patch {
        @Test
        @DisplayName("修改声明中设置的属性")
        void happy_path() {
            String appearanceString = "{\"visible\": true}";
            Map<String, Object> appearance = MapBuilder.<String, Object>get().put("visible", false).build();
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom()
                    .name("priority").dataType("INTEGER").description("").isRequired(false).isIdentifiable(false)
                    .scope("PRIVATE").appearance(appearance).build();
            when(validator.validatePropertyId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateTaskId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateName(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateDataType(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateDescription(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateRequired(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateIdentifiable(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateScope(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateAppearance(any())).thenAnswer(invocation -> invocation.getArgument(0));
            OperationContext context = mock(OperationContext.class);
            mockHasInstances(executor, false);
            mockPropertyRows(executor);
            mockSerializer(appearance, appearanceString);
            String propertyId = cast(ROW_NAME.get("id"));

            EventPublisher publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(publisher);

            Map<String, Object> row = MapBuilder.<String, Object>get()
                    .put("id", propertyId).put("name", "demo-property").put("required", true).put("description", "")
                    .put("scope", "PUBLIC").put("data_type", "TEXT").put("sequence", 1).put("appearance", "{}")
                    .put("identifiable", false).put("template_id", "00000000000000000000000000000000").build();

            when(executor.executeQuery(eq("SELECT \"id\", \"task_id\", \"name\", \"required\", \"description\"," +
                            " \"scope\", \"data_type\", \"sequence\", \"appearance\", \"identifiable\", \"template_id\" FROM" +
                            " \"task_property\" WHERE \"id\" = ? AND \"task_id\" = ?"),
                    argThat(matchArguments(propertyId, TASK_ID)))).thenReturn(Collections.singletonList(row));

            repo.patch(TASK_ID, propertyId, declaration, context);

            String sql
                    = "UPDATE \"task_property\" SET \"name\" = ?, \"data_type\" = ?, \"sequence\" = ?, \"description\" = ?, \"required\" = ?, \"identifiable\" = ?, \"scope\" = ?, \"appearance\" = ?::JSON WHERE \"id\" = ?";
            List<Object> args = Arrays.asList("priority", "INTEGER", 2, "", false, false, "PRIVATE", appearanceString,
                    propertyId);
            verify(executor, times(1)).executeUpdate(eq(sql), argThat(arg -> CollectionUtils.equals(args, arg)));
        }

        @Test
        @DisplayName("当待修改的属性不存在时抛出异常")
        void should_throw_when_property_does_not_exist() {
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().dataType("INTEGER").build();
            when(validator.validatePropertyId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            OperationContext context = mock(OperationContext.class);
            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> repo.patch(TASK_ID, "2b814a22c25f486a87c6b04ba64ed368", declaration, context));
            assertEquals(ErrorCodes.TASK_PROPERTY_NOT_FOUND.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当待修改的属性不存在时抛出异常")
        void should_throw_when_expected_name_already_exists() {
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().name("id").build();
            when(validator.validatePropertyId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateTaskId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateName(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            mockPropertyRows(executor);
            OperationContext context = mock(OperationContext.class);
            String propertyId = cast(ROW_NAME.get("id"));
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> repo.patch(TASK_ID, propertyId, declaration, context));
            assertEquals(ErrorCodes.TASK_PROPERTY_NAME_EXIST.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当任务存在实例时，修改属性的数据类型抛出异常")
        void should_throw_when_modify_data_type_with_instances() {
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().dataType("INTEGER").build();
            when(validator.validatePropertyId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateTaskId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateDataType(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            OperationContext context = mock(OperationContext.class);
            String propertyId = cast(ROW_NAME.get("id"));
            mockPropertyRows(executor);
            mockHasInstances(executor, true);
            EventPublisher publisher = mock(EventPublisher.class);
            when(plugin.publisherOfEvents()).thenReturn(publisher);
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> repo.patch(TASK_ID, propertyId, declaration, context));
            assertEquals(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当任务存在实例时，将 required 修改为 true 将抛出异常")
        void should_throw_when_modify_required_to_true_with_instances() {
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().isRequired(true).build();
            when(validator.validatePropertyId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateTaskId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateRequired(any())).thenAnswer(invocation -> invocation.getArgument(0));
            OperationContext context = mock(OperationContext.class);
            String propertyId = cast(ROW_NAME.get("id"));
            mockHasInstances(executor, true);
            mockPropertyRows(executor);
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> repo.patch(TASK_ID, propertyId, declaration, context));
            assertEquals(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("当任务存在实例时，将 identifiable 修改为 true 将抛出异常")
        void should_throw_when_modify_identifiable_to_true_with_instances() {
            TaskProperty.Declaration declaration = TaskProperty.Declaration.custom().isIdentifiable(true).build();
            when(validator.validatePropertyId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateTaskId(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(validator.validateIdentifiable(any())).thenAnswer(invocation -> invocation.getArgument(0));
            OperationContext context = mock(OperationContext.class);
            String propertyId = cast(ROW_NAME.get("id"));
            mockHasInstances(executor, true);
            mockPropertyRows(executor);
            ConflictException ex = assertThrows(ConflictException.class,
                    () -> repo.patch(TASK_ID, propertyId, declaration, context));
            assertEquals(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES.getErrorCode(), ex.getCode());
        }
    }

    private static void mockPropertyRows(DynamicSqlExecutor executor) {
        String querySql = SELECT_FROM + " WHERE \"task_id\" = ?";
        List<Map<String, Object>> queryResult = Arrays.asList(ROW_ID, ROW_NAME);
        when(executor.executeQuery(eq(querySql), argThat(matchArguments(TASK_ID)))).thenReturn(queryResult);
    }

    private static void mockHasInstances(DynamicSqlExecutor executor, boolean hasInstances) {
        long count = hasInstances ? 1 : 0;
        when(executor.executeScalar(eq("SELECT COUNT(1) FROM \"task_instance_wide\" WHERE \"task_id\" = ?"),
                argThat(matchArguments(TASK_ID)))).thenReturn(count);
    }

    private static boolean is(Object value, Map<String, Object> expected) {
        if (value instanceof Map) {
            Map<?, ?> actual = (Map<?, ?>) value;
            if (actual.size() != expected.size()) {
                return false;
            }
            for (String key : expected.keySet()) {
                Object expectedValue = expected.get(key);
                Object actualValue = actual.get(key);
                if (!Objects.equals(expectedValue, actualValue)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean isMap(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType actual = (ParameterizedType) type;
            if (!Objects.equals(actual.getRawType(), Map.class) || actual.getActualTypeArguments().length != 2) {
                return false;
            }
            Type keyType = actual.getActualTypeArguments()[0];
            Type valueType = actual.getActualTypeArguments()[1];
            return Objects.equals(keyType, String.class) && Objects.equals(valueType, Object.class);
        }
        return false;
    }

    private static void mockValidator(PropertyValidator validator) {
        when(validator.validateTaskId(any(), any())).thenAnswer(args -> args.getArgument(0));
        when(validator.validatePropertyId(any(), any())).thenAnswer(args -> args.getArgument(0));
        when(validator.validateName(any(), any())).thenAnswer(args -> args.getArgument(0));
        when(validator.validateDescription(any(), any())).thenAnswer(args -> args.getArgument(0));
        when(validator.validateDataType(any(), any())).thenAnswer(args -> args.getArgument(0));
        when(validator.validateIdentifiable(any())).thenAnswer(args -> args.getArgument(0));
        when(validator.validateRequired(any())).thenAnswer(args -> args.getArgument(0));
        when(validator.validateScope(any(), any())).thenAnswer(args -> args.getArgument(0));
        when(validator.validateAppearance(any())).thenAnswer(args -> args.getArgument(0));
    }
}