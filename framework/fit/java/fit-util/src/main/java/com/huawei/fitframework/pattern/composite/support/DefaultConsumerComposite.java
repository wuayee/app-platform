/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.pattern.composite.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.pattern.composite.ConsumerComposite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 为 {@link ConsumerComposite} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @since 2020-10-15
 */
public class DefaultConsumerComposite<T> implements ConsumerComposite<T> {
    private final List<Consumer<T>> consumers;

    public DefaultConsumerComposite() {
        this.consumers = new ArrayList<>();
    }

    @Override
    public void add(Consumer<T> consumer) {
        this.consumers.add(consumer);
    }

    @Override
    public void addAll(Collection<Consumer<T>> consumers) {
        Validation.notNull(consumers, "The consumers to add to consumer composite cannot be null.");
        this.consumers.addAll(consumers);
    }

    @Override
    public void remove(Consumer<T> consumer) {
        this.consumers.remove(consumer);
    }

    @Override
    public void removeAll(Collection<Consumer<T>> consumers) {
        Validation.notNull(consumers, "The consumers to remove from consumer composite cannot be null.");
        this.consumers.removeAll(consumers);
    }

    @Override
    public List<Consumer<T>> getConsumers() {
        return Collections.unmodifiableList(this.consumers);
    }
}
