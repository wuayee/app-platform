/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.external;

import com.huawei.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 用于测试的 FIT 泛服务接口。
 *
 * @author 季聿阶
 * @since 2023-10-23
 */
public interface FitGenericable {
    /**
     * 表示测试的方法 1。
     *
     * @param p1 表示第一个测试参数的 {@link String}。
     * @param p2 表示第二个测试参数的 {@link Integer}。
     * @param p3 表示第三个测试参数的 {@link Long}。
     * @param p4 表示第四个测试参数的 {@link Double}。
     * @return 表示测试返回值的 {@link String}。
     */
    @Genericable(id = "m1")
    String m1(String p1, Integer p2, Long p3, Double p4);

    /**
     * 表示测试的方法 2。
     */
    @Genericable(id = "m2")
    void m2();

    /**
     * 表示测试的方法 3。
     *
     * @param p1 表示第一个测试参数的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param p2 表示第二个测试参数的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示测试返回值的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Genericable(id = "m3")
    Map<String, Object> m3(Map<String, Object> p1, List<String> p2);
}
