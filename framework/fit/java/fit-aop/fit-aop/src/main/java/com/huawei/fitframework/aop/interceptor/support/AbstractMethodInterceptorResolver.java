/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.support;

import com.huawei.fitframework.aop.interceptor.MethodInterceptor;
import com.huawei.fitframework.aop.interceptor.MethodInterceptorResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link MethodInterceptorResolver} 的抽象实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-13
 */
public abstract class AbstractMethodInterceptorResolver implements MethodInterceptorResolver {
    private final Map<Class<?>, List<MethodInterceptor>> classMethodInterceptors = new HashMap<>();

    /**
     * 将候选方法拦截器逐一对指定 Bean 进行匹配，返回匹配成功的方法拦截器列表。
     *
     * @param methodInterceptors 表示候选方法拦截器列表的 {@link List}{@code <}{@link MethodInterceptor}{@code >}。
     * @param bean 表示指定 Bean 的 {@link Object}。
     * @return 表示匹配成功的方法拦截器列表的 {@link List}{@code <}{@link MethodInterceptor}{@code >}。
     */
    protected List<MethodInterceptor> matchPointcuts(List<MethodInterceptor> methodInterceptors, Object bean) {
        if (this.classMethodInterceptors.containsKey(bean.getClass())) {
            return this.classMethodInterceptors.get(bean.getClass());
        }
        List<MethodInterceptor> beanMethodInterceptors =
                this.matchPointcutsByClass(methodInterceptors, bean.getClass());
        this.classMethodInterceptors.put(bean.getClass(), beanMethodInterceptors);
        return beanMethodInterceptors;
    }

    private List<MethodInterceptor> matchPointcutsByClass(List<MethodInterceptor> methodInterceptors,
            Class<?> beanClass) {
        List<MethodInterceptor> beanMethodInterceptors = new ArrayList<>();
        for (MethodInterceptor methodInterceptor : methodInterceptors) {
            boolean isMatched = methodInterceptor.getPointCut().add(beanClass);
            if (isMatched) {
                beanMethodInterceptors.add(methodInterceptor);
            }
        }
        return beanMethodInterceptors;
    }
}
