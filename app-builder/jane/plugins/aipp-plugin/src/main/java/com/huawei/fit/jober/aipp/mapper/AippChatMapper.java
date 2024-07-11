/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.dto.chat.ChatDto;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;
import com.huawei.fit.jober.aipp.entity.ChatAndInstanceMap;
import com.huawei.fit.jober.aipp.entity.ChatInfo;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话消息Mapper
 *
 * @author z00597222
 * @since 2024-05-29
 */
public interface AippChatMapper {
    /**
     * 插入会话信息.
     *
     * @param info 会话信息
     */
    void insertChat(ChatInfo info);

    /**
     * 插入关系宽表
     *
     * @param info 会话信息
     */
    void insertWideRelationship(ChatAndInstanceMap info);

    /**
     * 查询会话记录
     *
     * @param request 请求体
     * @param chatId  会话ID
     * @return List<QueryChatRsp>
     */
    List<QueryChatRsp> selectChatList(@Param("requestParam") QueryChatRequest request, @Param("chatId") String chatId);

    /**
     * 查询会话
     *
     * @param chatId 会话ID
     * @param offset 起始查询
     * @param limit  查询条数
     * @return List<ChatDto>
     */
    List<ChatDto> selectChat(@Param("chatId") String chatId, @Param("offset") Integer offset,
                             @Param("limit") Integer limit);

    /**
     * 删除会话
     *
     * @param chatId 会话ID
     * @return Integer
     */
    Integer deleteChat(@Param("chatId") String chatId);

    /**
     * 删除APP
     *
     * @param appId appId
     * @return Integer
     */
    Integer deleteApp(@Param("appId") String appId);


    /**
     * 查询实例记录
     *
     * @param chatId 会话ID
     * @param limit  查询条数
     * @return List<String>
     */
    List<String> selectInstanceByChat(String chatId, Integer limit);

    /**
     * selectMsgByInstance
     *
     * @param instanceId InstanceId
     * @return String
     */
    String selectMsgByInstance(String instanceId);

    /**
     * countChat
     *
     * @param chatId chatId
     * @return Integer
     */
    Integer countChat(String chatId);

    /**
     * 根据指定应用ID, 查询最近count个会话ID
     *
     * @param appId 应用Id
     * @param count count
     * @return List<String> 会话ID列表
     */
    List<String> selectChatByAppId(String appId, int count);

    /**
     * 根据指定实例 ID 查询会话 ID。
     *
     * @param instanceId 指定实例 ID。
     * @return 表示会话 ID 的 {@link String}。
     */
    List<String> selectChatIdByInstanceId(String instanceId);

    /**
     * 根据指定实例 ID 删除关系宽表。
     *
     * @param instanceId 指定实例 ID。
     */
    void deleteWideRelationshipByInstanceId(String instanceId);

    /**
     * 根据chatId列表 查询会话
     *
     * @param chatIds 会话ID列表
     * @return List<QueryChatRsp> 会话记录列表
     */
    List<QueryChatRsp> selectChatListByChatIds(@Param("chatIds") List<String> chatIds);
}