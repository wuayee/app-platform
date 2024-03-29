/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.support;

import com.huawei.fitframework.ioc.annotation.AnnotationPropertyForward;
import com.huawei.fitframework.ioc.annotation.AnnotationPropertyForwarder;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 为 {@link AnnotationPropertyForwarder} 提供组合模式的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-05-19
 */
public class AnnotationPropertyForwarderComposite implements AnnotationPropertyForwarder {
    private final List<AnnotationPropertyForwarder> forwarders;

    /**
     * 初始化 {@link AnnotationPropertyForwarderComposite} 类的新实例。
     */
    public AnnotationPropertyForwarderComposite() {
        this.forwarders = new LinkedList<>();
    }

    public void add(AnnotationPropertyForwarder forwarder) {
        if (forwarder != null) {
            this.forwarders.add(forwarder);
        }
    }

    public void remove(AnnotationPropertyForwarder forwarder) {
        if (forwarder != null) {
            this.forwarders.remove(forwarder);
        }
    }

    public int size() {
        return this.forwarders.size();
    }

    @Override
    public Optional<AnnotationPropertyForward> forward(Method propertyMethod) {
        for (AnnotationPropertyForwarder forwarder : this.forwarders) {
            Optional<AnnotationPropertyForward> result = forwarder.forward(propertyMethod);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
