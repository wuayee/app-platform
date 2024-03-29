/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.client.http.support;

/**
 * 表示 Https 链接的构建器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-10
 */
public class HttpsConnectionBuilder extends HttpConnectionBuilder {
    /** 表示加密通信协议。 */
    public static final String HTTPS = "https";

    @Override
    public String type() {
        return HTTPS;
    }

    @Override
    protected int defaultPort() {
        return 443;
    }
}
