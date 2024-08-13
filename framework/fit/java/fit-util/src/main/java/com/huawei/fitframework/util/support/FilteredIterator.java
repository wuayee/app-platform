/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.util.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * 为 {@link Iterator} 提供带过滤器的实现。
 *
 * @author 梁济时
 * @since 2022-07-01
 */
public class FilteredIterator<E> implements Iterator<E> {
    private final Iterator<E> origin;
    private final Predicate<E> filter;
    private boolean hasNext;
    private E next;

    public FilteredIterator(Iterator<E> origin, Predicate<E> filter) {
        this.origin = notNull(origin, "The origin iterator to filter cannot be null.");
        this.filter = notNull(filter, "The filter of element in iterator cannot be null.");
        this.hasNext = true;
        this.moveNext();
    }

    protected final Iterator<E> origin() {
        return this.origin;
    }

    private void moveNext() {
        if (!this.hasNext) {
            return;
        }
        while (this.origin.hasNext()) {
            this.next = this.origin.next();
            if (this.filter.test(this.next)) {
                return;
            }
        }
        this.hasNext = false;
        this.next = null;
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public E next() {
        if (this.hasNext) {
            E nextItem = this.next;
            this.moveNext();
            return nextItem;
        } else {
            throw new NoSuchElementException();
        }
    }
}
