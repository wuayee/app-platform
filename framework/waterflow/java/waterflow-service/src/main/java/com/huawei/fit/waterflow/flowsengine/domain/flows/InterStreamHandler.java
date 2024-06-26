/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows;

/**
 * InterStreamHandler
 *
 * @author z00544938
 * @since 2024/01/22
 */
public interface InterStreamHandler<D> {
    /**
     * handle
     *
     * @param data data
     * @param token token
     */
    void handle(D data, String token);

    /**
     * handle
     *
     * @param data data
     * @param token token
     */
    void handle(D[] data, String token);

    /**
     * offer
     *
     * @param streamPublisher streamPublisher
     */
    default void offer(InterStream streamPublisher) {
        streamPublisher.register(this);
    }
}
