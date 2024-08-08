/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.vectorestore.milvus.config;

import com.huawei.fitframework.annotation.AcceptConfigValues;
import com.huawei.fitframework.annotation.Component;

import io.milvus.param.LogLevel;

/**
 * 表示 milvus 客户端的配置。
 *
 * @author 易文渊
 * @since 2024-08-12
 */
@Component
@AcceptConfigValues("fel.milvus.client")
public class MilvusClientConfig {
    private String host = "localhost";
    private int port = 19530;
    private String databaseName = "default";
    private String token;
    private LogLevel logLevel = LogLevel.Error;

    /**
     * 获取 Milvus 服务器的主机名。
     *
     * @return 表示 Milvus 服务器主机名的 {@link String}。
     */
    public String getHost() {
        return this.host;
    }

    /**
     * 设置 Milvus 服务器的主机名。
     *
     * @param host 表示 Milvus 服务器主机名的 {@link String}。
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取 Milvus 服务器的端口号。
     *
     * @return 表示 Milvus 服务器端口号的 {@code int}。
     */
    public int getPort() {
        return this.port;
    }

    /**
     * 设置 Milvus 服务器的端口号。
     *
     * @param port 表示 Milvus 服务器端口号的 {@code int}。
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 获取 Milvus 数据库的名称。
     *
     * @return 表示 Milvus 数据库名称的 {@link String}。
     */
    public String getDatabaseName() {
        return this.databaseName;
    }

    /**
     * 设置 Milvus 数据库的名称。
     *
     * @param databaseName 表示 Milvus 数据库名称的 {@link String}。
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * 获取 Milvus 服务器的访问凭证。
     *
     * @return 表示 Milvus 服务器访问令凭证的 {@link String}。
     */
    public String getToken() {
        return this.token;
    }

    /**
     * 设置 Milvus 服务器的访问凭证。
     *
     * @param token 表示 Milvus 服务器访问令凭证的 {@link String}。
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取 Milvus 客户端的日志级别。
     *
     * @return 表示 Milvus 客户端日志级别的 {@link LogLevel}。
     */
    public LogLevel getLogLevel() {
        return this.logLevel;
    }

    /**
     * 设置 Milvus 客户端的日志级别。
     *
     * @param logLevel 表示 Milvus 客户端日志级别的 {@link LogLevel}。
     */
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
}