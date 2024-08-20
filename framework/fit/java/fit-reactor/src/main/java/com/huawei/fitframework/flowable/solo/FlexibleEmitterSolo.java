/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.solo;

import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.flowable.Solo;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.choir.FlexibleEmitterChoir;
import com.huawei.fitframework.flowable.util.OnSubscribedObserver;
import com.huawei.fitframework.inspection.Nonnull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 表示 {@link Solo} 的可配置订阅、元素请求、取消行为的数据发送器实现。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 何天放
 * @since 2024-05-22
 */
public class FlexibleEmitterSolo<T> extends AbstractSolo<T> implements OnSubscribedObserver {
    private final FlexibleEmitterChoir<T> adapted;

    /**
     * 构建一个可配置订阅、元素请求、取消行为并且订阅关系发生通知时机可控的 {@link Solo} 数据发送器实现。
     *
     * @param emitterSupplier 表示数据发送器供应方式的 {@link Supplier}{@code <}{@link T}{@code >}。
     * @param emitterConsumer 表示数据发送器消费方式的 {@link Consumer}{@code <}{@link T}{@code >}。
     * @param subscribeHandler 表示订阅行为的 {@link Runnable}。
     * @param requestHandler 表示元素请求行为的 {@link Consumer}{@code <}{@link Long}{@code >}。
     * @param cancelHandler 表示取消行为的 {@link Runnable}。
     */
    public FlexibleEmitterSolo(Supplier<Emitter<T>> emitterSupplier, Consumer<Emitter<T>> emitterConsumer,
            Consumer<OnSubscribedObserver> subscribeHandler, Consumer<Long> requestHandler, Runnable cancelHandler) {
        this.adapted = new FlexibleEmitterChoir<>(emitterSupplier,
                emitterConsumer,
                subscribeHandler,
                requestHandler,
                cancelHandler);
    }

    @Override
    public void notifyOnSubscribed() {
        this.adapted.notifyOnSubscribed();
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        Solo.fromPublisher(this.adapted).subscribe(subscriber);
    }
}
