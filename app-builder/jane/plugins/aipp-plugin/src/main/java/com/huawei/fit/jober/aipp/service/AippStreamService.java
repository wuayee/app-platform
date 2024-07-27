/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.http.websocket.Session;

import java.util.Optional;

/**
 * 流式服务接口.
 *
 * @author z00559346 张越
 * @since 2024-05-14
 */
public interface AippStreamService {
    /**
     * 添加session.
     *
     * @param instanceId 实例id.
     * @param session websocket会话对象.
     */
    void addSession(String instanceId, Session session);

    /**
     * 添加session.
     *
     * @param session websocket会话对象.
     */
    void addSession(Session session);

    /**
     * 删除session.
     *
     * @param session {@link Session} 对象.
     */
    void removeSession(Session session);

    /**
     * 获取session.
     *
     * @param instanceId 实例id.
     * @return {@link Optional}{@code <}{@link Session}{@code >}对象.
     */
    Optional<Session> getSession(String instanceId);

    /**
     * 通过会话id获取session.
     *
     * @param sessionId 会话id.
     * @return {@link Optional}{@code <}{@link Session}{@code >}对象.
     */
    Optional<Session> getSessionById(String sessionId);

    /**
     * 推送数据到前端.
     *
     * @param instanceId 实例id.
     * @param data 数据.
     */
    void send(String instanceId, Object data);

    /**
     * 推送数据到前端祖先的流。
     *
     * @param instanceId 实例id.
     * @param data 数据.
     */
    void sendToAncestor(String instanceId, Object data);
}
