/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.common;

import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.entity.ChatSession;

/**
 * 应用执行接口.
 *
 * @author 张越
 * @since 2025-01-10
 */
public interface AppTaskRunnable {
    /**
     * 运行任务.
     *
     * @param context 上下文信息.
     */
    void run(RunContext context);

    /**
     * 运行任务.
     *
     * @param context 上下文信息.
     * @param chatSession 会话对象.
     */
    void run(RunContext context, ChatSession<Object> chatSession);
}
