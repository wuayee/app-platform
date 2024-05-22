/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector;

import com.huawei.fitframework.log.Logger;
import com.huawei.jade.fel.rag.store.query.Expression;

import lombok.NonNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC数据库连接器。
 *
 * @since 2024-05-07
 */
public class JdbcSqlConnector implements SqlConnector {
    private static final Logger logger = Logger.get(JdbcSqlConnector.class);
    private Connection connection = null;

    /**
     * 根据传入的数据库类型及其他连接参数创建 {@link JdbcSqlConnector} 实例。
     *
     * @param dbType dbType
     * @param properties properties
     * @param database database
     */
    public JdbcSqlConnector(@NonNull JdbcType dbType, ConnectorProperties properties, String database) {
        String connStr = "jdbc:" + dbType.getName()
                + "://" + properties.getHost() + ":" + properties.getPort() + "/" + database;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(connStr, properties.getUsername(), properties.getPassword());
        } catch (Exception e) {
            logger.debug(e.getMessage());
            logger.error("Failed to establish connection with {}", dbType.getName());
            throw new IllegalArgumentException("Connection failed");
        }
    }

    /**
     * 关闭连接。
     *
     * @throws SQLException SQLException
     */
    @Override
    protected void finalize() throws SQLException {
        connection.close();
    }

    /**
     * 用查询语句从指定的表名中进行查询。
     *
     * @param tableName 表示表名的 {@link String}。
     * @param expr 表示查询语句的 {@link String}。
     * @return 返回查询到的结果。
     */
    @Override
    public List<Map<String, Object>> get(String tableName, Expression expr) {
        return Collections.emptyList();
    }

    /**
     * 将键值数据插入到指定的表中。
     *
     * @param tableName 表示表名的 {@link String}。
     * @param value 表示要插入的键值数据的 {@link Map}{@code <}{@link String},{@link Object}{@code >}。
     */
    @Override
    public void put(String tableName, Map<String, Object> value) {}

    /**
     * 根据传入的表名和删除语句进行删除。
     *
     * @param tableName 表示表名的 {@link String}。
     * @param expr 表示删除语句的 {@link Expression}。
     */
    @Override
    public void delete(String tableName, Expression expr) {}

    /**
     * 执行sql语句。
     *
     * @param sql 表示sql语句的 {@link String}。
     * @return 返回执行的结果。
     */
    @Override
    public List<Map<String, Object>> execute(String sql) {
        List<Map<String, Object>> rows = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.createStatement();
            if (stmt.execute(sql)) {
                rs = stmt.getResultSet();
                rows = processRows(rs);
            }
            connection.commit();
        } catch (SQLException e) {
            logger.error("Failed to execute sql");
        } finally {
            close(rs);
            close(stmt);
            this.close();
        }
        return rows;
    }

    private List<Map<String, Object>> processRows(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData meta = null;

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String columnName = meta.getColumnName(i);
                row.put(columnName, rs.getObject(i));
            }

            rows.add(row);
        }
        return rows;
    }
    private void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close statement");
        }
    }

    private void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close result set");
        }
    }

    /**
     * 创建表。
     */
    @Override
    public void createTable() {}

    /**
     * 删除表。
     */
    @Override
    public void dropTable() {}

    /**
     * 关闭数据库连接。
     */
    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("Open config file err");
        }
    }
}
