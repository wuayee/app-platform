/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.methodmatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.aop.interceptor.MethodInvocation;
import com.huawei.fitframework.aop.interceptor.MethodMatcher;
import com.huawei.fitframework.aop.interceptor.aspect.interceptor.AspectMethodMatcher;
import com.huawei.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import com.huawei.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import com.huawei.fitframework.aop.interceptor.aspect.parser.support.ThisParser;
import com.huawei.fitframework.aop.interceptor.aspect.test.TestExecutionAspect;
import com.huawei.fitframework.aop.interceptor.aspect.test.TestService1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link ThisParser} 单元测试。
 *
 * @author 白鹏坤
 * @since 2023-03-29
 */
@DisplayName("测试 this 表达式")
public class ThisMatcherTest {
    private Method service1M2Method;

    @BeforeEach
    void setup() throws NoSuchMethodException {
        this.service1M2Method = TestService1.class.getDeclaredMethod("m2", String.class);
    }

    @AfterEach
    void teardown() {
        this.service1M2Method = null;
    }

    @Test
    @DisplayName("当表达式包含 this 数据绑定时，代理对象匹配成功")
    void givenExpressionContainsThisBindingThenProxyMatches() {
        String exp = "this(proxy)";
        PointcutParameter[] parameters = new PointcutParameter[1];
        parameters[0] = new DefaultPointcutParameter("proxy", TestService1.class);
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M2Method);
        assertThat(isMatch.matches()).isTrue();
        methodMatcher.choose(this.service1M2Method, isMatch);
        MethodInvocation proxy = mock(MethodInvocation.class);
        Object proxyTest = new TestService1();
        when(proxy.getTarget()).thenReturn(proxyTest);
        MethodInvocation proxied = mock(MethodInvocation.class);
        PointcutParameter[] parametersBindings =
                methodMatcher.matchJoinPoint(this.service1M2Method, new Object[] {"Hello"}, proxy, proxied);
        assertThat(parametersBindings).hasSize(1);
        assertThat(parametersBindings[0].getBinding()).isEqualTo(proxyTest);
    }

    @Test
    @DisplayName("当表达式包含 this 范围匹配时，代理对象匹配成功")
    void givenExpressionContainsThisThenProxyMatches() {
        String exp = "this(com.huawei.fitframework.aop.interceptor.aspect.test.TestService1)";
        PointcutParameter[] parameters = new PointcutParameter[1];
        AspectMethodMatcher methodMatcher = new AspectMethodMatcher(exp, TestExecutionAspect.class, parameters);
        boolean isCouldMatch = methodMatcher.couldMatch(TestService1.class);
        assertThat(isCouldMatch).isTrue();
        MethodMatcher.MatchResult isMatch = methodMatcher.match(this.service1M2Method);
        assertThat(isMatch.matches()).isTrue();
    }
}
