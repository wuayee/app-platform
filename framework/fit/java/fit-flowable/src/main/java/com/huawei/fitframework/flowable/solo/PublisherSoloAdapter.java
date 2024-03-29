/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.solo;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Solo;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.inspection.Nonnull;

/**
 * 表示 {@link Solo} 的指定 {@link Publisher} 的适配。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶 j00559309
 * @since 2024-02-09
 */
public class PublisherSoloAdapter<T> extends AbstractSolo<T> {
    private final Publisher<T> publisher;

    public PublisherSoloAdapter(Publisher<T> publisher) {
        this.publisher = notNull(publisher, "The publisher cannot be null.");
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        this.publisher.subscribe(subscriber);
    }
}
