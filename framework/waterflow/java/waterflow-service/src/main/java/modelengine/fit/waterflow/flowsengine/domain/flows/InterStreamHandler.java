/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows;

/**
 * InterStreamHandler
 *
 * @author 张群辉
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
