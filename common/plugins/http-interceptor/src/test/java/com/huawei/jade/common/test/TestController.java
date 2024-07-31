/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.test;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.exception.FitException;
import com.huawei.jade.common.code.CommonRetCode;
import com.huawei.jade.common.filter.HttpResult;

/**
 * 表示测试用控制器。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Component
public class TestController {
    /**
     * {@link com.huawei.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenInterceptData}。
     *
     * @return 表示测试用字符串的 {@link String}。
     */
    @GetMapping("/support/string")
    public String test0() {
        return "test";
    }

    /**
     * {@link com.huawei.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenInterceptVoid}。
     */
    @GetMapping("/support/void")
    public void test1() {}

    /**
     * {@link com.huawei.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenInterceptData}。
     *
     * @return 表示测试结果的 {@link HttpResult}{@code <}{@link String}{@code >}。
     */
    @GetMapping("/support/result")
    public HttpResult<String> test2() {
        return HttpResult.ok("test");
    }

    /**
     * {@link com.huawei.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenInterceptInteger}。
     *
     * @return 表示测试结果的 {@code int}。
     */
    @GetMapping("/support/int")
    public int test3() {
        return 0;
    }

    /**
     * {@link com.huawei.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenNoInterceptString}。
     *
     * @return 表示测试结果的 {@link String}。
     */
    @GetMapping("/nonsupport/string")
    public String test5() {
        return "test";
    }

    /**
     * {@link com.huawei.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenNoInterceptResult}。
     *
     * @return 表示测试结果的 {@link HttpResult}{@code <}{@link Void}{@code >}。
     */
    @GetMapping("/nonsupport/result")
    public HttpResult<Void> test6() {
        return HttpResult.error(CommonRetCode.INTERNAL_ERROR);
    }

    /**
     * {@link com.huawei.jade.common.filter.support.DefaultHttpExceptionAdviceTest#shouldOkWhenInterceptException}。
     *
     * @return 表示测试结果的 {@link String}。
     */
    @GetMapping("/nonsupport/exception")
    public String test7() {
        throw new FitException(404, "test error");
    }
}