/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fit.http.client.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.Serializers;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fitframework.value.ValueFetcher;

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
