/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.test;

/**
 * 测试服务类。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-14
 */
public class TestService3 extends TestService1 {
    @Override
    public String m1() {
        return "m1";
    }

    public String selfMethod() {
        return "selfMethod";
    }
}
