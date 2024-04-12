/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.mvc.request;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.model.MultiValueMap;
import com.huawei.fitframework.test.domain.mvc.MockMvc;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为模拟 {@link MockMvc} 中的客户端提供请求的构建者。
 *
 * @author 王攀博 w00561424
 * @since 2024-04-09
 */
public class MockRequestBuilder implements RequestBuilder {
    private static final String BASE_URL = "http://localhost:";

    private final HttpRequestMethod method;
    private final Map<String, String> params = new ConcurrentHashMap<>();
    private final Map<String, String> headers = new ConcurrentHashMap<>();
    private final Map<String, List<String>> headersSet = new ConcurrentHashMap<>();
    private String url;
    private Type responseType;
    private int port;
    private HttpClassicClient httpClassicClient;
    private Entity entity;
    private Object jsonObject;
    private MultiValueMap<String, String> formEntity;

    MockRequestBuilder(HttpRequestMethod method, String url) {
        this.method = method;
        this.url = Validation.notNull(url, "Url for request is null.");
    }

    /**
     * 为请求插件结构体添加键值对参数。
     *
     * @param name 表示请求体内请求参数的键值 {@link String}。
     * @param value 表示请求体内请求参数的值 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder param(String name, String value) {
        this.params.put(name, value);
        return this;
    }

    /**
     * 设置客户端请求结果的类型。
     *
     * @param responseType 表示请求结果的返回值类型 {@link Type}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder responseType(Type responseType) {
        this.responseType = responseType;
        return this;
    }

    /**
     * 设置客户端请求的端口号。
     *
     * @param port 表示客户端请求的端口号 {@link int}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder port(int port) {
        this.port = port;
        return this;
    }

    /**
     * 设置请求结构体的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * @param header 表示待设置的消息头内容的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder header(String name, String header) {
        this.headers.put(name, header);
        return this;
    }

    /**
     * 设置请求结构体的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * @param headers 表示待设置的消息头内容列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder header(String name, List<String> headers) {
        this.headersSet.put(name, headers);
        return this;
    }

    /**
     * 设置请求结构体的消息体内容。
     *
     * @param entity 表示 Http 请求的消息体内容的 {@link Entity}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder entity(Entity entity) {
        this.entity = entity;
        return this;
    }

    /**
     * 设置 Http 请求的 Form 格式的消息体内容。
     *
     * @param formEntity 表示 Http 请求的 Form 格式的消息体内容的 {@link MultiValueMap}{@code <}{@link String}{@code ,
     * }{@link String}{@code >}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder formEntity(MultiValueMap<String, String> formEntity) {
        this.formEntity = formEntity;
        return this;
    }

    /**
     * 设置 Http 请求的 Json 格式的消息体内容。
     *
     * @param jsonObject 表示 Http 请求的 Json 格式的消息体内容的 {@link Object}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder jsonEntity(Object jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    /**
     * 设置 Http 客户端。
     *
     * @param httpClassicClient 表示测试需要的客户端。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder client(HttpClassicClient httpClassicClient) {
        this.httpClassicClient = httpClassicClient;
        return this;
    }

    /**
     * 构建客户端的请求参数。
     *
     * @return 表示返回构建完成的客户端的请求参数 {@link RequestParam}。
     */
    @Override
    public RequestParam build() {
        this.url = BASE_URL + this.port + this.url;
        if (!this.params.isEmpty()) {
            this.url += "?";
        }
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            this.url += entry.getKey() + "=" + entry.getValue();
        }

        HttpClassicClientRequest request = this.httpClassicClient.createRequest(this.method, this.url);
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            request.headers().set(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, List<String>> entry : this.headersSet.entrySet()) {
            request.headers().set(entry.getKey(), entry.getValue());
        }
        if (this.entity != null) {
            request.entity(this.entity);
        } else if (this.jsonObject != null) {
            request.jsonEntity(this.jsonObject);
        } else if (this.formEntity != null) {
            request.formEntity(this.formEntity);
        }
        return new RequestParam(this.method, this.url, this.responseType, request);
    }
}
