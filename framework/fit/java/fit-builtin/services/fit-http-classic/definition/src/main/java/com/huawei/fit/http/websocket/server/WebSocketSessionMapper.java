/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.websocket.server;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.PropertyValueMapper;

import java.util.Map;

/**
 * 表示 WebSocket 中会话的映射器。
 *
 * @author 季聿阶
 * @since 2023-12-10
 */
public class WebSocketSessionMapper implements PropertyValueMapper {
    /** 表示存储在 WebSocket 握手请求中的会话主键。 */
    public static final String KEY = "FIT-WebSocket-Session";

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        return request.attributes().get(KEY).orElse(null);
    }
}
