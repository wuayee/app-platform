/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.flowable.choir;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.flowable.Subscriber;
import modelengine.fitframework.inspection.Nonnull;

/**
 * 表示 {@link com.huawei.fitframework.flowable.Choir} 的指定 {@link Publisher} 的适配。
 *
 * @param <T> 表示响应式流中数据类型的 {@link T}。
 * @author 季聿阶
 * @since 2024-02-09
 */
public class PublisherChoirAdapter<T> extends AbstractChoir<T> {
    private final Publisher<T> publisher;

    public PublisherChoirAdapter(Publisher<T> publisher) {
        this.publisher = notNull(publisher, "The publisher cannot be null.");
    }

    @Override
    protected void subscribe0(@Nonnull Subscriber<T> subscriber) {
        this.publisher.subscribe(subscriber);
    }
}
