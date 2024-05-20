/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.subscriber;

import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.inspection.Nonnull;

/**
 * 表示仅请求数据，但不消费数据的 {@link Subscriber 订阅者}。
 *
 * @param <T> 表示订阅者订阅的数据类型的 {@link T}。
 * @author 季聿阶 j00559309
 * @since 2024-02-09
 */
public class EmptySubscriber<T> extends AbstractSubscriber<T> {
    /** 表示 {@link EmptySubscriber} 的单例。 */
    public static final Subscriber<Object> INSTANCE = new EmptySubscriber<>();

    @Override
    protected void onSubscribed0(@Nonnull Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    protected void consume(@Nonnull Subscription subscription, T data) {}

    @Override
    protected void complete(@Nonnull Subscription subscription) {}

    @Override
    protected void fail(@Nonnull Subscription subscription, Exception cause) {}
}
