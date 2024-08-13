/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.conf.runtime.support;

import com.huawei.fitframework.conf.runtime.ClientConfig.Secure;

import java.util.Optional;

/**
 * 表示 {@link Secure} 的默认实现。
 *
 * @author 杭潇
 * @since 2024-03-18
 */
public class DefaultClientSecure implements Secure {
    /**
     * 配置项：{@code 'ignore-hostname'}。
     */
    private Boolean ignoreHostname;

    /**
     * 配置项：{@code 'ignore-trust'}。
     */
    private Boolean ignoreTrust;

    /**
     * 配置项：{@code 'trust-store-file'}。
     */
    private String trustStoreFile;

    /**
     * 配置项：{@code 'encrypted'}。
     */
    private Boolean encrypted;

    /**
     * 配置项：{@code 'key-store-file'}。
     */
    private String keyStoreFile;

    /**
     * 配置项：{@code 'trust-store-password'}。
     */
    private String trustStorePassword;

    /**
     * 配置项：{@code 'key-store-password'}。
     */
    private String keyStorePassword;

    /**
     * 设置是否忽略 hostname。
     *
     * @param ignoreHostname 表示是否忽略 hostname 的 {@link Boolean}。
     */
    public void setIgnoreHostname(Boolean ignoreHostname) {
        this.ignoreHostname = ignoreHostname;
    }

    /**
     * 设置是否忽略根证书。
     *
     * @param ignoreTrust 表示是否忽略根证书的 {@link Boolean}。
     */
    public void setIgnoreTrust(Boolean ignoreTrust) {
        this.ignoreTrust = ignoreTrust;
    }

    /**
     * 设置是否加密密码。
     *
     * @param encrypted 表示是否加密密码的 {@link Boolean}。
     */
    public void setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * 设置秘钥库文件的地址。
     *
     * @param keyStoreFile 表示秘钥库文件地址的 {@link String}。
     */
    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    @Override
    public boolean encrypted() {
        return this.encrypted != null ? this.encrypted : false;
    }

    /**
     * 设置根证书文件的地址。
     *
     * @param trustStoreFile 表示根证书密码的 {@link String}。
     */
    public void setTrustStoreFile(String trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    /**
     * 设置秘钥库的密码。
     *
     * @param trustStorePassword 表示秘钥库密码的 {@link String}。
     */
    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    /**
     * 设置秘钥库内容的密码。
     *
     * @param keyStorePassword 表示秘钥库内容的密码的 {@link String}。
     */
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    public boolean ignoreHostName() {
        return this.ignoreHostname != null ? this.ignoreHostname : false;
    }

    @Override
    public boolean ignoreTrust() {
        return this.ignoreTrust != null ? this.ignoreTrust : false;
    }

    @Override
    public Optional<String> keyStoreFile() {
        return Optional.ofNullable(this.keyStoreFile);
    }

    @Override
    public Optional<String> trustStoreFile() {
        return Optional.ofNullable(this.trustStoreFile);
    }

    @Override
    public Optional<String> trustStorePassword() {
        return Optional.ofNullable(this.trustStorePassword);
    }

    @Override
    public Optional<String> keyStorePassword() {
        return Optional.ofNullable(this.keyStorePassword);
    }
}
