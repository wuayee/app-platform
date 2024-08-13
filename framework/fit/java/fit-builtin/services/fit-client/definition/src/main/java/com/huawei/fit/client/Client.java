/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.client;

import com.huawei.fitframework.inspection.Nonnull;

import java.util.Set;

/**
 * 请求客户端。
 *
 * @author 季聿阶
 * @since 2022-09-19
 */
public interface Client {
    /**
     * 请求一个响应。
     *
     * @param request 表示请求的 {@link Request}。调用保证请求一定不为 {@code null}。
     * @return 表示响应的 {@link Response}。
     */
    Response requestResponse(@Nonnull Request request);

    /**
     * 获取支持的协议号集合。
     *
     * @return 表示支持的协议号集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> getSupportedProtocols();
}
