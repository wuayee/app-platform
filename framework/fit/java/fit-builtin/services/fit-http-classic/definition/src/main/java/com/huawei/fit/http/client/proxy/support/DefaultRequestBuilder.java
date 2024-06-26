/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy.support;

import com.huawei.fit.http.Cookie;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.proxy.RequestBuilder;
import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.MultiValueMap;
import com.huawei.fitframework.model.support.DefaultMultiValueMap;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示 Http 请求提供建造者的默认实现。
 *
 * @author 王攀博 w00561424
 * @since 2024-06-11
 */
public class DefaultRequestBuilder implements RequestBuilder {
    private static final Logger log = Logger.get(DefaultRequestBuilder.class);

    private final Map<String, String> pathVariables = new HashMap<String, String>();
    private final MultiValueMap<String, String> queries = new DefaultMultiValueMap<String, String>();
    private final MultiValueMap<String, String> headers = new DefaultMultiValueMap<String, String>();
    private final Map<String, String> cookies = new HashMap<String, String>();
    private final MultiValueMap<String, String> formEntity = new DefaultMultiValueMap<>();

    private Object jsonObject;
    private HttpClassicClient httpClassicClient;
    private HttpRequestMethod method;
    private String protocol;
    private String domain;
    private String pathPattern;
    private Entity entity;

    @Override
    public RequestBuilder method(HttpRequestMethod method) {
        this.method = method;
        return this;
    }

    @Override
    public RequestBuilder client(HttpClassicClient httpClassicClient) {
        this.httpClassicClient = httpClassicClient;
        return this;
    }

    @Override
    public RequestBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public RequestBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    @Override
    public RequestBuilder pathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
        return this;
    }

    @Override
    public RequestBuilder pathVariable(String key, String pathVariable) {
        if (StringUtils.isNotBlank(key)) {
            this.pathVariables.put(key, pathVariable);
        }
        return this;
    }

    @Override
    public RequestBuilder query(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            this.queries.add(key, value);
        }
        return this;
    }

    @Override
    public RequestBuilder header(String name, String header) {
        if (StringUtils.isNotBlank(name)) {
            this.headers.add(name, header);
        }
        return this;
    }

    @Override
    public RequestBuilder cookie(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            this.cookies.put(key, value);
        }
        return this;
    }

    @Override
    public RequestBuilder entity(Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public RequestBuilder formEntity(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            this.formEntity.add(key, value);
        }
        return this;
    }

    @Override
    public RequestBuilder jsonEntity(String propertyValuePath, Object value) {
        if (StringUtils.isNotEmpty(propertyValuePath)) {
            if (this.jsonObject == null) {
                this.jsonObject = new HashMap<String, Object>();
            } else {
                if (!(this.jsonObject instanceof Map)) {
                    log.warn("Value type is error, can not put kv pair.");
                    return this;
                }
            }
            Map<String, Object> temp = ObjectUtils.cast(this.jsonObject);
            temp.put(propertyValuePath, value);
        } else {
            if (jsonObject != null) {
                log.warn("Can not put more data.");
                return this;
            } else {
                this.jsonObject = value;
            }
        }
        return this;
    }

    @Override
    public HttpClassicClientRequest build() {
        StringBuilder url = new StringBuilder(this.protocol);
        url.append("://").append(this.domain);
        if (!this.pathVariables.isEmpty()) {
            this.pathVariables.forEach((key, value) -> {
                this.pathPattern = this.pathPattern.replace("{" + key + "}", value);
            });
        }
        url.append(this.pathPattern);

        if (!this.queries.isEmpty()) {
            url.append("?");
            this.queries.forEach((key, values) -> {
                values.forEach(value -> url.append(key).append("=").append(value).append("&"));
            });
            url.setLength(url.length() - 1);
        }

        HttpClassicClientRequest request = this.httpClassicClient.createRequest(this.method, url.toString());
        if (!this.headers.isEmpty()) {
            this.headers.entrySet().forEach(header -> request.headers().set(header.getKey(), header.getValue()));
        }

        this.cookies.forEach((name, value) -> request.cookies().add(Cookie.builder().name(name).value(value).build()));

        if (this.entity != null) {
            request.entity(this.entity);
        } else if (this.jsonObject != null) {
            request.jsonEntity(jsonObject);
        } else if (!this.formEntity.isEmpty()) {
            request.formEntity(formEntity);
        }

        return request;
    }
}
