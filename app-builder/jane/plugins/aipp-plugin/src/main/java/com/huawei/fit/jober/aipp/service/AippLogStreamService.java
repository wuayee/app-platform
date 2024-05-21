package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.http.websocket.Session;
import com.huawei.fit.jober.aipp.vo.AippLogVO;

import java.util.Optional;

/**
 * AippLog流式服务接口.
 *
 * @author z00559346 张越
 * @since 2024-05-14
 */
public interface AippLogStreamService {
    /**
     * 添加session.
     *
     * @param instanceId 实例id.
     * @param session websocket会话对象.
     */
    void addSession(String instanceId, Session session);

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
     * 推送日志信息到前端.
     *
     * @param log 日志对象.
     */
    void send(AippLogVO log);
}
