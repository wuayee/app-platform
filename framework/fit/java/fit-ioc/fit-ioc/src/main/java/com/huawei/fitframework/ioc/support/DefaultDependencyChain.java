/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanMetadata;
import com.huawei.fitframework.ioc.DependencyChain;
import com.huawei.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 为 {@link DependencyChain} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-07-08
 */
class DefaultDependencyChain implements DependencyChain {
    private final DependencyChain parent;
    private final BeanMetadata metadata;

    /**
     * 使用父链及当前的Bean元数据初始化 {@link DefaultDependencyChain} 类的新实例。
     *
     * @param parent 表示父链的 {@link DependencyChain}。
     * @param metadata 表示当前Bean的元数据的 {@link BeanMetadata}。
     * @throws IllegalArgumentException {@code metadata} 为 {@code null}。
     */
    DefaultDependencyChain(DependencyChain parent, BeanMetadata metadata) {
        Validation.notNull(metadata, "The metadata of dependency chain cannot be null.");
        this.parent = parent;
        this.metadata = metadata;
    }

    @Override
    public DependencyChain next(BeanMetadata metadata) {
        return new DefaultDependencyChain(this, metadata);
    }

    @Override
    public Iterator<BeanMetadata> iterator() {
        return this.new MetadataIterator();
    }

    private class MetadataIterator implements Iterator<BeanMetadata> {
        private Iterator<BeanMetadata> iterator;

        @Override
        public boolean hasNext() {
            if (this.iterator == null) {
                return true;
            } else {
                return this.iterator.hasNext();
            }
        }

        @Override
        public BeanMetadata next() {
            if (this.iterator == null) {
                this.iterator = Optional.ofNullable(DefaultDependencyChain.this.parent)
                        .map(Iterable::iterator)
                        .orElse(Collections.emptyIterator());
                return DefaultDependencyChain.this.metadata;
            } else {
                return this.iterator.next();
            }
        }
    }

    @Override
    public String toString() {
        List<String> names = new LinkedList<>();
        for (BeanMetadata metadata : this) {
            names.add(metadata.name());
        }
        Collections.reverse(names);
        return StringUtils.join(" -> ", names);
    }
}
