/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.jane.dlock.jdbc.utils;

import modelengine.fitframework.log.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 系统工具类
 *
 * @author 李哲峰
 * @since 2023/12/08
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
