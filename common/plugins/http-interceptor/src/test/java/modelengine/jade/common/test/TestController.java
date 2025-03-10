/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.test;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.validation.Validated;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.filter.support.DefaultHttpExceptionAdviceTest;
import modelengine.jade.common.vo.Result;

import javax.validation.constraints.NotBlank;

/**
 * 表示测试用控制器。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Component
@Validated
public class TestController {
    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenInterceptData}。
     *
     * @return 表示测试用字符串的 {@link String}。
     */
    @GetMapping("/support/string")
    public String test0() {
        return "test";
    }

    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenInterceptVoid}。
     */
    @GetMapping("/support/void")
    public void test1() {}

    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenInterceptData}。
     *
     * @return 表示测试结果的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @GetMapping("/support/result")
    public Result<String> test2() {
        return Result.ok("test");
    }

    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenInterceptInteger}。
     *
     * @return 表示测试结果的 {@code int}。
     */
    @GetMapping("/support/int")
    public int test3() {
        return 0;
    }

    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenNoInterceptString}。
     *
     * @return 表示测试结果的 {@link String}。
     */
    @GetMapping("/nonsupport/string")
    public String test5() {
        return "test";
    }

    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenNoInterceptResult}。
     *
     * @return 表示测试结果的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    @GetMapping("/nonsupport/result")
    public Result<Void> test6() {
        return Result.error(CommonRetCode.INTERNAL_ERROR);
    }

    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpExceptionAdviceTest#shouldOkWhenInterceptException}。
     *
     * @return 表示测试结果的 {@link String}。
     */
    @GetMapping("/nonsupport/exception")
    public String test7() {
        throw new FitException(404, "test error");
    }

    /**
     * {@link modelengine.jade.common.filter.support.LoginFilterTest#shouldOkWhenGetUserContext()}。
     *
     * @return 表示测试结果的 {@link String}
     */
    @GetMapping("/support/testLoginFilter")
    public String test8() {
        return UserContextHolder.get().getName();
    }

    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldOkWhenNoInterceptStream}。
     *
     * @return 表示测试结果的 {@link Choir}{@code <}{@link Integer}{@code >}。
     */
    @GetMapping("/support/stream")
    public Choir<Integer> test9() {
        return Choir.just(1, 2, 3);
    }

    /**
     * {@link DefaultHttpExceptionAdviceTest#shouldOkWhenConstraintViolationException()}。
     *
     * @param car 表示车的 {@link Car}。
     */
    @PostMapping("/violation/car")
    public void test10(@RequestBody @Validated Car car) {}

    /**
     * {@link DefaultHttpExceptionAdviceTest#shouldOkWhenConstraintViolationException()}。
     *
     * @param name 表示名字的 {@link String}。
     */
    @PostMapping("/hibernate/blank")
    public void test11(@RequestParam("name") @NotBlank(message = "{blank}") String name) {}

    /**
     * {@link modelengine.jade.common.filter.support.DefaultHttpResponseWrapperTest#shouldExWhenStreamException}。
     *
     * @return 表示测试结果的 {@link Choir}{@code <}{@link Integer}{@code >}。
     */
    @GetMapping("/support/stream/ex")
    public Choir<Integer> test12() {
        return Choir.just(1, 2, 3).map(i -> {
            if (i == 2) {
                throw new IllegalStateException("Test Mock Exception");
            }
            return i;
        });
    }
}