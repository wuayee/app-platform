package com.huawei.fitframework.flowable.publisher;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Subscriber;
import com.huawei.fitframework.flowable.Subscription;
import com.huawei.fitframework.flowable.operation.AbstractOperation;

import java.util.function.Predicate;

/**
 * 表示 {@link Publisher} 的携带数据过滤的实现。
 *
 * @param <T> 表示发布者中待过滤数据的类型。
 * @author 何天放 h00679269
 * @since 2024-02-20
 */
public class FilterPublisherDecorator<T> implements Publisher<T> {
    private final Publisher<T> decorated;
    private final Predicate<T> filter;

    public FilterPublisherDecorator(Publisher<T> decorated, Predicate<T> filter) {
        this.decorated = notNull(decorated, "The decorated filter publisher cannot be null.");
        this.filter = notNull(filter, "The filter cannot be null.");
    }

    @Override
    public void subscribe(Subscriber<T> subscriber) {
        this.decorated.subscribe(new FilterOperation<>(this.filter, subscriber));
    }

    private static class FilterOperation<T> extends AbstractOperation<T, T> {
        private final Predicate<T> filter;

        FilterOperation(Predicate<T> filter, Subscriber<T> subscriber) {
            super(subscriber);
            this.filter = filter;
        }

        @Override
        protected void consume0(Subscription subscription, T data) {
            final boolean result;
            try {
                result = this.filter.test(data);
            } catch (Exception cause) {
                this.getPreSubscription().cancel();
                this.getNextSubscriber().fail(cause);
                return;
            }
            if (result) {
                this.getNextSubscriber().consume(data);
            } else {
                this.getPreSubscription().request(1);
            }
        }
    }
}
