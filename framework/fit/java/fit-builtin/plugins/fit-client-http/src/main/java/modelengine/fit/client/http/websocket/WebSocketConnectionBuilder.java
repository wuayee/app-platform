/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.client.http.websocket;

import modelengine.fit.client.http.support.AbstractConnectionBuilder;
import modelengine.fit.http.protocol.Protocol;

/**
 * 表示 WebSocket 链接的构建器。
 *
 * @author 季聿阶
 * @since 2024-05-07
 */
public class WebSocketConnectionBuilder extends AbstractConnectionBuilder {
    @Override
    public Protocol protocol() {
        return Protocol.WEB_SOCKET;
    }
}
