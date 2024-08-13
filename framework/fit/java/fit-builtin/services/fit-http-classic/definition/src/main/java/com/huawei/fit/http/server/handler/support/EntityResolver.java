/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.handler.PropertyValueMapperResolver;
import com.huawei.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示解析 {@link Entity} 对象参数的 {@link PropertyValueMapperResolver}。
 *
 * @author 季聿阶
 * @since 2022-08-31
 */
public class EntityResolver implements PropertyValueMapperResolver {
    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        if (!Entity.class.isAssignableFrom(propertyValue.getType())) {
            return Optional.empty();
        }
        UniqueSourcePropertyValueMapper mapper = new UniqueSourcePropertyValueMapper(new EntityFetcher(), false);
        return Optional.of(mapper);
    }
}
