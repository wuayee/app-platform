/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.websocket.server;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.PropertyValueMapper;

import java.util.Map;

/**
 * 表示 WebSocket 中文本消息的映射器。
 *
 * @author 季聿阶
 * @since 2023-12-11
 */
public class WebSocketTextMessageMapper implements PropertyValueMapper {
    /** 表示在自定义上下文中文本消息的主键。 */
    public static final String KEY = "FIT-WebSocket-Text-Message";

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        if (context == null) {
            return null;
        }
        Object textMessage = context.get(KEY);
        if (textMessage instanceof String) {
            return textMessage;
        }
        return null;
    }
}
