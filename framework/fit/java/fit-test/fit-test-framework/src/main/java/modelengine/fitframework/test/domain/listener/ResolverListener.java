/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.test.domain.resolver.TestClassResolver;
import modelengine.fitframework.test.domain.resolver.TestContextConfiguration;

import java.util.Optional;

/**
 * 用于 Resolver 配置解析的监听器。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public class ResolverListener implements TestListener {
    @Override
    public Optional<TestContextConfiguration> config(Class<?> clazz) {
        TestContextConfiguration configuration = TestClassResolver.create().resolve(clazz);
        return Optional.of(configuration);
    }
}
