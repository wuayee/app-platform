/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.websocket.server;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.handler.PropertyValueMapperResolver;
import com.huawei.fit.http.websocket.annotation.TextMessage;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示 {@link WebSocketTextMessageMapper} 的映射器。
 *
 * @author 季聿阶
 * @since 2023-12-11
 */
public class WebSocketTextMessageMapperResolver implements PropertyValueMapperResolver {
    private final AnnotationMetadataResolver annotationResolver;

    public WebSocketTextMessageMapperResolver(AnnotationMetadataResolver annotationResolver) {
        this.annotationResolver = annotationResolver;
    }

    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        return notNull(propertyValue, "The property value cannot be null.").getElement()
                .map(this.annotationResolver::resolve)
                .filter(annotations -> annotations.isAnnotationPresent(TextMessage.class))
                .map(annotations -> new WebSocketTextMessageMapper());
    }
}
