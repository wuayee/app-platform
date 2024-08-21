/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.conf.runtime.ClientConfig;
import modelengine.fitframework.conf.runtime.support.DefaultClientSecure;

import java.util.Optional;

/**
 * 表示配置项 {@code 'client.http'} 下的配置。
 *
 * @author 杭潇
 * @since 2024-03-18
 */
@Component
@AcceptConfigValues("client.http")
public class DefaultClientConfig implements ClientConfig {
    /**
     * 配置项：{@code 'secure'}。
     */
    private DefaultClientSecure secure;

    @Override
    public Optional<Secure> secure() {
        return Optional.ofNullable(this.secure);
    }

    /**
     * 设置安全相关的信息。
     *
     * @param secure 表示安全相关的信息的 {@link DefaultClientSecure}。
     */
    public void setSecure(DefaultClientSecure secure) {
        this.secure = secure;
    }
}
