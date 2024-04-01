/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.parameter;

import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.server.handler.PropertyValueMetadata;
import com.huawei.fit.http.server.handler.PropertyValueMetadataResolver;
import com.huawei.fit.http.server.handler.Source;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * 表示解析带有 {@link RequestBody} 注解的参数的 {@link PropertyValueMetadataResolver}。
 *
 * @author 季聿阶 j00559309
 * @since 2023-01-12
 */
public class RequestBodyMetadataResolver extends AbstractPropertyValueMetadataResolver {
    public RequestBodyMetadataResolver(AnnotationMetadataResolver annotationResolver) {
        super(annotationResolver);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return RequestBody.class;
    }

    @Override
    protected List<PropertyValueMetadata> resolve(PropertyValue propertyValue, AnnotationMetadata annotations) {
        RequestBody body = annotations.getAnnotation(RequestBody.class);
        Property property = annotations.getAnnotation(Property.class);
        PropertyValueMetadata propertyValueMetadata = PropertyValueMetadata.builder()
                .name(body.key())
                .in(Source.BODY)
                .description(property != null ? property.description() : StringUtils.EMPTY)
                .example(property != null ? property.example() : StringUtils.EMPTY)
                .type(propertyValue.getParameterizedType())
                .isRequired(body.required())
                .defaultValue(null)
                .element(propertyValue.getElement())
                .build();
        return Collections.singletonList(propertyValueMetadata);
    }
}
