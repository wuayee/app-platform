/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.chat.CreateChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;

import java.util.List;

/**
 * 历史会话服务接口.
 *
 * @author z00597222 翟卉馨
 * @since 2024-05-29
 */
public interface AippChatService {
    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param context 上下文
     * @return QueryChatRsp
     */
    QueryChatRsp createChat(CreateChatRequest body, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param chatId 会话ID
     * @param context 上下文
     * @return QueryChatRsp
     */
    QueryChatRsp queryChat(QueryChatRequest body, String chatId, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param context 上下文
     * @return List<QueryChatRsp>
     */
    List<QueryChatRsp> queryChatList(QueryChatRequest body, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param chatId 会话ID
     * @param appId 应用ID
     * @param context 上下文
     * @return Void
     */
    Void deleteChat(String chatId, String appId, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param chatId 会话ID
     * @param body 请求体
     * @param context 上下文
     * @return QueryChatRsp
     */
    QueryChatRsp updateChat(String chatId, CreateChatRequest body, OperationContext context);
}
