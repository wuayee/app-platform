/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows;

/**
 * 除了waterflow自身的驱动，waterflow允许外部数据实现InterStream接口
 * 给waterflow响应式灌入外部数据
 *
 * @author 夏斐
 * @since 2024/02/17
 */
public interface InterStream<T> {
    /**
     * 注册监听
     *
     * @param handler handler
     */
    void register(InterStreamHandler<T> handler);

    /**
     * 单个publish数据
     *
     * @param data data
     * @param id id
     */
    void publish(T data, String id);

    /**
     * 批量publish数据
     *
     * @param data data
     * @param id id
     */
    void publish(T[] data, String id);
}
