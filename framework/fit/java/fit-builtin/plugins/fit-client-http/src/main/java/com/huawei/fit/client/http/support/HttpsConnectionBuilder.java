/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import com.huawei.fit.http.protocol.Protocol;

/**
 * 表示 Https 链接的构建器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-10
 */
public class HttpsConnectionBuilder extends HttpConnectionBuilder {
    @Override
    public Protocol protocol() {
        return Protocol.HTTPS;
    }
}
