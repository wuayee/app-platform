/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.service.impl.CategoryServiceImpl;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@DisplayName("测试 CategoryService")
class CategoryServiceTest {
    public static final String OBJECT_TYPE = "INSTANCE";

    public static final String OBJECT_ID = "f1a4f7849f53456b9c3cf96833ec02bd";

    private static final String ALL_CATEGORIES_SQL;

    private static final String SELECT_USAGE_BY_OBJECT_SQL;

    private static final String INSERT_USAGE_SQL;

    private static final String UPDATE_USAGE_SQL;

    private static final String DELETE_USAGE_SQL;

    private static final List<Map<String, Object>> ALL_CATEGORIES = Arrays.asList(
            categoryRow("7c96b4b048e14602a62864eafaa01360", "未开始", "ee1864c1613e408c9ec9b7facd3dcf48", "状态"),
            categoryRow("52f071d9aac14d849c77a5ea1cd1a042", "进行中", "ee1864c1613e408c9ec9b7facd3dcf48", "状态"),
            categoryRow("73c86bf0e4f4480cb486128e29b124b7", "已完成", "ee1864c1613e408c9ec9b7facd3dcf48", "状态"),
            categoryRow("575e1db5705f44b38c0f6105ec87937d", "时效内", "1531c9dd64c74703bdc6d26ce4fd69ef", "时效"),
            categoryRow("e46e0d9e5f024f62a25ccd29bc4bff20", "已逾期", "1531c9dd64c74703bdc6d26ce4fd69ef", "时效"),
            categoryRow("317ac28c221b4680b6151e66fbf7f90c", "有风险", "e65d7816e7d141b5b9451aba1bd0290a", "风险"),
            categoryRow("73c86bf0e4f4480cb486128e29b124b7", "无风险", "e65d7816e7d141b5b9451aba1bd0290a", "风险"));

    private static final String OPERATOR = "admin";

    private static final OperationContext CONTEXT;

    private static Map<String, Object> categoryRow(String id, String name, String groupId, String groupName) {
        return MapBuilder.<String, Object>get()
                .put("id", id)
                .put("name", name)
                .put("group_id", groupId)
                .put("group_name", groupName)
                .build();
    }

    static {
        ClassLoader loader = CategoryServiceTest.class.getClassLoader();
        try {
            ALL_CATEGORIES_SQL = IoUtils.content(loader, "sql/category-select.sql");
            SELECT_USAGE_BY_OBJECT_SQL = IoUtils.content(loader, "sql/category-usage-select-by-object.sql");
            INSERT_USAGE_SQL = IoUtils.content(loader, "sql/category-usage-insert.sql");
            UPDATE_USAGE_SQL = IoUtils.content(loader, "sql/category-usage-update.sql");
            DELETE_USAGE_SQL = IoUtils.content(loader, "sql/category-usage-delete.sql");
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load embedded resource.", ex);
        }
        CONTEXT = mock(OperationContext.class);
        when(CONTEXT.operator()).thenReturn(OPERATOR);
    }

    private CategoryService categoryService;

    private DynamicSqlExecutor executor;

    @BeforeEach
    void setup() {
        this.executor = mock(DynamicSqlExecutor.class);
        this.categoryService = new CategoryServiceImpl(this.executor);

        whenQuery(this.executor, ALL_CATEGORIES_SQL, ALL_CATEGORIES);
    }

