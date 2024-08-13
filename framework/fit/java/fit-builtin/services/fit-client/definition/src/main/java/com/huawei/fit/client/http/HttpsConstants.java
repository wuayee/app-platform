/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.client.http;

/**
 * 表示 https 所需要的配置项值。
 *
 * @author 杭潇
 * @since 2024-03-21
 */
public class HttpsConstants {
    /** 表示客户端是否选择忽略服务端证书 */
    public static final String CLIENT_SECURE_IGNORE_TRUST = "client.http.secure.ignore-trust";

    /** 表示客户端是否选择忽略 hostname */
    public static final String CLIENT_SECURE_IGNORE_HOSTNAME = "client.http.secure.ignore-hostname";

    /** 表示客户端根证书文件路径 */
    public static final String CLIENT_SECURE_TRUST_STORE_FILE = "client.http.secure.trust-store-file";

    /** 表示客户端根证书文件密码 */
    public static final String CLIENT_SECURE_TRUST_STORE_PASSWORD = "client.http.secure.trust-store-password";

    /** 表示客户端客户端证书文件路径 */
    public static final String CLIENT_SECURE_KEY_STORE_FILE = "client.http.secure.key-store-file";

    /** 表示客户端客户端文件密码 */
    public static final String CLIENT_SECURE_KEY_STORE_PASSWORD = "client.http.secure.key-store-password";
}