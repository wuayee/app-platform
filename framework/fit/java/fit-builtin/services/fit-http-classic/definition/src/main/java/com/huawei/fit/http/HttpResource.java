/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.http;

import com.huawei.fitframework.value.ValueFetcher;

/**
 * 表示 Http 的资源。
 *
 * @author 季聿阶
 * @since 2022-11-22
 */
public interface HttpResource {
    /**
     * 获取序列化器的集合。
     *
     * @return 表示序列化器集合的 {@link Serializers}。
     */
    Serializers serializers();

    /**
     * 获取求值器。
     *
     * @return 表示求值器的 {@link ValueFetcher}。
     */
    ValueFetcher valueFetcher();
}
