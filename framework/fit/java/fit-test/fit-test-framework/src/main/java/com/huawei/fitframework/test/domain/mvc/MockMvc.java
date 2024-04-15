/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.mvc;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.plugin.RootPlugin;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.fitframework.test.domain.mvc.request.RequestParam;

/**
 * 为测试提供模拟的 MVC。
 *
 * @author 王攀博 w00561424
 * @since 2024-04-09
 */
public final class MockMvc {
    private final MockMvcHttpServer mockMvcHttpServer;
    private final int port;

    public MockMvc(RootPlugin plugin, int port) {
        this.mockMvcHttpServer = new MockMvcHttpServer(plugin);
        this.port = port;
    }

    /**
     * 执行模拟的 http 调用。
     *
     * @param builder 表示构建客户端请求参数的 {@link MockRequestBuilder}
     * @param <T> 表示期待的返回值类型的 {@link T}。
     * @return 表示 Http 响应的 {@link HttpClassicClientResponse}{@code <}{@link T}{@code >}。
     */
    public <T> HttpClassicClientResponse<T> perform(MockRequestBuilder builder) {
        if (!this.mockMvcHttpServer.isStarted()) {
            this.mockMvcHttpServer.waitServerStart();
        }

        HttpClassicClient httpClassicClient = HttpClientFactory.create();
        RequestParam requestParam = builder.port(this.port).client(httpClassicClient).build();
        return httpClassicClient.exchange(requestParam.getRequest(), requestParam.getResponseType());
    }

    /**
     * 测试执行完成的后处理。
     */
    public void afterProcess() {
        this.mockMvcHttpServer.stop();
    }
}