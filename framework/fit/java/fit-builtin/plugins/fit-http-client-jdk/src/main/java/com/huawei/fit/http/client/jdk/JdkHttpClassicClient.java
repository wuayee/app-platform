/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.client.jdk;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.http.Serializers;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.client.support.AbstractHttpClassicClient;
import com.huawei.fit.http.client.support.DefaultHttpClassicClientRequest;
import com.huawei.fit.http.protocol.ClientRequest;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.value.ValueFetcher;

import java.lang.reflect.Type;

/**
 * 表示 {@link HttpClassicClient} 的 JDK 实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-23
 */
public class JdkHttpClassicClient extends AbstractHttpClassicClient {
    private final HttpClassicClientFactory.Config config;

    /**
     * 创建 {@link HttpClassicClient} 的 JDK 实现对象。
     *
     * @param serializers 表示序列化器集合的 {@link Serializers}。
     * @param valueFetcher 表示值的获取工具的 {@link ValueFetcher}。
     * @param config 表示配置的 {@link com.huawei.fit.http.client.HttpClassicClientFactory.Config}。
     */
    public JdkHttpClassicClient(Serializers serializers, ValueFetcher valueFetcher,
            HttpClassicClientFactory.Config config) {
        super(serializers, valueFetcher);
        this.config = getIfNull(config, () -> HttpClassicClientFactory.Config.builder().build());
        System.setProperty("http.keepAlive", "false");
    }

    @Override
    public HttpClassicClientRequest createRequest(HttpRequestMethod method, String url) {
        ClientRequest clientRequest = new UrlConnectionClientRequest(method, url, this.config);
        return new DefaultHttpClassicClientRequest(this, clientRequest, this.config);
    }

    @Override
    public HttpClassicClientResponse<Object> exchange(HttpClassicClientRequest request) {
        return this.exchange(request, Object.class);
    }

    @Override
    public <T> HttpClassicClientResponse<T> exchange(HttpClassicClientRequest request, Type responseType) {
        notNull(request, "The http classic request to exchange cannot be null.");
        return request.exchange(responseType);
    }
}
