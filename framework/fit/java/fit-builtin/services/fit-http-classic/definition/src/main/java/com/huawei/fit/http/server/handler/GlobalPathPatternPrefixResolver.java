/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import com.huawei.fit.http.server.handler.support.GlobalPathPatternPrefixResolverComposite;

import java.util.Optional;

/**
 * 表示全局路径样式的前缀解析器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-11
 */
@FunctionalInterface
public interface GlobalPathPatternPrefixResolver {
    /**
     * 解析获取全局路径样式的前缀。
     *
     * @return 表示解析出的全局路径样式的前缀的 {@link Optional}{@code <}{@link String}{@code >}。
     */
    Optional<String> resolve();

    /**
     * 将多个全局路径样式的前缀解析器合并为一个。
     *
     * @param resolvers 表示多个全局路径样式的前缀解析器的 {@link GlobalPathPatternPrefixResolver}{@code []}。
     * @return 表示合并后的全局路径样式的前缀解析器的 {@link GlobalPathPatternPrefixResolver}。
     */
    static GlobalPathPatternPrefixResolver combine(GlobalPathPatternPrefixResolver... resolvers) {
        return new GlobalPathPatternPrefixResolverComposite(resolvers);
    }
}
