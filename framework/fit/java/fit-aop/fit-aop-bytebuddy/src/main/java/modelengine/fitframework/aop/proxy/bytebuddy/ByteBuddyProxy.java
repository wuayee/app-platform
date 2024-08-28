/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.aop.proxy.bytebuddy;

import modelengine.fitframework.aop.proxy.InterceptSupport;
import modelengine.fitframework.aop.proxy.support.AbstractAopProxy;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * ByteBuddy 的代理方法回调。
 *
 * @author 詹高扬
 * @author 季聿阶
 * @since 2022-08-02
 */
public class ByteBuddyProxy extends AbstractAopProxy implements InvocationHandler {
    /**
     * 使用拦截支持信息实例化 {@link AbstractAopProxy}。
     *
     * @param support 表示拦截支持信息的 {@link InterceptSupport}。
     */
    protected ByteBuddyProxy(InterceptSupport support) {
        super(support);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.invoke(proxy, method, args, methodInvocation -> {
            try {
                return ReflectionUtils.invoke(methodInvocation.getTarget(), method, methodInvocation.getArguments());
            } catch (MethodInvocationException e) {
                throw e.getCause();
            }
        });
    }
}
