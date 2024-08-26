/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.support;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.TextEvent;
import com.huawei.fit.http.entity.TextEventStreamEntity;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.flowable.choir.AbstractChoir;
import com.huawei.fitframework.flowable.subscription.AbstractSubscription;
import com.huawei.fitframework.flowable.util.worker.Worker;
import com.huawei.fitframework.flowable.util.worker.WorkerObserver;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.LockUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

/**
 * 表示 {@link Choir} 的事件流的实现。
 *
 * @author 季聿阶
 * @since 2024-08-17
 */
public class TextStreamChoir<T> extends AbstractChoir<T> implements Choir<T> {
    private final HttpClassicClientRequest request;
    private final Type responseType;

    public TextStreamChoir(HttpClassicClientRequest request, Type responseType) {
        this.request = request;
        this.responseType = responseType;
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        subscriber.onSubscribed(new TextStreamSubscription<>(subscriber, this.request, this.responseType));
    }

    private static class TextStreamSubscription<T> extends AbstractSubscription implements WorkerObserver<T> {
        private static final int HTTP_SUCCESS_CODE_MIN = 200;
        private static final int HTTP_SUCCESS_CODE_MAX = 300;

        private final Subscriber<T> subscriber;
        private final HttpClassicClientRequest request;
        private final Type responseType;
        private final AtomicBoolean requested = new AtomicBoolean();
        private final AtomicBoolean completed = new AtomicBoolean();
        private final AtomicLong counter = new AtomicLong();
        private final Lock lock = LockUtils.newReentrantLock();
        private final Queue<T> buffer = new ArrayDeque<>();
        private volatile Exception error;

        TextStreamSubscription(Subscriber<T> subscriber, HttpClassicClientRequest request, Type responseType) {
            this.subscriber = subscriber;
            this.request = request;
            this.responseType = responseType;
        }

        @Override
        protected void request0(long count) {
            long num = this.counter.addAndGet(count);
            if (this.requested.compareAndSet(false, true)) {
                this.exchange();
            }
            synchronized (this.lock) {
                for (int i = 0; i < num; i++) {
                    if (this.buffer.isEmpty()) {
                        this.handleBufferIsEmpty();
                        return;
                    } else {
                        this.subscriber.consume(this.buffer.remove());
                        this.counter.decrementAndGet();
                    }
                }
            }
        }

        private void exchange() {
            try (HttpClassicClientResponse<T> response = this.request.exchange(this.responseType)) {
                if (!ObjectUtils.between(response.statusCode(), HTTP_SUCCESS_CODE_MIN, HTTP_SUCCESS_CODE_MAX,
                        true, false)) {
                    throw new IllegalStateException(
                            StringUtils.format("Http response error. [statusCode={0}]", response.statusCode()));
                }
                TextEventStreamEntity entity = response.textEventStreamEntity()
                        .orElseThrow(() -> new IllegalStateException("No text event stream entity."));
                Worker.create(this, entity.stream().map(this::convert)).run();
            } catch (IOException e) {
                this.onWorkerFailed(e);
            }
        }

        private void handleBufferIsEmpty() {
            if (!this.completed.get()) {
                return;
            }
            if (this.error != null) {
                this.subscriber.fail(this.error);
            } else {
                this.subscriber.complete();
            }
        }

        private T convert(TextEvent textEvent) {
            if (this.responseType == TextEvent.class) {
                return cast(textEvent);
            } else {
                return cast(textEvent.data());
            }
        }

        @Override
        public void onWorkerSubscribed(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onWorkerConsumed(T data, long id) {
            synchronized (this.lock) {
                this.buffer.add(data);
                if (this.counter.get() > 0) {
                    this.subscriber.consume(this.buffer.remove());
                    this.counter.decrementAndGet();
                }
            }
        }

        @Override
        public void onWorkerFailed(Exception cause) {
            this.completed.set(true);
            this.error = cause;
        }

        @Override
        public void onWorkerCompleted() {
            this.completed.set(true);
        }
    }
}
