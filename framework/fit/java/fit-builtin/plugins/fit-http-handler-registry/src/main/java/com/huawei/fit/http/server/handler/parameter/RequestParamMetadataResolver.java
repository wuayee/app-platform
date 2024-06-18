/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.parameter;

import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.handler.PropertyValueMetadata;
import com.huawei.fit.http.server.handler.PropertyValueMetadataResolver;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * 表示解析带有 {@link RequestParam} 注解的参数的 {@link PropertyValueMetadataResolver}。
 *
 * @author 季聿阶 j00559309
 * @since 2023-01-12
 */
public class RequestParamMetadataResolver extends AbstractPropertyValueMetadataResolver {
    public RequestParamMetadataResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestParam.class;
    }

    @Override
    protected List<PropertyValueMetadata> resolve(PropertyValue propertyValue, AnnotationMetadata annotations) {
        RequestParam param = annotations.getAnnotation(RequestParam.class);
        Property property = annotations.getAnnotation(Property.class);
        PropertyValueMetadata propertyValueMetadata = PropertyValueMetadata.builder()
                .name(StringUtils.blankIf(param.name(), propertyValue.getName()))
                .in(param.in())
                .description(property != null ? property.description() : StringUtils.EMPTY)
                .example(property != null ? property.example() : StringUtils.EMPTY)
                .type(propertyValue.getParameterizedType())
                .isRequired(param.required())
                .defaultValue(param.defaultValue())
                .element(propertyValue.getElement().orElse(null))
                .build();
        return Collections.singletonList(propertyValueMetadata);
    }
}
