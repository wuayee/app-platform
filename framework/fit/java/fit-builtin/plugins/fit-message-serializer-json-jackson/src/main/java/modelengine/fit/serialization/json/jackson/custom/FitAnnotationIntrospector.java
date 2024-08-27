/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.json.jackson.custom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;

import modelengine.fitframework.annotation.Aliases;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示支持 FIT 注解的 jackson 注解解析器。
 *
 * @author 易文渊
 * @since 2024-08-11
 */
public class FitAnnotationIntrospector extends AnnotationIntrospector {
    @Override
    public Version version() {
        return VersionUtil.parseVersion("3.5.0", "modelengine.fit.plugin", "fit-message-serializer-json-jackson");
    }

    @Override
    public JsonInclude.Value findPropertyInclusion(Annotated annotated) {
        SerializeStrategy serializeStrategy = this._findAnnotation(annotated, SerializeStrategy.class);
        JsonInclude.Value value = JsonInclude.Value.empty();
        if (serializeStrategy == null) {
            return value;
        }
        switch (serializeStrategy.include()) {
            case NON_NULL:
                return value.withValueInclusion(JsonInclude.Include.NON_NULL);
            case NON_EMPTY:
                return value.withValueInclusion(JsonInclude.Include.NON_EMPTY);
            case DEFAULT:
            default:
                return value;
        }
    }

    @Override
    public List<PropertyName> findPropertyAliases(Annotated annotated) {
        Aliases aliases = this._findAnnotation(annotated, Aliases.class);
        if (aliases == null) {
            return null;
        }
        return this.findAliases(aliases);
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

    private List<PropertyName> findAliases(Aliases aliases) {
        return Arrays.stream(aliases.value())
                .filter(Objects::nonNull)
                .map(alias -> PropertyName.construct(alias.value()))
                .collect(Collectors.toList());
    }
}
