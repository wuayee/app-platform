/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.handler.PropertyValueMapperResolver;
import com.huawei.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示解析 {@link HttpClassicServerRequest} 对象参数的 {@link PropertyValueMapperResolver}。
 *
 * @author 季聿阶 j00559309
 * @since 2022-08-29
 */
public class PropertyClassicRequestResolver implements PropertyValueMapperResolver {
    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        if (propertyValue.getParameterizedType() != HttpClassicServerRequest.class) {
            return Optional.empty();
        }
        UniqueSourcePropertyValueMapper mapper =
                new UniqueSourcePropertyValueMapper(new HttpClassicRequestFetcher(), false);
        return Optional.of(mapper);
    }
}
