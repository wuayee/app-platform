/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.support;

import com.huawei.fitframework.aop.interceptor.AdviceMethodInterceptor;
import com.huawei.fitframework.aop.interceptor.MethodJoinPoint;
import com.huawei.fitframework.aop.interceptor.MethodMatcher;
import com.huawei.fitframework.exception.MethodInvocationException;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 带建议的方法拦截器的通用抽象实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-11
 */
public abstract class AbstractAdviceMethodInterceptor extends AbstractMethodInterceptor
        implements AdviceMethodInterceptor {
    /** 表示空的参数列表的 {@link Object}{@code []}。 */
    public static final Object[] EMPTY_ARGS = new Object[0];

    private final BeanFactory aspectFactory;
    private final Method advisorMethod;

    /**
     * 使用拦截建议的对象和拦截建议的方法来实例化一个 {@link AbstractAdviceMethodInterceptor}。
     *
     * @param aspectFactory 表示拦截建议的对象的工厂的 {@link BeanFactory}。
     * @param advisorMethod 表示拦截建议的方法的 {@link Method}。
     */
    protected AbstractAdviceMethodInterceptor(BeanFactory aspectFactory, Method advisorMethod) {
        this(aspectFactory, advisorMethod, null);
    }

    /**
     * 使用拦截建议的对象、拦截建议的方法和一系列方法拦截器来实例化一个 {@link AbstractAdviceMethodInterceptor}。
     *
     * @param aspectFactory 表示拦截建议的对象的工厂的 {@link BeanFactory}。
     * @param advisorMethod 表示拦截建议的方法的 {@link Method}。
     * @param methodMatchers 表示一系列方法拦截器的 {@link List}{@code <}{@link MethodMatcher}{@code >}。
     */
    protected AbstractAdviceMethodInterceptor(BeanFactory aspectFactory, Method advisorMethod,
            List<MethodMatcher> methodMatchers) {
        super(methodMatchers);
        this.aspectFactory = aspectFactory;
        this.advisorMethod = Validation.notNull(advisorMethod, "The intercept method cannot be null.");
    }

    @Nullable
    @Override
    public Object getAdvisorTarget() {
        return this.aspectFactory.get();
    }

    @Nonnull
    @Override
    public Method getAdvisorMethod() {
        return this.advisorMethod;
    }

    /**
     * 调用拦截的建议点。
     * <p>通过反射的方式调用用户自定义的拦截逻辑。</p>
     *
     * @param joinPoint 表示运行时的方法连接点信息的 {@link MethodJoinPoint}。
     * @param returnValue 表示运行后的返回值的 {@link Object}。
     * @param throwable 表示运行时抛出的异常的 {@link Throwable}。
     * @return 表示调用拦截点后的返回值的 {@link Object}。
     * @throws Throwable 当调用方法发生异常时抛出，抛出的异常类型为原方法所定义的类型。
     */
    protected Object invokeAdvisorPoint(MethodJoinPoint joinPoint, @Nullable Object returnValue,
            @Nullable Throwable throwable) throws Throwable {
        Object[] advisorArgs = this.getAdvisorArgs(joinPoint, returnValue, throwable);
        try {
            return ReflectionUtils.invoke(this.getAdvisorTarget(), this.advisorMethod, advisorArgs);
        } catch (MethodInvocationException e) {
            throw e.getCause();
        }
    }

    /**
     * 获取调用拦截点的参数。
     *
     * @param joinPoint 表示运行时的方法连接点信息的 {@link MethodJoinPoint}。
     * @param returnValue 表示运行后的返回值的 {@link Object}。
     * @param throwable 表示运行时抛出的异常的 {@link Throwable}。
     * @return 表示调用拦截点参数的 {@link Object}{@code []}。
     */
    protected Object[] getAdvisorArgs(MethodJoinPoint joinPoint, @Nullable Object returnValue,
            @Nullable Throwable throwable) {
        return EMPTY_ARGS;
    }
}
