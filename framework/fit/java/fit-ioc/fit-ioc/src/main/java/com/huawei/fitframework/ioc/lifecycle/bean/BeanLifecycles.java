/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.lifecycle.bean.support.InterceptedBeanLifecycle;
import com.huawei.fitframework.type.TypeMatcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 Bean 声明周期相关的工具类集合。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-01
 */
public class BeanLifecycles {
    /**
     * 返回应用了拦截程序的生命周期。
     *
     * @param lifecycle 表示待拦截的生命周期的 {@link BeanLifecycle}。
     * @return 表示应用拦截程序后的生命周期的 {@link BeanLifecycle}。
     */
    public static BeanLifecycle intercept(BeanLifecycle lifecycle) {
        notNull(lifecycle, "The lifecycle cannot be null.");
        if (lifecycle instanceof InterceptedBeanLifecycle) {
            return lifecycle;
        } else if (TypeMatcher.match(lifecycle.metadata().type(), BeanLifecycleDependency.class)) {
            return lifecycle;
        } else {
            BeanContainer container = lifecycle.metadata().container();
            List<BeanLifecycleInterceptor> interceptors = container.all(BeanLifecycleInterceptor.class)
                    .stream()
                    .map(BeanFactory::<BeanLifecycleInterceptor>get)
                    .collect(Collectors.toList());
            BeanLifecycle intercepted = lifecycle;
            for (BeanLifecycleInterceptor interceptor : interceptors) {
                if (interceptor.isInterceptionRequired(intercepted.metadata())) {
                    intercepted = new InterceptedBeanLifecycle(intercepted, interceptor);
                }
            }
            return intercepted;
        }
    }
}
