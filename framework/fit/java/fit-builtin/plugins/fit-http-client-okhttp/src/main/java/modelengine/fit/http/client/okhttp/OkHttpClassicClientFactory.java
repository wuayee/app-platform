/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.okhttp;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.Serializers;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
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
