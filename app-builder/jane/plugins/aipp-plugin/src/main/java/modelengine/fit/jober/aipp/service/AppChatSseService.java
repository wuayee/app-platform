/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.entity.ChatSession;

import java.util.Optional;

/**
 * 本类为app chat 提供sse服务
 *
 * @author 邬涨财
 * @since 2024-07-28
 */
public interface AppChatSseService {
    /**
     * 获取数据发送器.
     *
     * @param instanceId 实例id.
     * @return {@link Optional}{@code <}{@link Object}{@code >}对象.
     */
    Optional<ChatSession<Object>> getEmitter(String instanceId);

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
}
