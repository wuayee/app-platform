/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.common.oms.entity;

/**
 * 表示主机与端口的组合的类。
 *
 * @author 李金绪
 * @since 2024-11-27
 */
public class NetPoint {
    private String protocol;
    private String host;
    private int port;

    public NetPoint(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    /**
     * 获取端口号。
     *
     * @return 表示端口号的 {@code int}。
     */
    public int getPort() {
        return this.port;
    }

    /**
     * 设置端口号。
     *
     * @param port 表示端口号的 {@code int}。
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 获取主机名。
     *
     * @return 表示主机名的 {@link String}。
     */
    public String getHost() {
        return this.host;
    }

    /**
     * 设置主机名。
     *
     * @param host 表示主机名的 {@link String}。
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取协议名。
     *
     * @return 表示协议名的 {@link String}。
     */
    public String getProtocol() {
        return this.protocol;
    }

    /**
     * 设置协议名。
     *
     * @param protocol 表示协议名的 {@link String}。
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}