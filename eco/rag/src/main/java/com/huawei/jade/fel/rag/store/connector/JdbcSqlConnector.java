/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector;

import com.huawei.fitframework.log.Logger;
import com.huawei.jade.fel.rag.store.connector.schema.RdbColumn;
import com.huawei.jade.fel.rag.store.query.Expression;

import lombok.NonNull;
import lombok.Setter;

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
    private static final int BATCH_LIMIT = 1000;

    @Setter
    private int batchLimit = BATCH_LIMIT;
    private Connection connection = null;

    /**
     * 根据传入的数据库类型及其他连接参数创建 {@link JdbcSqlConnector} 实例。
     *
     * @param dbType dbType
     * @param properties properties
     * @param database database
     */
    public JdbcSqlConnector(@NonNull JdbcType dbType, ConnectorProperties properties, String database) {
        String connStr = "jdbc:" + dbType.getName() + "://" + properties.getHost() + ":" + properties.getPort() + "/"
            + database;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(connStr, properties.getUsername(), properties.getPassword());
        } catch (SQLException | ClassNotFoundException e) {
            logger.error(filterSQLSensitiveInfo(e.getMessage()));
            logger.error("Failed to establish connection with {}", dbType.getName());
            throw new IllegalArgumentException("Connection failed");
        }
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
    public void put(String tableName, Map<String, Object> value) {
    }

    /**
     * 根据传入的表名和删除语句进行删除。
     *
     * @param tableName 表示表名的 {@link String}。
     * @param expr 表示删除语句的 {@link Expression}。
     */
    @Override
    public void delete(String tableName, Expression expr) {
    }

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
        } catch (SQLException e) {
            logger.error("Failed to execute sql, err msg: {}", filterSQLSensitiveInfo(e.getMessage()));
        } finally {
            close(rs);
            close(stmt);
        }
        return rows;
    }

    private static String filterSQLSensitiveInfo(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        String filtered = message.replaceAll("jdbc:[\\w:]+//[\\w.-]+:[0-9]+/[\\w.-]+", "jdbc:[FILTERED]");
        filtered = filtered.replaceAll("SELECT .* FROM .*", "SELECT [FILTERED] FROM [FILTERED]");
        filtered = filtered.replaceAll("INSERT INTO .* VALUES .*", "INSERT INTO [FILTERED] VALUES [FILTERED]");
        return filtered;
    }

    private List<Map<String, Object>> executeSql(Statement stmt, String sql) {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSet rs = null;

        try {
            if (stmt.execute(sql)) {
                rs = stmt.getResultSet();
                rows = processRows(rs);
            }
        } catch (SQLException e) {
            logger.error("Failed to execute sql in execute batch {}", filterSQLSensitiveInfo(e.getMessage()));
        } finally {
            close(rs);
        }

        return rows;
    }

    @Override
    public List<List<Map<String, Object>>> executeBatch(List<String> sqls) {
        if (sqls == null || sqls.isEmpty()) {
            return null;
        }
        Statement stmt = null;
        List<List<Map<String, Object>>> resultSets = new ArrayList<>();

        try {
            stmt = connection.createStatement();
            connection.setAutoCommit(false);
            int batchSize = 0;

            for (String sql : sqls) {
                resultSets.add(executeSql(stmt, sql));
                batchSize++;
                if (batchSize >= batchLimit) {
                    connection.commit();
                    batchSize = 0;
                }
            }

            connection.commit();
        } catch (SQLException e) {
            logger.error("Failed to execute transaction");
        } finally {
            close(stmt);
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Failed to close connection {}", filterSQLSensitiveInfo(e.getMessage()));
                }
            }
        }

        return resultSets;
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
            logger.error("Failed to close statement {}", filterSQLSensitiveInfo(e.getMessage()));
        }
    }

    private void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close result set {}", filterSQLSensitiveInfo(e.getMessage()));
        }
    }

    /**
     * 创建表。
     *
     * @param tableName 表名称
     * @param columns 列信息
     * @throws SQLException sql异常
     */
    @Override
    public void createTable(String tableName, List<RdbColumn> columns) throws SQLException {
        List<String> commentSqls = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            String.format("CREATE TABLE IF NOT EXISTS %s (inner_id SERIAL PRIMARY KEY, ", tableName));
        if (columns != null && !columns.isEmpty()) {
            for (int i = 0; i < columns.size(); i++) {
                RdbColumn column = columns.get(i);
                sb.append(column.toSqlString());
                sb.append(i == columns.size() - 1 ? "" : ",");
                commentSqls.add(String.format("COMMENT ON COLUMN %s.user_%s is '%s';", tableName, column.getName(),
                    column.getDesc()));
            }
            sb.append(");");
        }
        String createSql = sb.toString();
        Statement stmt = null;
        try {
            disableAutoCommit();
            stmt = connection.createStatement();
            stmt.addBatch(createSql);
            for (String commentSql : commentSqls) {
                stmt.addBatch(commentSql);
            }
            int[] result = stmt.executeBatch();
            connection.commit();
            logger.info(String.format("Succeed to create table knowledge: %s", tableName));
        } catch (SQLException e) {
            rollBack();
            logger.error(String.format("Failed to create table knowledge: %s, rolled back", tableName));
            throw new SQLException(String.format("Failed to create table knowledge: %s, rolled back", tableName));
        } finally {
            enableAutoCommit();
            close(stmt);
        }
    }

    /**
     * 删除表。
     *
     * @param tableName 表名称
     * @throws SQLException sql异常
     */
    @Override
    public void dropTable(String tableName) throws SQLException {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            String sql = String.format("DROP TABLE %S;", tableName);
            stmt.executeUpdate(sql);
            logger.info(String.format("Succeed to drop table: %s", tableName));
        } catch (SQLException e) {
            logger.error(String.format("Failed to drop table: %s", tableName));
            throw new SQLException(String.format("Failed to drop table: %s", tableName));
        } finally {
            close(stmt);
        }
    }

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

    @Override
    public void createIndex(String tableName, List<RdbColumn> columns) throws SQLException {
        if (columns == null || columns.isEmpty()) {
            return;
        }
        List<String> indexSqls = new ArrayList<>();
        for (RdbColumn column : columns) {
            String colName = column.getName();
            if (column.isIndex()) {
                indexSqls.add(
                    String.format("CREATE INDEX %s_idx_user_%s ON %s(user_%s);", tableName, colName, tableName,
                        colName));
            }
        }
        Statement stmt = null;
        try {
            disableAutoCommit();
            stmt = connection.createStatement();
            for (String indexSql : indexSqls) {
                stmt.addBatch(indexSql);
            }
            int[] result = stmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            rollBack();
            logger.error(String.format("Fail to create index on table %s", tableName));
            throw new SQLException(String.format("Fail to create index on table %s", tableName));
        } finally {
            enableAutoCommit();
            close(stmt);
        }
    }

    private void disableAutoCommit() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("Failed to disable auto commit.");
        }
    }

    private void enableAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error("Failed to enable auto commit.");
        }
    }

    private void rollBack() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.error("Failed to rollback.");
        }
    }
}
