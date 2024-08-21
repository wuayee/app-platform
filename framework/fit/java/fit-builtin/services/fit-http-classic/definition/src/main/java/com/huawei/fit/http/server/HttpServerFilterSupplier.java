/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server;

import modelengine.fitframework.ioc.BeanContainer;

import java.util.List;

/**
 * 表示 {@link HttpServerFilter} 列表的提供者。
 *
 * @author 季聿阶
 * @since 2023-01-10
 */
public interface HttpServerFilterSupplier {
    /**
     * 从指定容器中获取过滤器列表。
     *
     * @param container 表示指定容器的 {@link BeanContainer}。
     * @return 表示指定容器中的过滤器列表的 {@link List}{@code <}{@link HttpServerFilter}{@code >}。
     */
    List<HttpServerFilter> get(BeanContainer container);
}
