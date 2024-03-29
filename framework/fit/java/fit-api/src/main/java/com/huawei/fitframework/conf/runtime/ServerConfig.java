/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.conf.runtime;

import java.util.Optional;

/**
 * 表示运行时 {@code 'server.*.'} 前缀的配置项。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-14
 */
public interface ServerConfig {
    /**
     * 获取端口是否打开的标志。
     * <p>如果显式地配置了 {@code 'server.*.is-enabled'}，则以配置内容为准，如果没有配置，则配置了 {@code 'server.*.port'}
     * 表示打开，没有配置则表示关闭。</p>
     * <p>如果这里的配置和安全配置都关闭了，则这里的配置会被强制打开。</p>
     *
     * @return 如果端口打开，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isProtocolEnabled();

    /**
     * 获取端口号。
     *
     * @return 表示端口号的 {@link Optional}{@code <}{@link Integer}{@code >}。
     */
    Optional<Integer> port();

    /**
     * 获取注册的端口号。
     *
     * @return 表示注册的端口号的 {@link Optional}{@code <}{@link Integer}{@code >}。
     */
    Optional<Integer> toRegisterPort();

    /**
     * 获取安全相关的配置信息。
     *
     * @return 表示安全相关的配置信息的 {@link Optional}{@code <}{@link Secure}{@code >}。
     */
    Optional<Secure> secure();

    /**
     * 表示运行时 {@code 'server.*.secure.'} 前缀的配置项。
     */
    interface Secure {
        /**
         * 获取端口是否打开的标志。
         * <p>如果显式地配置了 {@code 'server.*.secure.is-enabled'}，则以配置内容为准，如果没有配置，则配置了
         * {@code 'server.*.secure.port'} 表示打开，没有配置则表示关闭。</p>
         *
         * @return 如果端口打开，则返回 {@code true}，否则，返回 {@code false}。
         */
        boolean isProtocolEnabled();

        /**
         * 服务端是否校验客户端证书。
         * <p>如果显式地配置了 {@code 'server.*.need-client-auth'}，则以配置内容为准，如果没有配置，默认校验客户端证书。</p>
         *
         * @return 如果校验客户端证书，则返回 {@code true}，否则，返回 {@code false}。
         */
        boolean needClientAuth();

        /**
         * 获取端口号。
         *
         * @return 表示端口号的 {@link Optional}{@code <}{@link Integer}{@code >}。
         */
        Optional<Integer> port();

        /**
         * 获取密码是否加密。
         * <p>如果显式地配置了 {@code 'client.*.secure.encrypted'}，则以配置内容为准，如果没有配置表示关闭。</p>
         *
         * @return 如果加密，则返回 {@code true}，否则，返回 {@code false}。
         */
        boolean encrypted();

        /**
         * 获取注册的端口号。
         *
         * @return 表示注册的端口号的 {@link Optional}{@code <}{@link Integer}{@code >}。
         */
        Optional<Integer> toRegisterPort();

        /**
         * 获取秘钥库的文件地址。
         * <p>注意：当前约束输出文件的密码值与输入私钥文件的密码值均为相同内容。</p>
         *
         * @return 表示秘钥库的文件地址的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> keyStoreFile();

        /**
         * 获取秘钥库的文件地址。
         * <p>注意：当前约束密钥库的密码值与输入私钥文件的密码值均为相同内容。</p>
         *
         * @return 表示秘钥库的文件地址的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> trustStoreFile();

        /**
         * 获取秘钥库的密码。
         *
         * @return 表示秘钥库的秘钥的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> trustStorePassword();

        /**
         * 获取秘钥库的秘钥项的密码。
         *
         * @return 表示秘钥库的秘钥项的密码的 {@link Optional}{@code <}{@link String}{@link >}。
         */
        Optional<String> keyStorePassword();
    }
}
