/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.okhttp;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.Serializers;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.value.ValueFetcher;

import java.util.Map;

/**
 * 表示 {@link HttpClassicClientFactory} 的 OkHttp 实现。
 *
 * @author 杭潇
 * @since 2024-04-08
 */
@Order(Order.HIGH)
@Component
public class OkHttpClassicClientFactory implements HttpClassicClientFactory {
    private final Serializers serializers;
    private final ValueFetcher valueFetcher;

    public OkHttpClassicClientFactory(Map<String, ObjectSerializer> serializers, ValueFetcher valueFetcher) {
        this.serializers = Serializers.create(serializers);
        this.valueFetcher = notNull(valueFetcher, "The value fetcher cannot be null.");
    }

    @Override
    public HttpClassicClient create() {
        return this.create(Config.builder().build());
    }

    @Override
    public HttpClassicClient create(Config config) {
        return new OkHttpClassicClient(this.serializers, this.valueFetcher, config);
    }
}
