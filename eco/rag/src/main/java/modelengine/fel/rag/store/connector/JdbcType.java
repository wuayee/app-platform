/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store.connector;

/**
 * sql数据库类型。
 *
 * @since 2024-05-07
 */
public enum JdbcType {
    IBM("db2"),
    MYSQL("mysql"),
    MICROSOFT("microsoft:sqlserver"),
    POSTGRESQL("postgresql"),
    TERADATA("teradata");

    private String name;

    /**
     * 根据数据库名构建 {@link JdbcType} 实例。
     *
     * @param name 表示数据库名的 {@link String}。
     */
    JdbcType(String name) {
        this.name = name;
    }

    /**
     * 获取数据库名。
     *
     * @return 返回数据库名。
     */
    String getName() {
        return name;
    }
}
