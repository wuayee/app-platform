/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.utils;

import com.huawei.fitframework.log.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 系统工具类
 *
 * @author 00693950
 * @since 2023/7/29
 */
public class HostUtil {
    private static final Logger log = Logger.get(HostUtil.class);

    /**
     * getHostAddress
     *
     * @return String
     */
    public static String getHostAddress() {
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Fail to get host address, error message :{}", e.getMessage());
        }
        return hostAddress;
    }
}
