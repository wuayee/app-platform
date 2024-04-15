/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.util;

import com.huawei.fitframework.test.domain.mvc.MockMvc;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 为模拟 {@link MockMvc} 提供常见的公用方法。
 *
 * @author 王攀博 w00561424
 * @since 2024-04-09
 */
public class TestUtil {
    /**
     * 获取当前设备可用的端口。
     *
     * @return 返回当前设备可用的端口号 {@link int}。
     */
    public static int getLocalAvailablePort() {
        int port = 0;
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            try {
                port = serverSocket.getLocalPort();
            } finally {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Get local available port failed.");
        }
        return port;
    }
}