    @Test
    @DisplayName("测试存储使用情况")
    void should_test_usage() {
        LocalDateTime operationTime = LocalDateTime.now();
        List<Map<String, Object>> usageRows = Arrays.asList(
                // 未开始
                MapBuilder.<String, Object>get()
                        .put("id", "0f13d8f5cdcd44c884bdf3bdb12cda31")
                        .put("object_type", OBJECT_TYPE)
                        .put("object_id", OBJECT_ID)
                        .put("category_group_id", "ee1864c1613e408c9ec9b7facd3dcf48")
                        .put("category_id", "7c96b4b048e14602a62864eafaa01360")
                        .put("created_by", OPERATOR)
                        .put("created_at", operationTime)
                        .put("updated_by", OPERATOR)
                        .put("updated_at", operationTime)
                        .build(),
                // 时效内
                MapBuilder.<String, Object>get()
                        .put("id", "05b8ad0febf9405ab95ed1477ce15e2f")
                        .put("object_type", OBJECT_TYPE)
                        .put("object_id", OBJECT_ID)
                        .put("category_group_id", "1531c9dd64c74703bdc6d26ce4fd69ef")
                        .put("category_id", "575e1db5705f44b38c0f6105ec87937d")
                        .put("created_by", OPERATOR)
                        .put("created_at", operationTime)
                        .put("updated_by", OPERATOR)
                        .put("updated_at", operationTime)
                        .build()
        );

        // 初始类目为：未开始（7c96b4b048e14602a62864eafaa01360）、时效内（575e1db5705f44b38c0f6105ec87937d）
        // 目标类目为：进行中（52f071d9aac14d849c77a5ea1cd1a042）、有风险（317ac28c221b4680b6151e66fbf7f90c）
        // 期望操作：
        // 更新使用信息：未开始 -> 进行中
        // 插入使用信息：有风险
        // 删除使用信息：时效内

        whenQuery(this.executor, SELECT_USAGE_BY_OBJECT_SQL, Arrays.asList(OBJECT_ID, OBJECT_TYPE), usageRows);

        whenUpdate(this.executor, UPDATE_USAGE_SQL, Arrays.asList("52f071d9aac14d849c77a5ea1cd1a042", OPERATOR,
                earlier(), "0f13d8f5cdcd44c884bdf3bdb12cda31"), 1);
        whenUpdate(this.executor, INSERT_USAGE_SQL, Arrays.asList(alwaysTrue(), OBJECT_TYPE, OBJECT_ID,
                "e65d7816e7d141b5b9451aba1bd0290a", "317ac28c221b4680b6151e66fbf7f90c", OPERATOR, earlier(),
                OPERATOR, earlier()), 1);
        whenUpdate(this.executor, DELETE_USAGE_SQL, Collections.singletonList("05b8ad0febf9405ab95ed1477ce15e2f"), 1);

        this.categoryService.saveUsages(OBJECT_TYPE, OBJECT_ID, Arrays.asList("进行中", "有风险"), CONTEXT);

        expectUpdate(this.executor, UPDATE_USAGE_SQL, Arrays.asList("52f071d9aac14d849c77a5ea1cd1a042", OPERATOR,
                earlier(), "0f13d8f5cdcd44c884bdf3bdb12cda31"));
        expectUpdate(this.executor, INSERT_USAGE_SQL, Arrays.asList(alwaysTrue(), OBJECT_TYPE, OBJECT_ID,
                "e65d7816e7d141b5b9451aba1bd0290a", "317ac28c221b4680b6151e66fbf7f90c", OPERATOR, earlier(),
                OPERATOR, earlier()));
        expectUpdate(this.executor, DELETE_USAGE_SQL, Collections.singletonList("05b8ad0febf9405ab95ed1477ce15e2f"));
    }

