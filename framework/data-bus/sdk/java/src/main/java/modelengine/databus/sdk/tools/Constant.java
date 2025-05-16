/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.tools;

/**
 * DataBus 相关常量类。
 *
 * @author 王成
 * @since 2024-03-17
 */
public class Constant {
    /**
     * 信息头部长度。
     */
    public static final int DATABUS_SERVICE_HEADER_SIZE = 32;

    /**
     * 接收缓冲区长度。
     */
    public static final int BUFFER_SIZE = 2048;

    /**
     * 默认回复等待时间。
     */
    public static final long DEFAULT_WAITING_TIME_SECOND = 30L;

    /**
     * 默认连接 DataBus 主服务等待时间。
     */
    public static final int DEFAULT_WAITING_TIME_CONNECT_MILLIS = 3000;
}
