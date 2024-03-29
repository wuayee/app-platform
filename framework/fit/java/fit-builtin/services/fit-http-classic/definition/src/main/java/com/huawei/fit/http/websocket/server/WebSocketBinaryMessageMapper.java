/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.websocket.server;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.PropertyValueMapper;

import java.util.Map;

/**
 * 表示 WebSocket 中二进制消息的映射器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-12-11
 */
public class WebSocketBinaryMessageMapper implements PropertyValueMapper {
    /** 表示在自定义上下文中二进制消息的主键。 */
    public static final String KEY = "FIT-WebSocket-Binary-Message";

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        if (context == null) {
            return null;
        }
        Object binaryMessage = context.get(KEY);
        if (binaryMessage instanceof byte[]) {
            return binaryMessage;
        }
        return null;
    }
}
