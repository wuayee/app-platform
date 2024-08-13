/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy.emitter;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.client.HttpClientException;
import com.huawei.fit.http.client.proxy.HttpEmitter;
import com.huawei.fit.http.client.proxy.PropertyValueApplier;
import com.huawei.fit.http.client.proxy.RequestBuilder;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * 表示 Http 客户端的代理执行者的默认实现。
 *
 * @author 王攀博
 * @since 2024-06-14
 */

public class DefaultHttpEmitter implements HttpEmitter {
    private final List<PropertyValueApplier> appliers;
    private final HttpClassicClient client;
    private final HttpRequestMethod method;
    private final String protocol;
    private final String domain;
    private final String pathPattern;

    /**
     * 表示提供 Http 客户端的代理执行者的默认实现的构造。
     *
     * @param appliers 表示和每一个入参对应的属性填充器的 {@link PropertyValueApplier}。
     * @param client 表示 Http 客户端的 {@link HttpClassicClient}。
     * @param method 表示 Http 请求的方法的 {@link HttpRequestMethod}。
     * @param protocol 表示 Http 请求的协议的 {@link String}。
     * @param domain 表示 Http 请求域名的 {@link String}。
     * @param pathPattern 表示 Http 请求的路径模板的 {@link String}。
     */
    public DefaultHttpEmitter(List<PropertyValueApplier> appliers, HttpClassicClient client, HttpRequestMethod method,
            String protocol, String domain, String pathPattern) {
        this.appliers = notNull(appliers, "The emitter cannot be null.");
        this.client = notNull(client, "The client cannot be null.");
        this.method = notNull(method, "The method cannot be null.");
        this.protocol = notBlank(protocol, "The protocol cannot be null.");
        this.domain = notBlank(domain, "The domain cannot be null.");
        this.pathPattern = notBlank(pathPattern, "The path pattern cannot be null.");
    }

    @Override
    public <T> HttpClassicClientResponse<T> emit(Object[] args) throws HttpClientException {
        Validation.equals(args.length,
                this.appliers.size(),
                () -> new HttpClientException(StringUtils.format(
                        "The args length not equals to the size of appliers. [args={0}, appliers={1}]",
                        args.length,
                        this.appliers.size())));

        RequestBuilder requestBuilder = RequestBuilder.create()
                .client(this.client)
                .method(this.method)
                .protocol(this.protocol)
                .domain(this.domain)
                .pathPattern(this.pathPattern);

        for (int i = 0; i < args.length; ++i) {
            this.appliers.get(i).apply(requestBuilder, args[i]);
        }
        try (HttpClassicClientRequest request = requestBuilder.build()) {
            return this.client.exchange(request, Object.class);
        } catch (IOException e) {
            throw new HttpClientException("Client request error.", e);
        }
    }
}
