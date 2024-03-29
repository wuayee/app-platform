/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.client.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.client.InvokerFactory;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.broker.client.RouterFactory;

import java.lang.reflect.Method;

/**
 * 为 {@link RouterFactory} 提供默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2021-10-26
 */
public class DefaultRouterFactory implements RouterFactory {
    private final InvokerFactory invokerFactory;

    public DefaultRouterFactory(InvokerFactory invokerFactory) {
        this.invokerFactory = notNull(invokerFactory, "The invoker factory cannot be null.");
    }

    @Override
    public Router create(String genericableId, boolean isMicro, Method genericableMethod) {
        return new DefaultRouter(this.invokerFactory, genericableId, isMicro, genericableMethod);
    }
}
