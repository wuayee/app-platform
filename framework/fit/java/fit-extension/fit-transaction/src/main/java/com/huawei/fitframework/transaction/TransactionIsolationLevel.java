/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 为事务提供隔离级别的定义。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-22
 */
public enum TransactionIsolationLevel {
    /**
     * 不支持事务。
     */
    NONE {
        @Override
        public void accept(Connection connection) throws SQLException {
            connection.setTransactionIsolation(Connection.TRANSACTION_NONE);
        }
    },

    /**
     * 脏读、幻读、不可重复读都是支持的。
     */
    READ_UNCOMMITTED {
        @Override
        public void accept(Connection connection) throws SQLException {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        }
    },

    /**
     * 不支持脏读，但支持不可重复读和幻读。
     */
    READ_COMMITTED {
        @Override
        public void accept(Connection connection) throws SQLException {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }
    },

    /**
     * 不支持脏读和不可重复读，允许幻读。
     */
    REPEATABLE_READ {
        @Override
        public void accept(Connection connection) throws SQLException {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        }
    },

    /**
     * 脏读、不可重复读、幻读都不支持。
     */
    SERIALIZABLE {
        @Override
        public void accept(Connection connection) throws SQLException {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        }
    };

    public abstract void accept(Connection connection) throws SQLException;
}
