/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.PropertyValueMapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 表示带优先级的 {@link PropertyValueMapper}。
 * <p>{@link PriorityPropertyValueMapper} 会选取第一个成功获取的结果。</p>
 *
 * @author 季聿阶 j00559309
 * @since 2022-08-28
 */
public class PriorityPropertyValueMapper implements PropertyValueMapper {
    private final List<PropertyValueMapper> propertyValueMappers;

    public PriorityPropertyValueMapper(List<PropertyValueMapper> propertyValueMappers) {
        this.propertyValueMappers = getIfNull(propertyValueMappers, Collections::emptyList);
    }

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        return this.propertyValueMappers.stream()
                .map(httpMapper -> httpMapper.map(request, response))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
