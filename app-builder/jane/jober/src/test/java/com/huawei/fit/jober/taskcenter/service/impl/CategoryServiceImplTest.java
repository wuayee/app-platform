/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static com.huawei.fit.jober.Tests.matchArguments;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@DisplayName("测试 CategoryService 实现")
class CategoryServiceImplTest {
    private static final String OBJECT_TYPE = "DEMO";

    private static final String OBJECT_ID = "35cca93bfa4f436d823e1188df5fc519";

    private static final String OPERATOR = "admin";

    private static final LocalDateTime OPERATION_TIME = LocalDateTime.now();

    private static final String SELECT_CATEGORIES_SQL;

    private static final String SELECT_USAGE_SQL;

    private static final String UPDATE_USAGE_SQL;

    private static final String INSERT_USAGE_SQL;

    private static final String DELETE_USAGE_SQL;

    static {
        ClassLoader loader = CategoryServiceImplTest.class.getClassLoader();
        try {
            SELECT_CATEGORIES_SQL = IoUtils.content(loader, "sql/select-categories.sql");
            SELECT_USAGE_SQL = IoUtils.content(loader, "sql/select-category-usage.sql");
            UPDATE_USAGE_SQL = IoUtils.content(loader, "sql/update-category-usage.sql");
            INSERT_USAGE_SQL = IoUtils.content(loader, "sql/insert-category-usage.sql");
            DELETE_USAGE_SQL = IoUtils.content(loader, "sql/delete-category-usage.sql");
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load embedded resources.", ex);
        }
    }

    private DynamicSqlExecutor executor;

    private CategoryServiceImpl impl;

    private OperationContext context;

    private static Object cachedAllRows;

    @BeforeAll
    static void setupAll() {
        List<Map<String, Object>> rows = Arrays.asList(
                MapBuilder.<String, Object>get()
                        .put("id", "6379ffc9be92468f92dc8f6c55320711")
                        .put("name", "未完成")
                        .put("group_id", "a2958974cda848a18916a85ffd4be754")
                        .put("group_name", "完成状态")
                        .build(),
                MapBuilder.<String, Object>get()
                        .put("id", "769df34a8e6b4571ac27dafd388194c1")
                        .put("name", "已完成")
                        .put("group_id", "a2958974cda848a18916a85ffd4be754")
                        .put("group_name", "完成状态")
                        .build(),
                MapBuilder.<String, Object>get()
                        .put("id", "92a88836aaf44780be5fbaee65445f2d")
                        .put("name", "无风险")
                        .put("group_id", "192ecd772c66416385bb210aefd668fa")
                        .put("group_name", "健康状态")
                        .build(),
                MapBuilder.<String, Object>get()
                        .put("id", "9631ac5853a5450c88ac5e6a1f48f701")
                        .put("name", "有风险")
                        .put("group_id", "192ecd772c66416385bb210aefd668fa")
                        .put("group_name", "健康状态")
                        .build());
        DynamicSqlExecutor sqlExecutor = mock(DynamicSqlExecutor.class);
        when(sqlExecutor.executeQuery(SELECT_CATEGORIES_SQL)).thenReturn(rows);
        Method method;
        try {
            String className = CategoryServiceImpl.class.getName() + "$CategoryRow";
            Class<?> categoryRowClass = CategoryServiceImpl.class.getClassLoader().loadClass(className);
            Field allRowsField = categoryRowClass.getDeclaredField("allRows");
            allRowsField.setAccessible(true);
            cachedAllRows = allRowsField.get(null);
            allRowsField.set(null, null);
            method = categoryRowClass.getDeclaredMethod("all", DynamicSqlExecutor.class);
            method.setAccessible(true);
            method.invoke(null, sqlExecutor);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to initialize unit tests for category service.", ex);
        }
    }

