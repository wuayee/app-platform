/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.client.jdk;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.Serializers;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.value.ValueFetcher;

import java.util.Map;

/**
 * 表示 {@link HttpClassicClientFactory} 的 JDK 实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-04
 */
@Order(Order.HIGH)
@Component
public class JdkHttpClassicClientFactory implements HttpClassicClientFactory {
    private final Serializers serializers;
    private final ValueFetcher valueFetcher;

    public JdkHttpClassicClientFactory(Map<String, ObjectSerializer> serializers, ValueFetcher valueFetcher) {
        this.serializers = Serializers.create(serializers);
        this.valueFetcher = notNull(valueFetcher, "The value fetcher cannot be null.");
    }

    @Override
    public HttpClassicClient create() {
        return this.create(Config.builder().build());
    }

    @Override
    public HttpClassicClient create(Config config) {
        return new JdkHttpClassicClient(this.serializers, this.valueFetcher, config);
    }
}