    @Test
    @DisplayName("当设置的类目不存在时抛出异常")
    void should_throw_when_category_unknown() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> this.categoryService
                .saveUsages(OBJECT_TYPE, OBJECT_ID, Collections.singletonList("不存在"), CONTEXT));
        assertEquals(ex.getCode(), ErrorCodes.UNKNOWN_CATEGORY.getErrorCode());
    }

    @Test
    @DisplayName("当插入使用信息失败时抛出异常")
    void should_throw_when_failed_to_insert() {
        whenQuery(this.executor, SELECT_USAGE_BY_OBJECT_SQL, Collections.emptyList());
        whenUpdate(this.executor, INSERT_USAGE_SQL, Arrays.asList(alwaysTrue(), OBJECT_TYPE, OBJECT_ID,
                "e65d7816e7d141b5b9451aba1bd0290a", "317ac28c221b4680b6151e66fbf7f90c", OPERATOR, earlier(),
                OPERATOR, earlier()), 0);
        ServerInternalException ex = assertThrows(ServerInternalException.class, () -> this.categoryService
                .saveUsages(OBJECT_TYPE, OBJECT_ID, Collections.singletonList("已完成"), CONTEXT));
        assertEquals("Failed to insert category usage into database.", ex.getMessage());
    }

    @Test
    @DisplayName("当更新使用信息失败时抛出异常")
    void should_throw_when_failed_to_update() {
        LocalDateTime operationTime = LocalDateTime.now();
        List<Map<String, Object>> usageRows = Collections.singletonList(MapBuilder.<String, Object>get()
                .put("id", "0f13d8f5cdcd44c884bdf3bdb12cda31")
                .put("object_type", OBJECT_TYPE)
                .put("object_id", OBJECT_ID)
                .put("category_group_id", "ee1864c1613e408c9ec9b7facd3dcf48")
                .put("category_id", "7c96b4b048e14602a62864eafaa01360")
                .put("created_by", OPERATOR)
                .put("created_at", operationTime)
                .put("updated_by", OPERATOR)
                .put("updated_at", operationTime)
                .build());

        // 初始类目为：未开始（7c96b4b048e14602a62864eafaa01360）
        // 目标类目为：进行中（52f071d9aac14d849c77a5ea1cd1a042）
        // 期望操作：
        // 更新使用信息：未开始 -> 进行中

        whenQuery(this.executor, SELECT_USAGE_BY_OBJECT_SQL, Arrays.asList(OBJECT_ID, OBJECT_TYPE), usageRows);

        whenUpdate(this.executor, UPDATE_USAGE_SQL, Arrays.asList("52f071d9aac14d849c77a5ea1cd1a042", OPERATOR,
                earlier(), "0f13d8f5cdcd44c884bdf3bdb12cda31"), 0);

        ServerInternalException ex = assertThrows(ServerInternalException.class, () -> this.categoryService
                .saveUsages(OBJECT_TYPE, OBJECT_ID, Arrays.asList("进行中", "有风险"), CONTEXT));
        assertEquals("Failed to update usage of category into database. [id=0f13d8f5cdcd44c884bdf3bdb12cda31]",
                ex.getMessage());
    }

    private static void whenUpdate(DynamicSqlExecutor executor, String expectedSql, List<?> expectedArgs, int result) {
        when(executor.executeUpdate(eq(expectedSql), listEq(expectedArgs))).thenReturn(result);
    }

    private static void whenQuery(DynamicSqlExecutor executor, String expectedSql, List<Map<String, Object>> result) {
        when(executor.executeQuery(eq(expectedSql))).thenReturn(result);
    }

    private static void whenQuery(DynamicSqlExecutor executor, String expectedSql, List<?> expectedArgs,
            List<Map<String, Object>> result) {
        when(executor.executeQuery(eq(expectedSql), listEq(expectedArgs))).thenReturn(result);
    }

    private static List<?> listEq(List<?> expectedArgs) {
        return argThat(args -> {
            if (args.size() != expectedArgs.size()) {
                return false;
            }
            for (int i = 0; i < args.size(); i++) {
                Object expected = expectedArgs.get(i);
                Object actual = args.get(i);
                Predicate<Object> predicate;
                if (expected instanceof Predicate) {
                    predicate = cast(expected);
                } else {
                    predicate = value -> Objects.equals(value, actual);
                }
                if (!predicate.test(actual)) {
                    return false;
                }
            }
            return true;
        });
    }

    private static Predicate<Object> alwaysTrue() {
        return value -> true;
    }

    private static Predicate<LocalDateTime> earlier() {
        return time -> !time.isAfter(LocalDateTime.now());
    }

    private static void expectUpdate(DynamicSqlExecutor executor, String expectedSql, List<?> expectedArgs) {
        verify(executor, times(1)).executeUpdate(eq(expectedSql), listEq(expectedArgs));
    }
}
