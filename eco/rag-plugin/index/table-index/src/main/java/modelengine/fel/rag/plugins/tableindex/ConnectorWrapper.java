/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.plugins.tableindex;

import modelengine.fitframework.inspection.Validation;
import modelengine.fel.rag.store.connector.JdbcSqlConnector;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据库连接包装类。
 * <p>用于管理数据库连接资源。</p>
 *
 * @since 2024-06-07
 */
public class ConnectorWrapper {
    private JdbcSqlConnector connector;
    @Getter
    private long lastUsedTime;
    @Setter
    @Getter
    private boolean isIdle;

    /**
     * 根据传入的数据库连接构造{@link ConnectorWrapper}的实例。
     *
     * @param connector 表示数据库连接的{@link JdbcSqlConnector}。
     */
    public ConnectorWrapper(JdbcSqlConnector connector) {
        this.connector = Validation.notNull(connector, "Jdbc connector cannot be null");
        this.lastUsedTime = System.currentTimeMillis();
        this.isIdle = false;
    }

    /**
     * 获取数据库连接。
     *
     * @return 返回数据库连接。
     */
    public JdbcSqlConnector getConnector() {
        this.lastUsedTime = System.currentTimeMillis();
        this.isIdle = false;
        return connector;
    }

    /**
     * 关闭数据库连接。
     */
    public void close() {
        connector.close();
    }
}
