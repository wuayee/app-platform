/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.ioc.BeanContainer;

import java.util.Optional;

/**
 * 表示从配置项 {@code 'server.http.context-path'} 中获取全局路径样式的前缀解析器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-07-03
 */
public class FitGlobalPathPatternPrefixResolver implements GlobalPathPatternPrefixResolver {
    private final String prefix;

    FitGlobalPathPatternPrefixResolver(BeanContainer container) {
        notNull(container, "The bean container cannot be null.");
        this.prefix = container.plugin().config().get("server.http.context-path", String.class);
    }

    @Override
    public Optional<String> resolve() {
        return Optional.ofNullable(this.prefix);
    }
}
