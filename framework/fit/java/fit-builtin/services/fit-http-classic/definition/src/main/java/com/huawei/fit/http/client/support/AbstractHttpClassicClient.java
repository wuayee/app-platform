/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.client.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.Serializers;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fitframework.value.ValueFetcher;

/**
 * 表示 {@link HttpClassicClient} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2022-12-04
 */
public abstract class AbstractHttpClassicClient implements HttpClassicClient {
    private final Serializers serializers;
    private final ValueFetcher valueFetcher;

    public AbstractHttpClassicClient(Serializers serializers, ValueFetcher valueFetcher) {
        this.serializers = notNull(serializers, "The serializers cannot be null.");
        this.valueFetcher = notNull(valueFetcher, "The value fetcher cannot be null.");
    }

    @Override
    public Serializers serializers() {
        return this.serializers;
    }

    @Override
    public ValueFetcher valueFetcher() {
        return this.valueFetcher;
    }
}
