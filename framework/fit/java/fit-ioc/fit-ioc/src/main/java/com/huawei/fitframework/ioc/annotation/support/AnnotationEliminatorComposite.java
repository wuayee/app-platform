/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.support;

import com.huawei.fitframework.ioc.annotation.AnnotationEliminator;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

/**
 * 为 {@link AnnotationEliminator} 提供组合模式的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-05-19
 */
public class AnnotationEliminatorComposite implements AnnotationEliminator {
    private final List<AnnotationEliminator> eliminators;

    /**
     * 初始化 {@link AnnotationEliminatorComposite} 类的新实例。
     */
    public AnnotationEliminatorComposite() {
        this.eliminators = new LinkedList<>();
    }

    public void add(AnnotationEliminator eliminator) {
        if (eliminator != null) {
            this.eliminators.add(eliminator);
        }
    }

    public void remove(AnnotationEliminator eliminator) {
        if (eliminator != null) {
            this.eliminators.remove(eliminator);
        }
    }

    @Override
    public boolean eliminate(Annotation annotation) {
        for (AnnotationEliminator eliminator : this.eliminators) {
            if (eliminator.eliminate(annotation)) {
                return true;
            }
        }
        return false;
    }
}
