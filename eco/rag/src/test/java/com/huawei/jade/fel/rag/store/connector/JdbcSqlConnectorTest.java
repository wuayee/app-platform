/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector;

import com.huawei.jade.fel.rag.store.connector.schema.DbFieldType;
import com.huawei.jade.fel.rag.store.connector.schema.RdbColumn;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * JDBC数据库连接器测试
 *
 * @since 2024-05-22
 */
class JdbcSqlConnectorTest {
    private static final String TEST_TABLE_NAME = "testKnowledge_1_2";

    @Test
    @Disabled
    public void given_tableName_and_column_info_should_createTable() {
        ConnectorProperties prop = new ConnectorProperties("51.36.139.24", 5433, "postgres", "postgres");
        JdbcSqlConnector conn = new JdbcSqlConnector(JdbcType.POSTGRESQL, prop, "wqtest");
        List<RdbColumn> columns = new ArrayList<>();
        columns.add(new RdbColumn("col1", DbFieldType.VARCHAR, "col1 desc"));
        columns.add(new RdbColumn("col2", DbFieldType.NUMBER, "col3 desc"));
        columns.add(new RdbColumn("col3", DbFieldType.VARCHAR, "col3 desc"));
        conn.createTable(TEST_TABLE_NAME, columns);
        conn.close();
    }

    @Test
    @Disabled
    public void given_tableName_should_drop_table() {
        ConnectorProperties prop = new ConnectorProperties("51.36.139.24", 5433, "postgres", "postgres");
        JdbcSqlConnector conn = new JdbcSqlConnector(JdbcType.POSTGRESQL, prop, "wqtest");
        conn.dropTable(TEST_TABLE_NAME);
        conn.close();
    }
}