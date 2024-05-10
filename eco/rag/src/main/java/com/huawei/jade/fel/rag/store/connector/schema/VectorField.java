/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector.schema;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 向量数据库的字段。
 *
 * @since 2024-05-07
 */
@Getter
@Setter
public class VectorField {
    private String name;
    private VectorFieldDataType dataType;
    private Map<String, Object> properties;

    /**
     * 根据传入的字段名、字段类型以及其他属性构建 {@link VectorField} 实例。
     *
     * @param name 表示字段名。
     * @param dataType 表示字段类型。
     * @param properties 表示其他属性。
     */
    public VectorField(String name, VectorFieldDataType dataType, Map<String, Object> properties) {
        this.name = name;
        this.dataType = dataType;
        this.properties = properties;
    }

    /**
     * 根据传入的属性名获取字段的属性值。
     *
     * @param propertyName 表示属性名。
     * @return 返回属性值。
     */
    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }
}