    @AfterAll
    static void teardown() {
        try {
            String className = CategoryServiceImpl.class.getName() + "$CategoryRow";
            Class<?> categoryRowClass = CategoryServiceImpl.class.getClassLoader().loadClass(className);
            Field allRowsField = categoryRowClass.getDeclaredField("allRows");
            allRowsField.setAccessible(true);
            allRowsField.set(null, cachedAllRows);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to initialize unit tests for category service.", ex);
        }
    }

    @BeforeEach
    void setup() {
        this.executor = mock(DynamicSqlExecutor.class);
        this.impl = new CategoryServiceImpl(this.executor);
        this.context = mock(OperationContext.class);
    }

    @Nested
    @DisplayName("测试更新类目使用信息")
    class UpdateUsageTest {
        @BeforeEach
        void setup() {
            Map<String, Object> usageRow = usageRow(
                    "27599743bdd3479fa0df15fb93dddfa9",
                    "192ecd772c66416385bb210aefd668fa", "92a88836aaf44780be5fbaee65445f2d");
            List<Map<String, Object>> usageRows = Collections.singletonList(usageRow);
            when(executor.executeQuery(eq(SELECT_USAGE_SQL), argThat(matchArguments(OBJECT_ID, OBJECT_TYPE))))
                    .thenReturn(usageRows);
            when(context.operator()).thenReturn(OPERATOR);
        }

        @Test
        @DisplayName("更新类目使用信息成功")
        void should_update_usage_when_group_exist() {
            when(executor.executeUpdate(eq(UPDATE_USAGE_SQL), argThat(matchArguments(
                    "9631ac5853a5450c88ac5e6a1f48f701", OPERATOR, alwaysTrue(), "27599743bdd3479fa0df15fb93dddfa9"))))
                    .thenReturn(1);
            impl.saveUsages(OBJECT_TYPE, OBJECT_ID, Collections.singletonList("有风险"), context);
        }

        @Test
        @DisplayName("当更新数据库时返回受影响的行数少于需要更新的数据记录数量时，抛出异常")
        void should_throw_when_affected_rows_less_than_rows_to_update() {
            when(executor.executeUpdate(eq(UPDATE_USAGE_SQL), argThat(matchArguments(
                    "9631ac5853a5450c88ac5e6a1f48f701", OPERATOR, alwaysTrue(), "27599743bdd3479fa0df15fb93dddfa9"))))
                    .thenReturn(0);

            ServerInternalException ex = assertThrows(ServerInternalException.class, () -> impl.saveUsages(
                    OBJECT_TYPE, OBJECT_ID, Collections.singletonList("有风险"), context));
            assertEquals("Failed to update usage of category into database. [id=27599743bdd3479fa0df15fb93dddfa9]",
                    ex.getMessage());
        }
    }

    @Nested
    @DisplayName("测试插入类目使用信息")
    class InsertUsageTest {
        @BeforeEach
        void setup() {
            when(executor.executeQuery(eq(SELECT_USAGE_SQL), argThat(matchArguments(OBJECT_ID, OBJECT_TYPE))))
                    .thenReturn(Collections.emptyList());
            when(context.operator()).thenReturn(OPERATOR);
        }

        @Test
        @DisplayName("插入类目使用信息成功")
        void should_insert_usage_when_group_not_exist() {
            when(executor.executeUpdate(eq(INSERT_USAGE_SQL), argThat(matchArguments(alwaysTrue(), OBJECT_TYPE,
                    OBJECT_ID, "a2958974cda848a18916a85ffd4be754", "769df34a8e6b4571ac27dafd388194c1",
                    OPERATOR, alwaysTrue(), OPERATOR, alwaysTrue())))).thenReturn(1);
            impl.saveUsages(OBJECT_TYPE, OBJECT_ID, Collections.singletonList("已完成"), context);
        }

