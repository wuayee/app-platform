/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.chat.CreateChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;

import java.util.List;
import java.util.Map;

/**
 * 历史会话服务接口.
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
public interface AippChatService {
    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param context 上下文
     * @return 表示会话相应体的 {@link QueryChatRsp}。
     */
    QueryChatRsp createChat(CreateChatRequest body, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param chatId 会话ID
     * @param context 上下文
     * @return 表示会话相应体的 {@link QueryChatRsp}。
     */
    QueryChatRsp queryChat(QueryChatRequest body, String chatId, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param context 上下文
     * @return 表示会话相应体列表 {@link QueryChatRsp}。List<QueryChatRsp>
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
     * @param originChatId 主应用会话ID
     * @param body 请求体
     * @param context 上下文
     * @return 表示会话相应体的 {@link QueryChatRsp}。
     */
    QueryChatRsp updateChat(String originChatId, CreateChatRequest body, OperationContext context);

    /**
     * 重新发起会话。
     *
     * @param currentInstanceId 需要重新发起会话的实例 ID。
     * @param additionalContext 重新会话需要的信息，如是否使用多轮对话等等。
     * @param context 上下文。
     * @return 表示会话相应体的 {@link QueryChatRsp}。
     */
    QueryChatRsp restartChat(String currentInstanceId, Map<String, Object> additionalContext, OperationContext context);
}
