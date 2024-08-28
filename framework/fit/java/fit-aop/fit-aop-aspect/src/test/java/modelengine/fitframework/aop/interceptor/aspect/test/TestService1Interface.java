/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.aop.interceptor.aspect.test;

/**
 * 测试服务1的接口。
 *
 * @author 季聿阶
 * @since 2022-05-25
 */
public interface TestService1Interface extends AnotherTestService1Interface {
    /**
     * 测试方法1。
     *
     * @return 表示测试返回值的 {@link String}。
     */
    String m1();
}
