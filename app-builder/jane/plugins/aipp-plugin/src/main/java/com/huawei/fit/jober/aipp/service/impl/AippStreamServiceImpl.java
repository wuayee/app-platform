/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.http.websocket.Session;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AippStreamService;
import com.huawei.fit.jober.aipp.util.AippLogUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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

    private final Map<String, Session> idToSessions = new ConcurrentHashMap<>();

    private final Map<String, List<String>> sessionIdToInstId = new ConcurrentHashMap<>();

    private final AippLogService logService;

    public AippStreamServiceImpl(AippLogService logService) {
        this.logService = logService;
    }

    @Override
    public void addSession(String instanceId, Session session) {
        this.sessions.put(instanceId, session);
        this.sessionIdToInstId.computeIfAbsent(session.getId(), k -> new ArrayList<>()).add(instanceId);
    }

    @Override
    public void addSession(Session session) {
        this.idToSessions.put(session.getId(), session);
    }

    @Override
    public void removeSession(Session session) {
        // 保证原子性.
        this.sessionIdToInstId.getOrDefault(session.getId(), new ArrayList<>()).forEach(this.sessions::remove);
        this.sessionIdToInstId.remove(session.getId());
        this.idToSessions.remove(session.getId(), session);
    }

    @Override
    public Optional<Session> getSession(String instanceId) {
        return Optional.ofNullable(this.sessions.get(instanceId));
    }

    @Override
    public Optional<Session> getSessionById(String sessionId) {
        return Optional.ofNullable(this.idToSessions.get(sessionId));
    }

    @Override
    public void send(String instanceId, Object data) {
        if (Objects.isNull(data)) {
            return;
        }
        Session session = this.sessions.get(instanceId);
        Optional.ofNullable(session).ifPresent(s -> s.send(JsonUtils.toJsonString(data)));
    }

    @Override
    public void sendToAncestor(String instanceId, Object data) {
        String processedInstanceId = instanceId;
        String path = this.logService.getParentPath(processedInstanceId);
        if (StringUtils.isNotEmpty(path)) {
            processedInstanceId = path.split(AippLogUtils.PATH_DELIMITER)[1];
        }
        this.send(processedInstanceId, data);
    }
}
