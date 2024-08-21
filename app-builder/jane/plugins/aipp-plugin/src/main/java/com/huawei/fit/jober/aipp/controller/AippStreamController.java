/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.annotation.OnClose;
import modelengine.fit.http.websocket.annotation.OnMessage;
import modelengine.fit.http.websocket.annotation.OnOpen;
import modelengine.fit.http.websocket.annotation.TextMessage;
import modelengine.fit.http.websocket.annotation.WebSocketEndpoint;
import com.huawei.fit.jober.aipp.entity.StreamLogParam;
import com.huawei.fit.jober.aipp.service.AippStreamService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

/**
 * aipp-websocket流式接口.
 *
 * @author 张越
 * @since 2024-005-14
 */
@WebSocketEndpoint(path = "/v1/api/aipp/wsStream")
@Component
public class AippStreamController {
    private static final Logger log = Logger.get(AippStreamController.class);

    private final AippStreamService aippStreamService;

    public AippStreamController(AippStreamService aippStreamService) {
        this.aippStreamService = aippStreamService;
    }

    /**
     * 当一个新的 WebSocket 会话打开时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param aippId aipp应用id {@link String}。
     * @param version 版本号 {@link String}。
     */
    @OnOpen
    public void onOpen(Session session, @PathVariable("aippId") String aippId,
            @PathVariable("version") String version) {
        log.warn(StringUtils.format("WebSocket connection open by client. [aippId={0}, version={1}]", aippId, version));
    }

    /**
     * 当收到 WebSocket 二进制消息时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param message 表示收到的 WebSocket 二进制消息的 {@link String}。
     */
    @OnMessage
    public void onMessage(Session session, @TextMessage String message) {
        StreamLogParam param = JsonUtils.parseObject(message, StreamLogParam.class);
        this.aippStreamService.addSession(param.getAippInstanceId(), session);
    }

    /**
     * 当 WebSocket 会话关闭时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     */
    @OnClose
    public void onClose(Session session) {
        log.warn(StringUtils.format("WebSocket connection closed by client. [code={0}, reason={1}]",
                session.getCloseCode(),
                session.getCloseReason()));
        this.aippStreamService.removeSession(session);
    }
}
