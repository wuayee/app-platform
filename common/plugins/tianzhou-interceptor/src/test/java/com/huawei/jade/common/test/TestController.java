/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.test;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fitframework.annotation.Component;
import com.huawei.jade.common.code.CommonRetCode;
import com.huawei.jade.common.filter.HttpResult;

/**
 * 表示测试用控制器。
 *
 * @author 吴宇伦
 * @since 2024-08-01
 */
@Component
public class TestController {
    /**
     * {@link com.huawei.jade.common.filter.support.DefaultAlbSseFilterTest#shouldOkWhenInterceptData}。
     *
     * @return 表示测试结果的 {@link HttpResult}{@code <}{@link String}{@code >}。
     */
    @GetMapping("/support/result")
    public HttpResult<String> test0() {
        return HttpResult.ok("test");
    }

    /**
     * {@link com.huawei.jade.common.filter.support.DefaultAlbSseFilterTest#shouldOkWhenNoInterceptString}。
     *
     * @return 表示测试结果的 {@link String}。
     */
    @GetMapping("/nonsupport/string")
    public String test1() {
        return "test";
    }

    /**
     * {@link com.huawei.jade.common.filter.support.DefaultAlbSseFilterTest#shouldOkWhenNoInterceptResult}。
     *
     * @return 表示测试结果的 {@link HttpResult}{@code <}{@link Void}{@code >}。
     */
    @GetMapping("/nonsupport/result")
    public HttpResult<Void> test2() {
        return HttpResult.error(CommonRetCode.INTERNAL_ERROR);
    }
}