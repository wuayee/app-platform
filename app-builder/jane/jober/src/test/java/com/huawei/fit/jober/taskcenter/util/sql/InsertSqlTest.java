/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@DisplayName("测试 InsertSql")
class InsertSqlTest {
    private static final String TABLE_NAME = "test";

    private static final String COLUMN_ID = "id";

    private static final String COLUMN_NAME = "name";

    private static final String VALUE_ID = "29c3cbdd40054b5eb6d4d6f78d432bd0";

    private static final String VALUE_NAME = "hello";

    private static boolean matchValues(List<Object> args) {
        return args.size() == 2 && Objects.equals(VALUE_ID, args.get(0)) && Objects.equals(VALUE_NAME, args.get(1));
    }

    @Nested
    @DisplayName("异常场景")
    class Fault {
        @Test
        @DisplayName("设置待插入数据的表为空白字符串")
        void should_throw_when_into_blank_table() {
            InsertSql sql = InsertSql.custom();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> sql.into(" "));
            assertEquals("The table to insert values cannot be blank.", ex.getMessage());
        }

        @Test
        @DisplayName("设置待插入的列为空白字符串")
        void should_throw_when_column_of_value_is_blank() {
            InsertSql sql = InsertSql.custom();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> sql.value(" ", VALUE_ID));
            assertEquals("The column to insert value cannot be blank.", ex.getMessage());
        }

        @Test
        @DisplayName("未指定表")
        void should_throw_when_no_table_specified() {
            DynamicSqlExecutor executor = mock(DynamicSqlExecutor.class);
            InsertSql sql = InsertSql.custom().value(COLUMN_ID, VALUE_ID).value(COLUMN_NAME, VALUE_ID);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> sql.execute(executor));
            assertEquals("No table specified to insert values.", ex.getMessage());
        }

        @Test
        @DisplayName("指定没有用以解决冲突的列")
        void should_throw_when_specify_no_columns_to_resolve_conflict() {
            InsertSql sql = InsertSql.custom();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> sql.conflict(null, " "));
            assertEquals("No columns specified to resolve conflicts.", ex.getMessage());
        }

        @Test
        @DisplayName("指定没有用以更新的列")
        void should_throw_when_specify_no_columns_to_update() {
            InsertSql sql = InsertSql.custom();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> sql.update(null, " "));
            assertEquals("No columns specified to update when conflicted.", ex.getMessage());
        }

        @Test
        @DisplayName("未指定待返回的列")
        void should_throw_when_no_returning_columns_specified() {
            DynamicSqlExecutor executor = mock(DynamicSqlExecutor.class);
            InsertSql sql = InsertSql.custom().into(TABLE_NAME).value(COLUMN_ID, VALUE_ID)
                    .value(COLUMN_NAME, VALUE_NAME);
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> sql.executeAndReturn(executor, null, " "));
            assertEquals("No columns specified to return values.", ex.getMessage());
        }

        @Test
        @DisplayName("未指定列")
        void should_throw_when_no_columns_specified() {
            DynamicSqlExecutor executor = mock(DynamicSqlExecutor.class);
            InsertSql sql = InsertSql.custom().into(TABLE_NAME);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> sql.execute(executor));
            assertEquals("No value specified to insert into table.", ex.getMessage());
        }

        @Test
        @DisplayName("指定了待更新的列，但未指定用以判定冲突的列")
        void should_throw_when_update_columns_specified_with_no_unique_columns() {
            DynamicSqlExecutor executor = mock(DynamicSqlExecutor.class);
            InsertSql sql = InsertSql.custom().into(TABLE_NAME).value(COLUMN_ID, VALUE_ID)
                    .value(COLUMN_NAME, VALUE_NAME).update(COLUMN_ID, COLUMN_NAME);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> sql.execute(executor));
            assertEquals("No columns specified but required to resolve conflicts.", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("正常场景")
    class Success {
        private InsertSql sql;

        private DynamicSqlExecutor executor;

        @BeforeEach
        void setup() {
            sql = InsertSql.custom().into(TABLE_NAME).value(COLUMN_ID, VALUE_ID).value(COLUMN_NAME, VALUE_NAME);
            executor = mock(DynamicSqlExecutor.class);
        }

        @Test
        @DisplayName("当仅插入单行数据时生成正确的 SQL")
        void should_build_sql_for_simple_insert() {
            when(executor.executeUpdate(any(), any())).thenReturn(1);

            int affectedRows = sql.execute(executor);
            assertEquals(1, affectedRows);

            final String expectedSql = "INSERT INTO \"test\"(\"id\", \"name\") VALUES(?, ?)";
            verify(executor, times(1)).executeUpdate(eq(expectedSql), argThat(InsertSqlTest::matchValues));
        }

        @Test
        @DisplayName("插入多行")
        void should_build_sql_to_insert_multiple_rows() {
            final String valueId2 = "27b99a1f334645929bf10f50d3b8a8bf";
            final String valueName2 = "world";
            sql.next().value(COLUMN_ID, valueId2).value(COLUMN_NAME, valueName2);
            when(executor.executeUpdate(any(), any())).thenReturn(1);

            int affectedRows = sql.execute(executor);
            assertEquals(1, affectedRows);

            String expectedSql = "INSERT INTO \"test\"(\"id\", \"name\") VALUES(?, ?), (?, ?)";
            verify(executor, times(1)).executeUpdate(eq(expectedSql), argThat(args -> args.size() == 4
                    && Objects.equals(args.get(0), VALUE_ID) && Objects.equals(args.get(1), VALUE_NAME)
                    && Objects.equals(args.get(2), valueId2) && Objects.equals(args.get(3), valueName2)));
        }

        @Test
        @DisplayName("当设置了冲突检测时生成正确的 SQL")
        void should_build_sql_with_conflict_resolution() {
            sql.conflict(COLUMN_ID, COLUMN_NAME);

            when(executor.executeUpdate(any(), any())).thenReturn(1);

            int affectedRows = sql.execute(executor);
            assertEquals(1, affectedRows);

            final String expectedSql
                    = "INSERT INTO \"test\"(\"id\", \"name\") VALUES(?, ?) ON CONFLICT (\"id\", \"name\") DO NOTHING";
            verify(executor, times(1)).executeUpdate(eq(expectedSql), argThat(InsertSqlTest::matchValues));
        }

        @Test
        @DisplayName("当设置了返回列时生成正确的 SQL")
        void should_build_sql_with_returning_columns() {
            when(executor.executeQuery(any(), any())).thenReturn(Collections.emptyList());

            List<Map<String, Object>> rows = sql.executeAndReturn(executor, COLUMN_ID, COLUMN_NAME);
            assertTrue(rows.isEmpty());

            final String expectedSql = "INSERT INTO \"test\"(\"id\", \"name\") VALUES(?, ?) RETURNING \"id\", \"name\"";
            verify(executor, times(1)).executeQuery(eq(expectedSql), argThat(InsertSqlTest::matchValues));
        }

        @Test
        @DisplayName("正确处理 SqlValue")
        void should_build_sql_with_sql_value() {
            SqlValue value = mock(SqlValue.class);
            when(value.get()).thenReturn(VALUE_NAME);
            when(value.wrapPlaceholder(any())).thenAnswer(arg -> arg.getArguments()[0] + "::JSON");
            sql.value(COLUMN_NAME, value);

            when(executor.executeUpdate(any(), any())).thenReturn(1);

            int affectedRows = sql.execute(executor);
            assertEquals(1, affectedRows);

            String expectedSql = "INSERT INTO \"test\"(\"id\", \"name\") VALUES(?, ?::JSON)";
            ArgumentMatcher<List<Object>> expectedArgs = args -> args.size() == 2
                    && Objects.equals(VALUE_ID, args.get(0)) && Objects.equals(VALUE_NAME, args.get(1));
            verify(executor, times(1)).executeUpdate(eq(expectedSql), argThat(expectedArgs));
        }

        @Test
        @DisplayName("当指定了用以解决冲突的列时生成正确的 SQL")
        void should_build_sql_with_unique_columns() {
            sql.conflict(COLUMN_ID);
            when(executor.executeUpdate(any(), any())).thenReturn(1);

            int affectedRows = sql.execute(executor);
            assertEquals(1, affectedRows);

            String expectedSql = "INSERT INTO \"test\"(\"id\", \"name\") VALUES(?, ?) ON CONFLICT (\"id\") DO NOTHING";
            ArgumentMatcher<List<Object>> expectedArgs = InsertSqlTest::matchValues;
            verify(executor, times(1)).executeUpdate(eq(expectedSql), argThat(expectedArgs));
        }

        @Test
        @DisplayName("当指定了用以解决冲突的列和待更新的列时生成正确的 SQL")
        void should_build_sql_with_unique_and_update_columns() {
            sql.conflict(COLUMN_ID).update(COLUMN_NAME);
            when(executor.executeUpdate(any(), any())).thenReturn(1);

            int affectedRows = sql.execute(executor);
            assertEquals(1, affectedRows);

            String expectedSql
                    = "INSERT INTO \"test\"(\"id\", \"name\") VALUES(?, ?) ON CONFLICT (\"id\") DO UPDATE SET \"name\" = EXCLUDED.\"name\"";
            ArgumentMatcher<List<Object>> expectedArgs = InsertSqlTest::matchValues;
            verify(executor, times(1)).executeUpdate(eq(expectedSql), argThat(expectedArgs));
        }

        @Test
        @DisplayName("当指定了用以解决冲突的列和待返回的列，但未指定待更新的列时生成正确的 SQL")
        void should_build_sql_with_unique_and_returning_columns_but_no_update_columns() {
            sql.conflict(COLUMN_ID, COLUMN_NAME);
            when(executor.executeQuery(any(), any())).thenReturn(Collections.emptyList());

            List<Map<String, Object>> rows = sql.executeAndReturn(executor, COLUMN_NAME);
            assertTrue(rows.isEmpty());

            String expectedSql
                    = "INSERT INTO \"test\"(\"id\", \"name\") VALUES(?, ?) ON CONFLICT (\"id\", \"name\") DO UPDATE SET \"id\" = EXCLUDED.\"id\" RETURNING \"name\"";
            ArgumentMatcher<List<Object>> expectedArgs = InsertSqlTest::matchValues;
            verify(executor, times(1)).executeQuery(eq(expectedSql), argThat(expectedArgs));
        }
    }
}