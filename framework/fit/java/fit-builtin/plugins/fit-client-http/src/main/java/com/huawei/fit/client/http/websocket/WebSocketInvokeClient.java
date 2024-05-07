/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.client.http.websocket;

import com.huawei.fit.client.Request;
import com.huawei.fit.client.Response;
import com.huawei.fit.client.http.support.AbstractInvokeClient;
import com.huawei.fit.client.http.support.ConnectionBuilder;
import com.huawei.fit.client.http.support.ConnectionBuilderFactory;
import com.huawei.fit.client.http.util.HttpClientUtils;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.protocol.Protocol;
import com.huawei.fit.http.websocket.Session;
import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.conf.runtime.ClientConfig;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Solo;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.TypeUtils;

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
        // TODO 需要增加一个自定义 listener
        Session session = client.createWebSocketSession(url, null);
        if (HttpClientUtils.isReactor(request.returnType())) {
            // TODO 响应式结果，可以直接创建一个对象，然后返回，这个对象同时接收 session 的消息
            Class<?> clazz = TypeUtils.toClass(request.returnType());
            if (Choir.class.isAssignableFrom(clazz)) {
                // TODO 返回值是 Choir
            } else if (Solo.class.isAssignableFrom(clazz)) {
                // TODO 返回值是 Solo
            } else {
                throw new UnsupportedOperationException(StringUtils.format("Not supported return type. [type={0}]",
                        request.returnType().getTypeName()));
            }
        } else {
            // TODO 结果为一般结果，需要等待所有结果处理完毕之后统一返回
        }
        return null;
    }

    @Override
    public CommunicationType support() {
        return CommunicationType.SYNC;
    }
}
