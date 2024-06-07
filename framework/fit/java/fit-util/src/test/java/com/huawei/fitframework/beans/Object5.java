/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.beans;

import com.huawei.fitframework.annotation.Property;

/**
 * Object5
 *
 * @author 易文渊
 * @since 2024-06-06
 */
public class Object5 {
    @Property(name = "foo_bar")
    private String fooBar;

    /**
     * 获取第一个属性。
     *
     * @return 表示第一个属性的 {@link String}。
     */
    public String getFooBar() {
        return fooBar;
    }

    /**
     * 设置第一个属性。
     *
     * @param fooBar 表示待设置的第一个属性的 {@link String}。
     */
    public void setFooBar(String fooBar) {
        this.fooBar = fooBar;
    }
}