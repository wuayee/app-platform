/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.oms.config;

import com.huawei.fitframework.annotation.AcceptConfigValues;
import com.huawei.fitframework.annotation.Component;

/**
 * 表示 Nacos 的配置。
 *
 * @author 李金绪
 * @since 2024-11-27
 */
@Component
@AcceptConfigValues("nacos")
public class NacosConfig {
    private String address;
    private String username;
    private String password;
    private boolean tlsEnabled;
    private boolean clientAuth;
    private String clientTrustCert;

    /**
     * 获取服务地址。
     *
     * @return 表示地址的 {@link String}。
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * 设置服务地址。
     *
     * @param address 表示地址的 {@link String}。
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取用户名。
     *
     * @return 表示用户名的 {@link String}。
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * 设置用户名。
     *
     * @param username 表示用户名的 {@link String}。
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码。
     *
     * @return 表示密码的 {@link String}。
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * 设置密码。
     *
     * @param password 表示密码的 {@link String}。
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取 TLS 是否启用。
     *
     * @return 表示是否启用的 {@code boolean}。
     */
    public boolean isTlsEnabled() {
        return this.tlsEnabled;
    }

    /**
     * 设置 TLS 是否启用。
     *
     * @param tlsEnabled 表示是否启用的 {@code boolean}。
     */
    public void setTlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    /**
     * 获取客户端是否需要认证。
     *
     * @return 表示是否需要认证的 {@code boolean}。
     */
    public boolean isClientAuth() {
        return this.clientAuth;
    }

    /**
     * 设置客户端是否需要认证。
     *
     * @param clientAuth 表示是否需要认证的 {@code boolean}。
     */
    public void setClientAuth(boolean clientAuth) {
        this.clientAuth = clientAuth;
    }

    /**
     * 获取证书。
     *
     * @return 表示是否信任的 {@link String}。
     */
    public String getClientTrustCert() {
        return this.clientTrustCert;
    }

    /**
     * 设置证书。
     *
     * @param clientTrustCert 表示证书的 {@link String}。
     */
    public void setClientTrustCert(String clientTrustCert) {
        this.clientTrustCert = clientTrustCert;
    }
}
