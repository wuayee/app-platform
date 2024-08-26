/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.server.http;

import modelengine.fitframework.conf.runtime.ServerConfig;

/**
 * 表示运行时 {@code 'server.http.'} 前缀的配置项。
 *
 * @author 季聿阶
 * @since 2023-09-10
 */
public interface HttpConfig extends ServerConfig {
    /**
     * 获取巨大消息体的阈值。
     *
     * @return 表示巨大消息体的阈值的 {@code long}。
     */
    long largeBodySize();
}
