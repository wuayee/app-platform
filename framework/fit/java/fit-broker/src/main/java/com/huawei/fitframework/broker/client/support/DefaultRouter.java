/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.client.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.InvokerFactory;
import com.huawei.fitframework.broker.client.Router;

import java.lang.reflect.Method;

/**
 * {@link Router} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2021-06-17
 */
public class DefaultRouter implements Router {
    private final InvokerFactory invokerFactory;
    private final String genericableId;
    private final boolean isMicro;
    private final Method genericableMethod;

    DefaultRouter(InvokerFactory invokerFactory, String genericableId, boolean isMicro, Method genericableMethod) {
        this.invokerFactory = notNull(invokerFactory, "The invoker factory cannot be null.");
        this.genericableId = notBlank(genericableId, "The genericable id to route cannot be blank.");
        this.isMicro = isMicro;
        this.genericableMethod = genericableMethod;
    }

    @Override
    public Invoker route(Filter filter) {
        return this.invokerFactory.create(this.genericableId, this.isMicro, this.genericableMethod, filter);
    }
}
