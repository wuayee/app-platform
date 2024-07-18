/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.fel.rag.store.connector.schema.DbFieldType;
import com.huawei.jade.fel.rag.store.connector.schema.RdbColumn;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JDBC数据库连接器测试
 *
 * @since 2024-05-22
 */
class JdbcSqlConnectorTest {
    private static final String TEST_TABLE_NAME = "tableknowledge_1_101";

    String getCheckExistsSql(String metaTable, String tabelName) {
        return "select EXISTS(select 1 from " + metaTable + " where tablename ='" + tabelName + "');";
    }

    @Test
    @DisplayName("给定表名与列信息在RDB中创表")
    @Disabled
    public void given_tableName_and_column_info_should_createTable() throws SQLException {
        ConnectorProperties prop =
                new ConnectorProperties("51.36.139.24", 5433, "postgres", "postgres");
        JdbcSqlConnector conn = new JdbcSqlConnector(JdbcType.POSTGRESQL, prop, "wqtest");
        List<RdbColumn> columns = new ArrayList<>();
        columns.add(new RdbColumn("col1", DbFieldType.VARCHAR, "col1 desc", false));
        columns.add(new RdbColumn("col2", DbFieldType.NUMBER, "col3 desc", false));
        columns.add(new RdbColumn("col3", DbFieldType.VARCHAR, "col3 desc", false));
        conn.createTable(TEST_TABLE_NAME, columns);

        List<Map<String, Object>> res =
                conn.execute(getCheckExistsSql("pg_catalog.pg_tables", TEST_TABLE_NAME));
        assertEquals(res.size(), 1);
        assertEquals(res.get(0).get("exists"), true);

        conn.close();
    }

    @Test
    @DisplayName("给定表名与列信息在RDB表中创建索引")
    @Disabled
    public void given_tableName_and_column_index_info_should_create_index() throws SQLException {
        ConnectorProperties prop =
                new ConnectorProperties("51.36.139.24", 5433, "postgres", "postgres");
        JdbcSqlConnector conn = new JdbcSqlConnector(JdbcType.POSTGRESQL, prop, "wqtest");
        List<RdbColumn> columns = new ArrayList<>();
        columns.add(new RdbColumn("col1", DbFieldType.VARCHAR, "col1 desc", true));
        columns.add(new RdbColumn("col2", DbFieldType.NUMBER, "col3 desc", true));
        columns.add(new RdbColumn("col3", DbFieldType.VARCHAR, "col3 desc", false));
        conn.createIndex(TEST_TABLE_NAME, columns);

        List<Map<String, Object>> res =
                conn.execute(getCheckExistsSql("pg_catalog.pg_indexes", TEST_TABLE_NAME));
        assertEquals(res.size(), 1);
        assertEquals(res.get(0).get("exists"), true);

        conn.close();
    }

    @Test
    @DisplayName("在RDB中删除给定名称的表")
    @Disabled
    public void given_tableName_should_drop_table() throws SQLException {
        ConnectorProperties prop =
                new ConnectorProperties("51.36.139.24", 5433, "postgres", "postgres");
        JdbcSqlConnector conn = new JdbcSqlConnector(JdbcType.POSTGRESQL, prop, "wqtest");
        conn.dropTable(TEST_TABLE_NAME);

        List<Map<String, Object>> res =
                conn.execute(getCheckExistsSql("pg_catalog.pg_indexes", TEST_TABLE_NAME));
        assertEquals(res.size(), 1);
        assertEquals(res.get(0).get("exists"), false);
        conn.close();
    }
}