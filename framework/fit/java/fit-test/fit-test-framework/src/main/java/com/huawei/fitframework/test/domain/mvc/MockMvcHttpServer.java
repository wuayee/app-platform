/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.mvc;

import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.plugin.RootPlugin;

/**
 * 为模拟 Mvc 测试提供服务端封装。
 *
 * @author 王攀博 w00561424
 * @since 2024-04-09
 */
public class MockMvcHttpServer {
    private final RootPlugin plugin;

    private HttpClassicServer httpClassicServer;

    public MockMvcHttpServer(RootPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 提供查询当前服务端是否启动的状态。
     *
     * @return 表示当前服务端状态的 {@link boolean}。
     */
    public boolean isStart() {
        if (this.httpClassicServer == null) {
            return false;
        }
        return this.httpClassicServer.isStarted();
    }

    /**
     * 启动服务端。
     */
    public void start() {
        this.httpClassicServer = plugin.container()
                .lookup(HttpClassicServer.class)
                .map(BeanFactory::<HttpClassicServer>get)
                .orElseThrow(() -> new IllegalStateException("Failed to start http server."));

        while (!this.httpClassicServer.isStarted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Failed to wait for server to start.");
            }
        }
    }

    /**
     * 停止服务端。
     */
    public void stop() {
        if (this.httpClassicServer == null && !this.isStart()) {
            return;
        }
        this.httpClassicServer.stop();
    }
}
