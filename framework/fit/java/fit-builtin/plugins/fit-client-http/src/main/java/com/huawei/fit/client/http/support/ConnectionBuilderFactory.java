/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import com.huawei.fit.client.http.websocket.SecureWebSocketConnectionBuilder;
import com.huawei.fit.client.http.websocket.WebSocketConnectionBuilder;
import com.huawei.fit.http.protocol.Protocol;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 通过协议生产对应的 {@link ConnectionBuilder}
 *
 * @author 王成
 * @author 季聿阶
 * @since 2023-11-05
 */
public class ConnectionBuilderFactory {
    private static final Map<Protocol, ConnectionBuilder> CONNECTION_BUILDERS =
            MapBuilder.<Protocol, ConnectionBuilder>get()
                    .put(Protocol.HTTP, new HttpConnectionBuilder())
                    .put(Protocol.HTTPS, new HttpsConnectionBuilder())
                    .put(Protocol.WEB_SOCKET, new WebSocketConnectionBuilder())
                    .put(Protocol.SECURE_WEB_SOCKET, new SecureWebSocketConnectionBuilder())
                    .build();

    /**
     * 构建一个 {@link ConnectionBuilder}。
     *
     * @param protocol 表示请求协议的 {@link Protocol}。
     * @return 表示构建出来的 {@link ConnectionBuilder}。
     */
    public static ConnectionBuilder getConnectionBuilder(Protocol protocol) {
        ConnectionBuilder connectionBuilder = CONNECTION_BUILDERS.get(protocol);
        return notNull(connectionBuilder, "Not supported protocol. [protocol={0}]", protocol);
    }
}
