/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fitframework.flowable.Emitter;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * 本类为app chat 提供sse服务
 *
 * @author 邬涨财
 * @since 2024-07-28
 */
public interface AppChatSseService {
    /**
     * 添加数据发送器.
     *
     * @param instanceId 实例id.
     * @param emitter 数据发送器.
     * @param latch 等待工具
     */
    void addEmitter(String instanceId, Emitter<Object> emitter, CountDownLatch latch);

    /**
     * 删除数据发送器.
     *
     * @param instanceId 实例id.
     */
    void removeEmitter(String instanceId);

    /**
     * 获取数据发送器.
     *
     * @param instanceId 实例id.
     * @return {@link Optional}{@code <}{@link Object}{@code >}对象.
     */
    Optional<Emitter<Object>> getEmitter(String instanceId);

    /**
     * 推送数据到前端.
     *
     * @param instanceId 实例id.
     * @param data 数据.
     */
    void send(String instanceId, Object data);

    /**
     * 推送最后数据到前端.
     *
     * @param instanceId 实例id.
     * @param data 数据.
     */
    void sendLastData(String instanceId, Object data);

    /**
     * 推送数据到前端祖先的流。
     *
     * @param instanceId 实例id.
     * @param data 数据.
     */
    void sendToAncestor(String instanceId, Object data);

    /**
     * 推送数据到前端祖先的流。
     *
     * @param instanceId 实例id.
     * @param data 数据.
     */
    void sendToAncestorLastData(String instanceId, Object data);

    /**
     * 使线程等待
     *
     * @param instanceId 实例id。
     */
    void latchAwait(String instanceId);
}
