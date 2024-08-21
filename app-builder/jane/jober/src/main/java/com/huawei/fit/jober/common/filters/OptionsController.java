/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.filters;

import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.jober.common.model.JoberResponse;
import modelengine.fitframework.annotation.Component;

/**
 * Options请求通过，允许跨域的临时规避Controller
 *
 * @author 陈镕希
 * @since 2023-06-30
 */
@Component
@RequestMapping
@DocumentIgnored
public class OptionsController {
    /**
     * 获取Options请求1
     *
     * @return Options请求1
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*")
    public JoberResponse<Void> options1() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求2
     *
     * @return Options请求2
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*")
    public JoberResponse<Void> options2() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求3
     *
     * @return Options请求3
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*")
    public JoberResponse<Void> options3() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求4
     *
     * @return Options请求4
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*")
    public JoberResponse<Void> options4() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求5
     *
     * @return Options请求5
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*")
    public JoberResponse<Void> options5() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求6
     *
     * @return Options请求6
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*")
    public JoberResponse<Void> options6() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求7
     *
     * @return Options请求7
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*")
    public JoberResponse<Void> options7() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求8
     *
     * @return Options请求8
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options8() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求9
     *
     * @return Options请求9
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options9() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求10
     *
     * @return Options请求10
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options10() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求11
     *
     * @return Options请求11
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options11() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求12
     *
     * @return Options请求12
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options12() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求13
     *
     * @return Options请求13
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options13() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求14
     *
     * @return Options请求14
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options14() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求15
     *
     * @return Options请求15
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options15() {
        return JoberResponse.success(null);
    }

    /**
     * 获取Options请求16
     *
     * @return Options请求16
     */
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options16() {
        return JoberResponse.success(null);
    }
}
