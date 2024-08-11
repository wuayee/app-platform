/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.json.jackson.custom;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.util.StringUtils;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;

/**
 * 表示支持 FIT 注解的 jackson 注解解析器。
 *
 * @author 易文渊
 * @since 2024-08-11
 */
public class FitAnnotationIntrospector extends AnnotationIntrospector {
    @Override
    public Version version() {
        return VersionUtil.parseVersion("3.5.0", "com.huawei.fit.plugin", "fit-message-serializer-json-jackson");
    }

    @Override
    public PropertyName findNameForSerialization(Annotated annotated) {
        return this.findName(annotated);
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated annotated) {
        return this.findName(annotated);
    }

    private PropertyName findName(Annotated annotated) {
        Property property = this._findAnnotation(annotated, Property.class);
        if (property == null || StringUtils.isEmpty(property.name())) {
            return null;
        }
        return PropertyName.construct(property.name());
    }
}
