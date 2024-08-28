/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.huawei.fit.jober.common.ServerInternalException;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 为 {@link DynamicSqlExecutor} 提供默认实现。
 *
 * @author 孙怡菲
 * @since 2023-07-24
 */
@Component
public class DefaultDynamicSqlExecutor implements DynamicSqlExecutor {
    private static final Logger log = Logger.get(DefaultDynamicSqlExecutor.class);

    private final SqlSessionFactory factory;

    public DefaultDynamicSqlExecutor(SqlSessionFactory factory) {
        this.factory = notNull(factory, "The factory of session to invoke SQL dynamically cannot be null.");
    }

    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        return this.executeQuery(sql, null);
    }

    @Override
    public List<Map<String, Object>> executeQuery(String sql, List<?> args) {
        return this.execute(sql, args, statement -> {
            try (ResultSet results = statement.executeQuery()) {
                int numberOfColumns = results.getMetaData().getColumnCount();
                List<String> columnNames = new ArrayList<>(numberOfColumns);
                for (int i = 1; i <= numberOfColumns; i++) {
                    columnNames.add(results.getMetaData().getColumnName(i));
                }
                List<Map<String, Object>> records = new LinkedList<>();
                while (results.next()) {
                    int columnIndex = 0;
                    Map<String, Object> record = new LinkedHashMap<>();
                    while (columnIndex < columnNames.size()) {
                        String columnName = columnNames.get(columnIndex++);
                        Object columnValue = results.getObject(columnIndex);
                        record.put(columnName, columnValue);
                    }
                    records.add(record);
                }
                return records;
            }
        });
    }

    @Override
    public Object executeScalar(String sql) {
        return this.executeScalar(sql, null);
    }

    @Override
    public Object executeScalar(String sql, List<?> args) {
        return this.execute(sql, args, statement -> {
            try (ResultSet results = statement.executeQuery()) {
                if (results.getMetaData().getColumnCount() < 1 || !results.next()) {
                    return null;
                } else {
                    return results.getObject(1);
                }
            }
        });
    }

    @Override
    public int executeUpdate(String sql) {
        return this.executeUpdate(sql, null);
    }

    @Override
    public int executeUpdate(String sql, List<?> args) {
        return this.execute(sql, args, PreparedStatement::executeUpdate);
    }

    private <T> T execute(String sql, List<?> args, StatementExecutor<T> executor) {
        SqlSession session = null;
        T result;
        try {
            session = this.factory.openSession(false);
            try (PreparedStatement statement = session.getConnection().prepareStatement(sql)) {
                if (args != null) {
                    int parameterIndex = 0;
                    while (parameterIndex < args.size()) {
                        Object value = args.get(parameterIndex++);
                        statement.setObject(parameterIndex, value);
                    }
                }
                result = executor.execute(statement);
            }
            session.commit();
            return result;
        } catch (SQLException | PersistenceException ex) {
            String errorMsg = ex.getMessage();
            log.error("Failed to executor dynamic sql: {}, exception message: {}", sql, errorMsg);
            if (session != null) {
                session.rollback();
            }
            throw new ServerInternalException("Failed to execute dynamic sql.");
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @FunctionalInterface
    private interface StatementExecutor<T> {
        /**
         * sql语句执行器
         *
         * @param statement 要执行的sql语句
         * @return sql语句执行结果
         * @throws SQLException sql语句执行异常
         */
        T execute(PreparedStatement statement) throws SQLException;
    }
}