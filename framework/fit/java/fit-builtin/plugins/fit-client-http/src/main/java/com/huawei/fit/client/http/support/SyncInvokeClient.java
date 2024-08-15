/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.client.http.support;

import com.huawei.fit.client.Request;
import com.huawei.fit.client.Response;
import com.huawei.fit.client.http.util.HttpClientUtils;
import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.conf.runtime.ClientConfig;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.exception.ClientException;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.BeanContainer;

import java.io.IOException;

/**
 * 表示 {@link com.huawei.fit.client.http.InvokeClient} 的同步实现。
 *
 * @author 季聿阶
 * @since 2024-02-17
 */
public class SyncInvokeClient extends AbstractInvokeClient {
    public SyncInvokeClient(BeanContainer container, WorkerConfig workerConfig, ClientConfig clientConfig) {
        super(container, workerConfig, clientConfig);
    }

    @Override
    public Response requestResponse(@Nonnull Request request) {
        HttpClassicClient client = this.buildHttpClient(request);
        HttpClassicClientRequest clientRequest = this.buildClientRequest(client, request);
        clientRequest.entity(this.buildHttpEntity(clientRequest, request));
        try (HttpClassicClientResponse<Object> clientResponse = client.exchange(clientRequest, request.returnType())) {
            return HttpClientUtils.getResponse(this.getContainer(), request, clientResponse);
        } catch (IOException e) {
            throw new ClientException("Failed to close http classic client.", e);
        }
    }

    @Override
    public CommunicationType support() {
        return CommunicationType.SYNC;
    }
}
