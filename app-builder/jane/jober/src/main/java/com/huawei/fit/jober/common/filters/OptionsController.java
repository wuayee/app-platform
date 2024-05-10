/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.filters;

import com.huawei.fit.http.annotation.DocumentIgnored;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.jober.common.model.JoberResponse;
import com.huawei.fitframework.annotation.Component;

/**
 * Options请求通过，允许跨域的临时规避Controller
 *
 * @author 陈镕希 c00572808
 * @since 2023-06-30
 */
@Component
@RequestMapping
@DocumentIgnored
public class OptionsController {
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*")
    public JoberResponse<Void> options1() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*")
    public JoberResponse<Void> options2() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*")
    public JoberResponse<Void> options3() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*")
    public JoberResponse<Void> options4() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*")
    public JoberResponse<Void> options5() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*")
    public JoberResponse<Void> options6() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*")
    public JoberResponse<Void> options7() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options8() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options9() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options10() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options11() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options12() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options13() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options14() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options15() {
        return JoberResponse.success(null);
    }

    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*")
    public JoberResponse<Void> options16() {
        return JoberResponse.success(null);
    }
}
