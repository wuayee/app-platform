/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.PropertyValueMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表示带优先级的 {@link PropertyValueMapper}。
 * <p>{@link PriorityPropertyValueMapper} 会选取第一个成功获取的结果。</p>
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class PriorityPropertyValueMapper implements PropertyValueMapper {
    private final List<PropertyValueMapper> propertyValueMappers;

    public PriorityPropertyValueMapper(List<PropertyValueMapper> propertyValueMappers) {
        this.propertyValueMappers = getIfNull(propertyValueMappers, Collections::emptyList);
    }

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        return this.propertyValueMappers.stream()
                .map(httpMapper -> httpMapper.map(request, response, context))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
