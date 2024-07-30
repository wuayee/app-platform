/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.entity;

/**
 * PropertyValue实体类
 *
 * @author 梁济时 l00815032
 * @since 2023-08-28
 */
public class PropertyValue {
    private String property;

    private String dataType;

    private String value;

    /**
     * PropertyValue
     */
    public PropertyValue() {
    }

    public PropertyValue(String property, String dataType, String value) {
        this.property = property;
        this.dataType = dataType;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
