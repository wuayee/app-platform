/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.http.websocket.Session;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.service.AippStreamService;
import com.huawei.fitframework.annotation.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流式服务实现，单进程实现方案.
 *
 * @author z00559346 张越
 * @since 2024-05-14
 */
@Component
public class AippStreamServiceImpl implements AippStreamService {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void addSession(String instanceId, Session session) {
        this.sessions.put(instanceId, session);
    }

    @Override
    public void removeSession(Session session) {
        // 保证原子性.
        this.sessions.replaceAll((s, ss) -> {
            if (ss.getId().equals(session.getId())) {
                return null;
            }
            return ss;
        });
    }

    @Override
    public Optional<Session> getSession(String instanceId) {
        return Optional.ofNullable(this.sessions.get(instanceId));
    }

    @Override
    public void send(String instanceId, Object data) {
        if (Objects.isNull(data)) {
            return;
        }
        Session session = this.sessions.get(instanceId);
        Optional.ofNullable(session).ifPresent(s -> s.send(JsonUtils.toJsonString(data)));
    }
}
