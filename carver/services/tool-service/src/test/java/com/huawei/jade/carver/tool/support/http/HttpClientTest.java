/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support.http;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.client.okhttp.OkHttpClassicClientFactory;
import com.huawei.fit.http.client.proxy.DestinationSetter;
import com.huawei.fit.http.client.proxy.PropertyValueApplier;
import com.huawei.fit.http.client.proxy.RequestBuilder;
import com.huawei.fit.http.client.proxy.emitter.DefaultHttpEmitter;
import com.huawei.fit.http.client.proxy.support.DefaultRequestBuilder;
import com.huawei.fit.http.client.proxy.support.applier.MultiDestinationsPropertyValueApplier;
import com.huawei.fit.http.client.proxy.support.applier.UniqueDestinationPropertyValueApplier;
import com.huawei.fit.http.client.proxy.support.setter.CookieDestinationSetter;
import com.huawei.fit.http.client.proxy.support.setter.DestinationSetterInfo;
import com.huawei.fit.http.client.proxy.support.setter.FormUrlEncodedEntitySetter;
import com.huawei.fit.http.client.proxy.support.setter.HeaderDestinationSetter;
import com.huawei.fit.http.client.proxy.support.setter.ObjectEntitySetter;
import com.huawei.fit.http.client.proxy.support.setter.PathVariableDestinationSetter;
import com.huawei.fit.http.client.proxy.support.setter.QueryDestinationSetter;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fit.value.fastjson.FastJsonValueHandler;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.value.ValueFetcher;
import com.huawei.jade.carver.tool.support.http.server.RuntimeForServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试 Http 提供。
 *
 * @author 王攀博 w00561424
 * @since 2024-06-15
 */
@DisplayName("测试 Http 构建规则")
public class HttpClientTest {
    private RuntimeForServer runtime;
    private RequestBuilder requestBuilder;
    private String protocol;
    private String domain;
    private HttpRequestMethod method;
    private String pathPattern;
    private HttpClassicClient client;
    private String[] keys;
    private Object[] values;
    private Map<String, Object> weather;
    private Map<String, Object> formBody;

    public HttpClientTest() {
        this.runtime = new RuntimeForServer(HttpClientTest.class);
    }

    @BeforeEach
    void setup() {
        this.runtime.start();
        this.protocol = "http";
        this.domain = "localhost:8080";
        this.pathPattern = "/test/travel/{type}";
        this.method = HttpRequestMethod.POST;
        this.client = this.createHttpClient();
        this.requestBuilder = new DefaultRequestBuilder().client(this.client)
                .protocol(this.protocol)
                .domain(this.domain)
                .pathPattern(this.pathPattern)
                .method(this.method);
        this.keys = new String[] {
                "type", "name", "age", "hobby", "phoneNumber", "transportations", "weather"
        };
        this.weather = MapBuilder.<String, Object>get().put("level", 1).put("weather", "sunny").build();
        this.values = new Object[] {
                "tourism", "test_name", 12, "running", "19988666666", Arrays.asList("train", "airplanes"), this.weather
        };
        this.formBody = new HashMap<>();
        this.formBody.put("strings", Arrays.asList("string1", "string2"));
        this.formBody.put("integer", 666);
    }

    @AfterEach
    void teardown() {
        this.runtime.stop();
    }

    @Test
    @DisplayName("测试 Http 客户端构建规则")
    void shouldReturnValueWhenEmitGivenParam() {
        // given
        List<PropertyValueApplier> appliers = this.createAppliers();

        // when
        DefaultHttpEmitter emitter = new DefaultHttpEmitter(appliers,
                this.client,
                this.method,
                this.protocol,
                this.domain,
                this.pathPattern);

        HttpClassicClientResponse<?> response = emitter.emit(this.values);
        Map<String, Object> result = ObjectUtils.cast(response.objectEntity().get().object());

        // then
        for (int i = 0; i < this.keys.length; ++i) {
            assertThat(result.get(this.keys[i])).isEqualTo(this.values[i]);
        }
    }

    @Test
    @DisplayName("测试 Http formBody")
    void shouldReturnFormBodyInfoWhenEmitGivenParam() {
        // given
        List<DestinationSetterInfo> setterInfos = new ArrayList<>();
        this.formBody.entrySet()
                .forEach(element -> setterInfos.add(
                        new DestinationSetterInfo(new FormUrlEncodedEntitySetter(element.getKey()), element.getKey())));

        List<PropertyValueApplier> appliers = new ArrayList<>();
        PropertyValueApplier applier =
                new MultiDestinationsPropertyValueApplier(setterInfos, this.client.valueFetcher());
        appliers.add(applier);

        // when
        DefaultHttpEmitter emitter =
                new DefaultHttpEmitter(appliers, this.client, this.method, this.protocol, this.domain, "/test/form");

        HttpClassicClientResponse<?> response = emitter.emit(new Object[] {this.formBody});
        Map<String, Object> result = ObjectUtils.cast(response.objectEntity().get().object());

        // then
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            assertThat(result.get(entry.getKey())).isEqualTo(this.formBody.get(entry.getKey()));
        }
    }

    private List<PropertyValueApplier> createAppliers() {
        List<DestinationSetter> setters = new ArrayList<>();
        setters.add(new PathVariableDestinationSetter(this.keys[0]));
        setters.add(new HeaderDestinationSetter(this.keys[1]));
        setters.add(new HeaderDestinationSetter(this.keys[2]));
        setters.add(new CookieDestinationSetter(this.keys[3]));
        setters.add(new CookieDestinationSetter(this.keys[4]));
        setters.add(new QueryDestinationSetter(this.keys[5]));
        setters.add(new ObjectEntitySetter("")); // address

        List<PropertyValueApplier> appliers = new ArrayList<>();
        setters.forEach(setter -> appliers.add(new UniqueDestinationPropertyValueApplier(setter,
                this.client.valueFetcher())));
        return appliers;
    }

    private HttpClassicClient createHttpClient() {
        ObjectSerializer jsonSerializer = new JacksonObjectSerializer(null, null, null);
        Map<String, ObjectSerializer> serializers =
                MapBuilder.<String, ObjectSerializer>get().put("json", jsonSerializer).build();
        ValueFetcher valueFetcher = new FastJsonValueHandler();
        HttpClassicClientFactory httpClassicClientFactory = new OkHttpClassicClientFactory(serializers, valueFetcher);
        return httpClassicClientFactory.create();
    }
}