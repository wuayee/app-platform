/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.domain;

import com.huawei.fit.jane.task.util.UndefinableValue;

/**
 * 为属性的类目配置提供声明。
 *
 * @author 梁济时
 * @since 2023-08-22
 */
public class PropertyCategoryDeclaration {
    private UndefinableValue<String> value;

    private UndefinableValue<String> category;

    public PropertyCategoryDeclaration() {
        this(null, null);
    }

    public PropertyCategoryDeclaration(UndefinableValue<String> value, UndefinableValue<String> category) {
        this.value = value;
        this.category = category;
    }

    public UndefinableValue<String> getValue() {
        return value;
    }

    public void setValue(UndefinableValue<String> value) {
        this.value = value;
    }

    public UndefinableValue<String> getCategory() {
        return category;
    }

    public void setCategory(UndefinableValue<String> category) {
        this.category = category;
    }
}
