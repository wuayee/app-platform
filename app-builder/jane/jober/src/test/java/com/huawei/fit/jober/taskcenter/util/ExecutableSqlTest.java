/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("测试可执行的 SQL")
class ExecutableSqlTest {
    @Test
    @DisplayName("当存在 Iterable 类型的参数时，返回预期的 SQL")
    void should_return_sql_with_args_when_contains_iterable_value() {
        shouldFlatArgs(Arrays.asList(1, 2));
    }

    @Test
    @DisplayName("当存在 Iterator 类型的参数时，返回预期的 SQL")
    void should_return_sql_with_args_when_contains_iterator_value() {
        shouldFlatArgs(Arrays.asList(1, 2).iterator());
    }

    @Test
    @DisplayName("当存在数组类型的参数时，返回预期的 SQL")
    void should_return_sql_with_args_when_contains_array_value() {
        shouldFlatArgs(new Object[] {1, 2});
    }

    private static void shouldFlatArgs(Object ids) {
        String sql = "UPDATE temp SET is_deleted = ${deleted} WHERE id IN (${ids}) AND owner = ${owner}";
        Map<String, Object> args = new HashMap<>(3);
        args.put("deleted", true);
        args.put("ids", ids);
        args.put("owner", "self");
        ExecutableSql executableSql = ExecutableSql.resolve(sql, args);

        final String expectedSql = "UPDATE temp SET is_deleted = ? WHERE id IN (?, ?) AND owner = ?";
        final List<Object> expectedArgs = Arrays.asList(true, 1, 2, "self");

        assertEquals(expectedSql, executableSql.sql());
        assertIterableEquals(expectedArgs, executableSql.args());
    }
}
