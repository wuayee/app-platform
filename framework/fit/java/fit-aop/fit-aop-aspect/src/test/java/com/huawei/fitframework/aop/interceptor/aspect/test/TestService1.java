/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.test;

import java.util.List;
import java.util.Map;

/**
 * 测试服务类。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-14
 */
@TestAnnotationNest
public class TestService1 implements TestService1Interface {
    @Override
    public String m1() {
        return "m1";
    }

    /**
     * 测试方法2。
     *
     * @param s1 表示参数的 {@link String}。
     * @return 表示测试返回值的 {@link String}。
     */
    public String m2(String s1) {
        return "m2: " + s1;
    }

    /**
     * 带注解的方法。
     *
     * @return 表示测试返回值的 {@link String}。
     */
    @TestAnnotation
    public String m3() {
        return "m3";
    }

    /**
     * 带注解的方法。
     *
     * @param param 表示测试参数的 {@link TestParam}。
     * @return 表示测试返回值的 {@link String}。
     */
    public String m4(TestParam param) {
        return "m4:" + param;
    }

    /**
     * 多个复杂参数的方法。
     *
     * @param param 表示测试参数的 {@link TestParam}。
     * @param list 表示测试参数的 {@link List}{@code <}{@link String}{@code >}。
     * @param array 表示测试参数的 {@code int[]}。
     * @param map 表示测试参数的 {@link Map}{@code <}{@link Integer}{@code , }{@link String}{@code >}。
     * @return 表示测试返回值的 {@link TestParam}。
     */
    public TestParam m5(TestParam param, List<String> list, int[] array, Map<Integer, String> map) {
        return new TestParam();
    }

    /**
     * 参数有注解的方法。
     *
     * @param param 表示测试参数的 {@link TestParam}。
     * @return 表示测试返回值的 {@link TestParam}。
     */
    public TestParam m6(@TestAnnotationNest TestParam param) {
        return new TestParam();
    }
}
