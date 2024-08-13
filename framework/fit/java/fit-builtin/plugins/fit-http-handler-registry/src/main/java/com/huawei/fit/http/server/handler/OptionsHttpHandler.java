/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import com.huawei.fit.http.annotation.DocumentIgnored;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.annotation.Component;

/**
 * 表示全局默认的 {@link HttpRequestMethod#OPTIONS} 处理方法。
 *
 * @author 季聿阶
 * @since 2023-07-21
 */
@Component
public class OptionsHttpHandler {
    /**
     * 处理 Option 请求。
     */
    @DocumentIgnored
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/**")
    public void handleOptions() {}
}
