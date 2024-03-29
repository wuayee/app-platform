/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.protocol.jar.support;

import com.huawei.fitframework.protocol.jar.Jar;

import java.util.stream.Stream;

/**
 * 为 {@link Jar.EntryCollection} 提供空的装饰程序。
 *
 * @author 梁济时 l00815032
 * @since 2023-02-21
 */
class EmptyJarEntryCollectionDecorator implements Jar.EntryCollection {
    private final Jar.EntryCollection decorated;

    EmptyJarEntryCollectionDecorator(Jar.EntryCollection decorated) {
        if (decorated == null) {
            throw new IllegalArgumentException("The decorated JAR entry collection cannot be null.");
        } else {
            this.decorated = decorated;
        }
    }

    final Jar.EntryCollection decorated() {
        return this.decorated;
    }

    @Override
    public int size() {
        return this.decorated.size();
    }

    @Override
    public Jar.Entry get(int index) {
        return this.decorate(this.decorated.get(index));
    }

    @Override
    public Jar.Entry get(String name) {
        Jar.Entry entry = this.decorated.get(name);
        if (entry != null) {
            entry = this.decorate(entry);
        }
        return entry;
    }

    @Override
    public Stream<Jar.Entry> stream() {
        return this.decorated.stream().map(this::decorate);
    }

    @Override
    public Iterator iterator() {
        return this.new Iterator(this.decorated.iterator());
    }

    protected Jar.Entry decorate(Jar.Entry entry) {
        return entry;
    }

    private final class Iterator implements java.util.Iterator<Jar.Entry> {
        private final java.util.Iterator<Jar.Entry> iterator;

        private Iterator(java.util.Iterator<Jar.Entry> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public Jar.Entry next() {
            Jar.Entry entry = this.iterator.next();
            return EmptyJarEntryCollectionDecorator.this.decorate(entry);
        }
    }
}
