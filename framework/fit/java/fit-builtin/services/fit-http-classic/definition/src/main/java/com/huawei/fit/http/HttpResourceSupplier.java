/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http;

/**
 * {@link HttpResource} 的提供者。
 *
 * @author 季聿阶 j00559309
 * @since 2022-08-19
 */
public interface HttpResourceSupplier {
    /**
     * 获取 Http 的资源。
     *
     * @return 表示 Http 资源的 {@link HttpResource}。
     */
    HttpResource httpResource();
}
