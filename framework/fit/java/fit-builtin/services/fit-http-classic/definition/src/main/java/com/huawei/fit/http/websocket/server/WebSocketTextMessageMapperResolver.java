/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.websocket.server;

import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.handler.PropertyValueMapperResolver;
import com.huawei.fit.http.websocket.annotation.TextMessage;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示 {@link WebSocketTextMessageMapper} 的映射器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-12-11
 */
public class WebSocketTextMessageMapperResolver implements PropertyValueMapperResolver {
    private final AnnotationMetadataResolver annotationResolver;

    public WebSocketTextMessageMapperResolver(AnnotationMetadataResolver annotationResolver) {
        this.annotationResolver = annotationResolver;
    }

    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        AnnotationMetadata annotations = this.annotationResolver.resolve(propertyValue.getElement());
        if (annotations.isAnnotationNotPresent(TextMessage.class)) {
            return Optional.empty();
        }
        return Optional.of(new WebSocketTextMessageMapper());
    }
}
