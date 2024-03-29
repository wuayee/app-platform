/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import com.huawei.fitframework.ioc.BeanContainer;

/**
 * 表示 {@link GlobalPathPatternPrefixResolver} 的提供者。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-11
 */
@FunctionalInterface
public interface GlobalPathPatternPrefixResolverSupplier {
    /**
     * 从指定容器中获取全局路径样式的前缀的解析器。
     *
     * @param container 表示指定容器的 {@link BeanContainer}。
     * @return 表示从指定容器中获取到的全局路径样式的前缀的解析器的 {@link GlobalPathPatternPrefixResolver}。
     */
    GlobalPathPatternPrefixResolver get(BeanContainer container);
}
