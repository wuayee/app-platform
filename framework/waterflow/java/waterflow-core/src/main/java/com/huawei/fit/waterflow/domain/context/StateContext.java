/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.context;

/**
 * 节点的上下文
 *
 * @since 1.0
 */
public interface StateContext {
    /**
     * 获取指定key的上下文数据
     *
     * @param key 指定key
     * @return 上下文数据
     */
    <R> R getState(String key);

    /**
     * 设置上下文数据
     *
     * @param key 指定key
     * @param value 待设置的上下文数据
     */
    void setState(String key, Object value);
}
