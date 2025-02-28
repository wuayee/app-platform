/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.utils;

import modelengine.fitframework.log.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 系统工具类
 *
 * @author 晏钰坤
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
