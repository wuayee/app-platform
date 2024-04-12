/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.mvc;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.jdk.JdkHttpClassicClientFactory;
import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fit.value.fastjson.FastJsonValueHandler;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.value.ValueFetcher;

import java.util.Map;

/**
 * 为模拟的 Mvc 所使用的 http 客户端提供工厂。
 *
 * @author 王攀博 w00561424
 * @since 2024-04-09
 */
public class HttpClientFactory {
    /**
     * 为模拟的 Mvc 创建客户端。
     *
     * @return 表示用于模拟测试的 {@link HttpClassicClient}。
     */
    public static HttpClassicClient create() {
        ObjectSerializer jsonSerializer = new JacksonObjectSerializer(null, null, null);
        Map<String, ObjectSerializer> serializers =
                MapBuilder.<String, ObjectSerializer>get().put("json", jsonSerializer).build();
        ValueFetcher valueFetcher = new FastJsonValueHandler();
        HttpClassicClientFactory jdkFactory = new JdkHttpClassicClientFactory(serializers, valueFetcher);
        return jdkFactory.create();
    }
}
