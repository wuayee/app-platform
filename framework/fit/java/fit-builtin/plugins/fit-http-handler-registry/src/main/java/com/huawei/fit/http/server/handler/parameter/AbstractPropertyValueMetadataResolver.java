/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.parameter;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.handler.PropertyValueMetadata;
import com.huawei.fit.http.server.handler.PropertyValueMetadataResolver;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.value.PropertyValue;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link PropertyValueMetadataResolver} 的抽象的 Http 值元数据解析器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-01-12
 */
public abstract class AbstractPropertyValueMetadataResolver implements PropertyValueMetadataResolver {
    private final AnnotationMetadataResolver annotationResolver;

    protected AbstractPropertyValueMetadataResolver(AnnotationMetadataResolver annotationResolver) {
        this.annotationResolver = notNull(annotationResolver, "The annotation resolver cannot be null.");
    }

    @Override
    public List<PropertyValueMetadata> resolve(PropertyValue propertyValue) {
        AnnotationMetadata annotations = this.annotationResolver.resolve(propertyValue.getElement());
        if (!annotations.isAnnotationPresent(this.getAnnotation())) {
            return Collections.emptyList();
        }
        return this.resolve(propertyValue, annotations);
    }

    /**
     * 获取需要解析的注解的类型。
     *
     * @return 表示需要解析的注解类型的 {@link Class}{@code <? extends }{@link Annotation}{@code >}
     */
    protected abstract Class<? extends Annotation> getAnnotation();

    /**
     * 解析属性值及注解，来获取属性值的元数据。
     *
     * @param propertyValue 表示待解析的属性值的 {@link PropertyValue}。
     * @param annotations 表示待解析的注解的 {@link AnnotationMetadata}。
     * @return 表示解析后的 Http 值的元数据的 {@link List}{@code <}{@link PropertyValueMetadata}{@code >}。
     */
    protected abstract List<PropertyValueMetadata> resolve(PropertyValue propertyValue, AnnotationMetadata annotations);
}
