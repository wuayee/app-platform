/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support.http.server;

import com.huawei.fit.http.server.HttpClassicServer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.runtime.FitStarter;
import com.huawei.fitframework.util.ThreadUtils;

/**
 * 为测试 Http 提供的服务运行时。
 *
 * @author 王攀博 w00561424
 * @since 2024-06-15
 */
public class RuntimeForServer {
    private volatile boolean isStarted = false;

    private final Class<?> entry;
    private FitRuntime runtime;

    public RuntimeForServer(Class<?> entry) {
        this.entry = entry;
    }

    /**
     * 表示启动 Http 客户端。
     *
     * @param port 表示可用的端口 {@link int}。
     */
    public void start(int port) {
        if (this.isStarted) {
            return;
        }
        this.runtime = FitStarter.start(this.entry, new String[] {"server.http.port=" + port});
        HttpClassicServer httpClassicServer = runtime.root()
                .container()
                .lookup(HttpClassicServer.class)
                .map(BeanFactory::<HttpClassicServer>get)
                .orElseThrow(() -> new IllegalStateException("Failed to start http server."));
        while (!httpClassicServer.isStarted()) {
            ThreadUtils.sleep(0);
        }
        this.isStarted = true;
    }

    /**
     * 表示用于关闭 Http 客户端。
     */
    public void stop() {
        HttpClassicServer httpClassicServer = this.runtime.root()
                .container()
                .lookup(HttpClassicServer.class)
                .map(BeanFactory::<HttpClassicServer>get)
                .orElseThrow(() -> new IllegalStateException("Failed to stop http server."));
        httpClassicServer.stop();
        this.isStarted = false;
    }
}
