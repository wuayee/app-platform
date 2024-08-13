/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.handler.PropertyValueMapperResolver;
import com.huawei.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示 {@link ErrorMapper} 的解析器。
 *
 * @author 季聿阶
 * @since 2023-12-11
 */
public class ErrorMapperResolver implements PropertyValueMapperResolver {
    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        if (Throwable.class.isAssignableFrom(propertyValue.getType())) {
            Class<Throwable> errorClass = cast(propertyValue.getType());
            return Optional.of(new ErrorMapper(errorClass));
        }
        return Optional.empty();
    }
}
