/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fit.client.http;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.client.Client;
import com.huawei.fit.client.Request;
import com.huawei.fit.client.Response;
import com.huawei.fit.http.protocol.Protocol;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.conf.runtime.ClientConfig;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.util.MapBuilder;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 Broker 提供基于 HTTP 调用的客户端。
 *
 * @author 季聿阶 j00559309
 * @author 张越 z00559346
 * @author 詹高扬 z50029227
 * @since 2020-10-05
 */
@Component
public class HttpClient implements Client {
    private final Map<CommunicationType, InvokeClient> clients;

    /**
     * 创建 Http 客户端。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     * @param workerConfig 表示当前进程的配置的 {@link WorkerConfig}。
     * @param clientConfig 表示 Http 客户端配置的 {@link ClientConfig}。
     */
    public HttpClient(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        notNull(container, "The bean container cannot be null.");
        notNull(workerConfig, "The worker config cannot be null.");
        InvokeClient syncInvokeClient = InvokeClient.sync(container, workerConfig, clientConfig);
        InvokeClient asyncInvokeClient = InvokeClient.async(container, workerConfig, clientConfig);
        this.clients = MapBuilder.<CommunicationType, InvokeClient>get()
                .put(syncInvokeClient.support(), syncInvokeClient)
                .put(asyncInvokeClient.support(), asyncInvokeClient)
                .build();
    }

    @Override
    public Response requestResponse(@Nonnull Request request) {
        return this.clients.get(request.context().communicationType()).requestResponse(request);
    }

    @Override
    public Set<String> getSupportedProtocols() {
        return Stream.of(Protocol.HTTP.protocol(), Protocol.HTTPS.protocol()).collect(Collectors.toSet());
    }
}
