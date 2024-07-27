/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRsp;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;

/**
 * 历史会话服务接口.
 *
 * @author 姚江 yWX1299574
 * @since 2024-07-23
 */
public interface AppChatService {
    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param context 上下文
     * @return 表示会话相应体的 {@link QueryChatRsp}。
     */
    CreateAppChatRsp chat(CreateAppChatRequest body, OperationContext context);
}
