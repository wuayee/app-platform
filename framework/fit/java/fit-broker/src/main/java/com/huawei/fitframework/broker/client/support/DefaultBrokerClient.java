/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.support;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.broker.client.RouterFactory;
import com.huawei.fitframework.broker.client.RouterRetrievalFailureException;
import com.huawei.fitframework.broker.util.AnnotationUtils;
import com.huawei.fitframework.util.GenericableUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 为 {@link BrokerClient} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2020-08-19
 */
public class DefaultBrokerClient implements BrokerClient {
    private final RouterFactory routerFactory;

    public DefaultBrokerClient(RouterFactory routerFactory) {
        this.routerFactory = notNull(routerFactory, "The router factory cannot be null.");
    }

    @Override
    public Router getRouter(Class<?> genericableClass) {
        notNull(genericableClass, () -> new RouterRetrievalFailureException("The genericable class cannot be null."));
        isTrue(genericableClass.isInterface(),
                () -> new RouterRetrievalFailureException(StringUtils.format(
                        "The genericable class is not a interface. [class={0}]",
                        genericableClass.getName())));
        String genericableId = AnnotationUtils.getGenericableId(genericableClass)
                .orElseThrow(() -> new RouterRetrievalFailureException(StringUtils.format(
                        "No genericable id declared in class. [class={0}]",
                        genericableClass.getName())));
        try {
            Method genericableMethod = GenericableUtils.getMacroGenericableMethod(genericableClass)
                    .orElseThrow(() -> new RouterRetrievalFailureException(StringUtils.format(
                            "No genericable method declared in class. [class={0}]",
                            genericableClass.getName())));
            return this.getRouter(genericableId, false, genericableMethod);
        } catch (IllegalStateException e) {
            throw new RouterRetrievalFailureException(e.getMessage(), e);
        }
    }

    @Override
    public Router getRouter(Class<?> genericableClass, String genericableId) {
        notNull(genericableClass, () -> new RouterRetrievalFailureException("The genericable class cannot be null."));
        isTrue(genericableClass.isInterface(),
                () -> new RouterRetrievalFailureException(StringUtils.format(
                        "The genericable class is not a interface. [class={0}]",
                        genericableClass.getName())));
        notBlank(genericableId, () -> new RouterRetrievalFailureException("The genericable id cannot be blank."));
        for (Method method : genericableClass.getDeclaredMethods()) {
            boolean isPresent = AnnotationUtils.getGenericableId(method)
                    .filter(methodGenericableId -> Objects.equals(methodGenericableId, genericableId))
                    .isPresent();
            if (isPresent) {
                return this.getRouter(genericableId, false, method);
            }
        }
        throw new RouterRetrievalFailureException(StringUtils.format(
                "No specified genericable id declared in method. [class={0}, genericableId={1}]",
                genericableClass.getName(),
                genericableId));
    }

    @Override
    public Router getRouter(String genericableId, boolean isMicro, Method genericableMethod) {
        return this.routerFactory.create(genericableId, isMicro, genericableMethod);
    }

    @Override
    public Genericable getGenericable(Class<?> genericableClass) {
        return this.getRouter(genericableClass).route().getGenericable();
    }

    @Override
    public Genericable getGenericable(Class<?> genericableClass, String genericableId) {
        return this.getRouter(genericableClass, genericableId).route().getGenericable();
    }
}
