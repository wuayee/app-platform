/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import com.huawei.fit.client.Request;
import com.huawei.fit.http.protocol.Protocol;

/**
 * 表示通信链接的构建器。
 *
 * @author 季聿阶
 * @since 2023-09-10
 */
public interface ConnectionBuilder {
    /**
     * 构建一个链接。
     *
     * @param request 表示请求的 {@link Request}。
     * @return 表示构建出来的链接的 {@link String}。
     */
    String buildUrl(Request request);

    /**
     * 获取构建器的类型。
     *
     * @return 表示构建器类型的 {@link Protocol}。
     */
    Protocol protocol();
}
