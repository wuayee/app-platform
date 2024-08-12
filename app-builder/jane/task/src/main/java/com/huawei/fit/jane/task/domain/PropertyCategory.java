/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.domain;

import com.huawei.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 为属性提供类目的匹配器。
 *
 * @author 梁济时
 * @since 2023-08-18
 */
public class PropertyCategory {
    private String value;

    private String category;

    public PropertyCategory() {
        this(null, null);
    }

    public PropertyCategory(String value, String category) {
        this.value = value;
        this.category = category;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            PropertyCategory another = (PropertyCategory) obj;
            return Objects.equals(this.getValue(), another.getValue()) && Objects.equals(this.getCategory(),
                    another.getCategory());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.getValue(), this.getCategory()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[value={0}, category={1}]", this.getValue(), this.getCategory());
    }
}