        @Test
        @DisplayName("插入类目使用信息成功")
        void should_throw_when_affected_rows_less_than_rows_to_insert() {
            when(executor.executeUpdate(eq(INSERT_USAGE_SQL), argThat(matchArguments(alwaysTrue(), OBJECT_TYPE,
                    OBJECT_ID, "a2958974cda848a18916a85ffd4be754", "769df34a8e6b4571ac27dafd388194c1",
                    OPERATOR, alwaysTrue(), OPERATOR, alwaysTrue())))).thenReturn(0);
            ServerInternalException ex = assertThrows(ServerInternalException.class, () -> impl.saveUsages(
                    OBJECT_TYPE, OBJECT_ID, Collections.singletonList("已完成"), context));
            assertEquals("Failed to insert category usage into database.", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("测试删除类目使用信息")
    class DeleteUsageTest {
        @BeforeEach
        void setup() {
            Map<String, Object> usageRow = usageRow("bb32dc0129da46c9b247ba426d320654",
                    "a2958974cda848a18916a85ffd4be754", "6379ffc9be92468f92dc8f6c55320711");
            List<Map<String, Object>> usageRows = Collections.singletonList(usageRow);
            when(executor.executeQuery(eq(SELECT_USAGE_SQL), argThat(matchArguments(OBJECT_ID, OBJECT_TYPE))))
                    .thenReturn(usageRows);
            when(context.operator()).thenReturn(OPERATOR);
        }

        @Test
        @DisplayName("删除使用信息成功")
        void should_delete_redundant_usages() {
            when(executor.executeUpdate(eq(DELETE_USAGE_SQL), argThat(matchArguments(
                    "bb32dc0129da46c9b247ba426d320654")))).thenReturn(1);
            impl.saveUsages(OBJECT_TYPE, OBJECT_ID, Collections.emptyList(), context);
        }
    }

    @Test
    @DisplayName("当设置的类目不是已知的类目时抛出异常")
    void should_throw_when_category_unknown() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.impl.saveUsages(OBJECT_TYPE,
                OBJECT_ID, Collections.singletonList("错误类目"), this.context));
        assertEquals(ErrorCodes.UNKNOWN_CATEGORY.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("查询类目使用情况成功，并返回按对象唯一标识分组后的数据")
    void should_return_usages() {
        List<Map<String, Object>> usageRows = Arrays.asList(
                usageRow("a2958974cda848a18916a85ffd4be754", "769df34a8e6b4571ac27dafd388194c1"),
                usageRow("192ecd772c66416385bb210aefd668fa", "92a88836aaf44780be5fbaee65445f2d"));
        when(executor.executeQuery(eq(SELECT_USAGE_SQL), argThat(matchArguments(OBJECT_ID, OBJECT_TYPE))))
                .thenReturn(usageRows);
        Map<String, List<String>> usages = impl.listUsages(OBJECT_TYPE, Collections.singletonList(OBJECT_ID), context);
        List<String> categories = usages.get(OBJECT_ID);
        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertTrue(categories.contains("已完成"));
        assertTrue(categories.contains("无风险"));
    }

    @Test
    @DisplayName("查询类目使用情况时，若未提供对象唯一标识，则返回空的映射")
    void should_return_empty_usages_when_no_object_supplied() {
        Map<String, List<String>> usages = impl.listUsages(OBJECT_TYPE, Collections.emptyList(), context);
        assertTrue(usages.isEmpty());
    }

    private static Map<String, Object> usageRow(String groupId, String categoryId) {
        return usageRow(UUID.randomUUID().toString().replace("-", ""), groupId, categoryId);
    }

    private static Map<String, Object> usageRow(String usageId, String groupId, String categoryId) {
        return MapBuilder.<String, Object>get()
                .put("id", usageId)
                .put("object_type", CategoryServiceImplTest.OBJECT_TYPE)
                .put("object_id", CategoryServiceImplTest.OBJECT_ID)
                .put("category_group_id", groupId)
                .put("category_id", categoryId)
                .put("created_by", OPERATOR)
                .put("created_at", OPERATION_TIME)
                .put("updated_by", OPERATOR)
                .put("updated_at", OPERATION_TIME)
                .build();
    }

    private static Predicate<Object> alwaysTrue() {
        return value -> true;
    }
}
