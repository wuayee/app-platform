/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jober.taskcenter.domain.util.CategoryAcceptor;

import modelengine.fitframework.util.ParsingResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为 {@link CategoryAcceptor} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-10-28
 */
public class DefaultCategoryAcceptor implements CategoryAcceptor {
    private final List<Classifier> classifiers;

    /**
     * 构造函数
     *
     * @param properties 属性列表
     */
    public DefaultCategoryAcceptor(List<TaskProperty> properties) {
        this.classifiers = new LinkedList<>();
        for (TaskProperty property : properties) {
            for (PropertyCategory propertyCategory : property.categories()) {
                ParsingResult<?> value = property.dataType().parse(propertyCategory.getValue());
                if (value.isParsed()) {
                    String propertyName = property.name();
                    Object propertyValue = value.getResult();
                    String category = propertyCategory.getCategory();
                    this.classifiers.add(new Classifier(propertyName, propertyValue, category));
                }
            }
        }
    }

    @Override
    public List<String> obtain(Map<String, Object> info) {
        return this.classifiers.stream()
                .map(classifier -> classifier.classify(info))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 为任务实例提供属性的分类程序。
     *
     * @author 梁济时
     * @since 2023-10-28
     */
    private static class Classifier {
        private final String propertyName;

        private final Object propertyValue;

        private final String category;

        Classifier(String propertyName, Object propertyValue, String category) {
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
            this.category = category;
        }

        String classify(Map<String, Object> info) {
            Object actual = info.get(this.propertyName);
            if (Objects.equals(this.propertyValue, actual)) {
                return this.category;
            } else {
                return null;
            }
        }
    }
}
