/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.util;

import com.huawei.fit.waterflow.domain.context.FlowSession;

import java.util.Optional;

/**
 * {@link FlowSession} 相关工具方法。
 *
 * @author 刘信宏
 * @since 2024-04-22
 */
public class SessionUtils {
    /**
     * 拷贝已有的 {@link FlowSession} 状态数据的引用。
     *
     * @param session 表示已有的流程会话实例信息的 {@link FlowSession}。
     * @return 表示拷贝了 {@code session} 的状态数据引用的 {@link FlowSession}。
     */
    public static FlowSession copyFlowSession(FlowSession session) {
        return Optional.ofNullable(session)
                .map(FlowSession::new)
                .orElseGet(FlowSession::new);
    }
}
