/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.websocket.server;

import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.websocket.Session;
import modelengine.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示 {@link WebSocketSessionMapper} 的解析器。
 *
 * @author 季聿阶
 * @since 2023-12-10
 */
public class WebSocketSessionMapperResolver implements PropertyValueMapperResolver {
    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        if (propertyValue.getParameterizedType() != Session.class) {
            return Optional.empty();
        }
        return Optional.of(new WebSocketSessionMapper());
    }
}
