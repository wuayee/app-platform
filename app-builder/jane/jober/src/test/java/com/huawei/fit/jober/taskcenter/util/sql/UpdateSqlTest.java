/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.List;
import java.util.Objects;

@DisplayName("测试 UpdateSql")
class UpdateSqlTest {
    private static final String TABLE_NAME = "test";

    private static final String COLUMN_ID = "id";

    private static final Integer VALUE_ID = 1;

    private static final String COLUMN_NAME = "name";

    private static final String VALUE_NAME = "hello";

    private UpdateSql sql;

    private DynamicSqlExecutor executor;

    @BeforeEach
    void setup() {
        this.sql = UpdateSql.custom();
        this.executor = mock(DynamicSqlExecutor.class);
    }

    @Test
    @DisplayName("未指定表时抛出异常")
    void should_throw_when_no_table_specified() {
        this.sql.set(COLUMN_NAME, VALUE_NAME);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> this.sql.execute(this.executor));
        assertEquals("No table specified to update.", ex.getMessage());
    }

    @Test
    @DisplayName("未指定需要设置到列中的值时抛出异常")
    void should_throw_when_no_column_specified() {
        this.sql.table(TABLE_NAME);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> this.sql.execute(this.executor));
        assertEquals("No value specified to update to table.", ex.getMessage());
    }

    @Test
    @DisplayName("未指定筛选条件时生成的 SQL 正确")
    void should_build_sql_without_condition() {
        this.sql.table(TABLE_NAME).set(COLUMN_ID, VALUE_ID).set(COLUMN_NAME, VALUE_NAME);
        when(this.executor.executeUpdate(any(), any())).thenReturn(1);

        int affectedRows = this.sql.execute(this.executor);
        assertEquals(1, affectedRows);

        String expectedSql = "UPDATE \"test\" SET \"id\" = ?, \"name\" = ?";
        ArgumentMatcher<List<Object>> expectedArgs = args -> args.size() == 2
                && Objects.equals(VALUE_ID, args.get(0)) && Objects.equals(VALUE_NAME, args.get(1));
        verify(this.executor, times(1)).executeUpdate(eq(expectedSql), argThat(expectedArgs));
    }

    @Test
    @DisplayName("当提供了条件时，生成正确的 SQL")
    void should_build_sql_with_condition() {
        Condition condition = mock(Condition.class);
        doAnswer(invocation -> {
            invocation.<SqlBuilder>getArgument(0).append("\"id\"" + " = ?");
            invocation.<List<Object>>getArgument(1).add(VALUE_ID);
            return null;
        }).when(condition).toSql(any(), any());

        this.sql.table(TABLE_NAME).set(COLUMN_NAME, VALUE_NAME).where(condition);
        when(this.executor.executeUpdate(any(), any())).thenReturn(1);

        int affectedRows = this.sql.execute(this.executor);
        assertEquals(1, affectedRows);

        String expectedSql = "UPDATE \"test\" SET \"name\" = ? WHERE \"id\" = ?";
        ArgumentMatcher<List<Object>> expectedArgs = args -> args.size() == 2
                && Objects.equals(VALUE_NAME, args.get(0)) && Objects.equals(VALUE_ID, args.get(1));
        verify(this.executor, times(1)).executeUpdate(eq(expectedSql), argThat(expectedArgs));
    }

    @Test
    @DisplayName("支持 SqlValue")
    void should_support_sql_value() {
        SqlValue value = mock(SqlValue.class);
        when(value.get()).thenReturn(VALUE_NAME);
        when(value.wrapPlaceholder(any())).thenAnswer(invocation -> invocation.getArgument(0).toString() + "::JSON");
        this.sql.table(TABLE_NAME).set(COLUMN_NAME, value);
        when(this.executor.executeUpdate(any(), any())).thenReturn(1);

        int affectedRows = this.sql.execute(this.executor);
        assertEquals(1, affectedRows);

        verify(this.executor, times(1)).executeUpdate(
                eq("UPDATE \"test\" SET \"name\" = ?::JSON"),
                argThat(args -> args.size() == 1 && Objects.equals(VALUE_NAME, args.get(0))));
    }
}
