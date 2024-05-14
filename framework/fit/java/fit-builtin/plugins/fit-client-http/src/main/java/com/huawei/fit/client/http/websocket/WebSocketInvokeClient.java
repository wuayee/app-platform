/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.client.http.websocket;

import com.huawei.fit.client.Request;
import com.huawei.fit.client.Response;
import com.huawei.fit.client.http.support.AbstractInvokeClient;
import com.huawei.fit.client.http.support.ConnectionBuilder;
import com.huawei.fit.client.http.support.ConnectionBuilderFactory;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.protocol.Protocol;
import com.huawei.fit.http.websocket.Session;
import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.conf.runtime.ClientConfig;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.exception.ClientException;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.BeanContainer;

/**
 * 表示 {@link com.huawei.fit.client.http.InvokeClient} 的 WebSocket 流式实现。
 *
 * @author 季聿阶
 * @since 2024-05-06
 */
public class WebSocketInvokeClient extends AbstractInvokeClient {
    public WebSocketInvokeClient(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        super(container, workerConfig, clientConfig);
    }

    @Override
    public Response requestResponse(@Nonnull Request request) {
        HttpClassicClient client = this.buildHttpClient(request);
        ConnectionBuilder builder = ConnectionBuilderFactory.getConnectionBuilder(Protocol.from(request.protocol()));
        String url = builder.buildUrl(request);
        WebsocketInvoker invoker = new WebsocketInvoker(this.getContainer(), request);
        Session session = client.createWebSocketSession(url, invoker);
        invoker.request(session, request);
        try {
            return invoker.waitAndgetResponse();
        } catch (InterruptedException cause) {
            throw new ClientException("Failed to wait websocket request.", cause);
        }
    }

    @Override
    public CommunicationType support() {
        return CommunicationType.SYNC;
    }
}
