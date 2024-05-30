/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.api;

/**
 * DataBus 请求共有接口。
 *
 * @author 王成 w00863339
 * @since 2024-05-27
 */
public interface DataBusRequest {
    /**
     * 返回与本次 IO 相关的内存用户键。
     *
     * @return 表示与本次 IO 请求相关的内存 {@code String}。
     */
    String userKey();
}
