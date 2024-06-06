/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.UUIDUtil;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.chat.ChatDto;
import com.huawei.fit.jober.aipp.dto.chat.CreateChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.MessageInfo;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.entity.ChatAndInstanceMap;
import com.huawei.fit.jober.aipp.entity.ChatInfo;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.mapper.AippChatMapper;
import com.huawei.fit.jober.aipp.service.AippChatService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 历史会话时服务接口.
 *
 * @author z00559346 张越
 * @since 2024-05-25
 */
@Component
public class AippChatServiceImpl implements AippChatService {
    private final AippChatMapper aippChatMapper;

    @Fit
    private com.huawei.fit.jober.aipp.genericable.AippRunTimeService aippRunTimeService;

    public AippChatServiceImpl(AippChatMapper aippChatMapper) {
        this.aippChatMapper = aippChatMapper;
    }

    @Override
    public QueryChatRsp createChat(CreateChatRequest body, OperationContext context) {
        String chatId = UUIDUtil.uuid();
        String instId = persistChat(body, context, chatId);
        Map<String, Object> initContext = body.getInitContext();
        Map<String, Object> result = (Map<String, Object>) initContext.get("initContext");
        String chatName = (result.get("Question").toString().length() < AippConst.CHAT_NAME_LENGTH)
                ? result.get("Question").toString()
                : result.get("Question").toString().substring(0, AippConst.CHAT_NAME_LENGTH);
        return QueryChatRsp.builder()
                .aippId(body.getAippId())
                .chatName(chatName)
                .chatId(chatId)
                .msgId(instId)
                .version(body.getVersion())
                .massageList(new ArrayList<>())
                .updateTime(LocalDateTime.now().toString())
                .build();
    }

    private String persistChat(CreateChatRequest body, OperationContext context, String chatId) {
        String instId = aippRunTimeService.createAippInstance(body.getAippId(),
                body.getVersion(), body.getInitContext(), context);
        Map<String, Object> initContext = body.getInitContext();
        Map<String, Object> result = (Map<String, Object>) initContext.get("initContext");
        String chatName = (result.get("Question").toString().length() < AippConst.CHAT_NAME_LENGTH)
                ? result.get("Question").toString()
                : result.get("Question").toString().substring(0, AippConst.CHAT_NAME_LENGTH);
        ChatInfo chatInfo = ChatInfo.builder()
                .aippId(body.getAippId())
                .version(body.getVersion())
                .attributes(instId)
                .chatId(chatId)
                .chatName(chatName)
                .status(AippConst.CHAT_STATUS)
                .updater(context.getName())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .creator(context.getName())
                .build();
        ChatAndInstanceMap wideRelationInfo = ChatAndInstanceMap.builder()
                .msgId(UUIDUtil.uuid())
                .instanceId(instId)
                .chatId(chatId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        aippChatMapper.insertChat(chatInfo);
        aippChatMapper.insertWideRelationship(wideRelationInfo);
        return instId;
    }

    @Override
    public QueryChatRsp queryChat(QueryChatRequest body, String chatId, OperationContext context) {
        QueryChatRsp rsp = new QueryChatRsp();
        List<QueryChatRsp> chatResult = aippChatMapper.selectChatList(body, chatId);
        if (chatResult != null && chatResult.size() > 0 && chatResult.get(0) != null) {
            rsp = chatResult.get(0);
        } else {
            return rsp;
        }
        List<ChatDto> result = aippChatMapper.selectChat(chatId, body.getOffset(), body.getLimit());
        ArrayList msgList = new ArrayList<>();
        result.forEach((chat) -> {
            AippLogData data = JsonUtils.parseObject(chat.getLogData(), AippLogData.class);
            String content = data.getMsg();
            MessageInfo messageInfo = MessageInfo.builder()
                    .contentType(0)
                    .content(Arrays.asList(new String[]{content}))
                    .role((AippInstLogType.QUESTION.name().equals(chat.getLogType())) ? "USER" : "SYSTEM")
                    .createTime(chat.getCreateTime())
                    .msgId(chat.getMsgId())
                    .build();
            msgList.add(messageInfo);
        });
        Integer total = aippChatMapper.countChat(chatId);
        rsp.setTotal(total * 2);
        rsp.setMassageList(msgList);
        return rsp;
    }

    @Override
    public List<QueryChatRsp> queryChatList(QueryChatRequest body, OperationContext context) {
        List<QueryChatRsp> result = aippChatMapper.selectChatList(body, null);
        result.forEach((chat) -> {
            chat.setUpdateTimeStamp(Timestamp.valueOf(chat.getUpdateTime()).getTime());
            chat.setCurrentTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
            chat.setRecentInfo("暂无新消息");
            String log = aippChatMapper.selectMsgByInstance(chat.getMsgId());
            if (log != null) {
                AippLogData data = JsonUtils.parseObject(log, AippLogData.class);
                chat.setRecentInfo(data.getMsg());
            }
        });
        return result;
    }

    @Override
    public Void deleteChat(String chatId, String appId, OperationContext context) {
        if (appId != null && !appId.isEmpty()) {
            aippChatMapper.deleteApp(appId);
            return null;
        }
        aippChatMapper.deleteChat(chatId);
        return null;
    }

    @Override
    public QueryChatRsp updateChat(String chatId, CreateChatRequest body, OperationContext context) {
        List<QueryChatRsp> chatResult = aippChatMapper.selectChatList(null, chatId);
        if (chatResult == null || chatResult.size() == 0 || chatResult.get(0) == null) {
            throw new IllegalArgumentException("chatId is not exist");
        }
        // 只查询该应用的近次记录
        Map<String, Object> bodyContext = body.getInitContext();
        bodyContext.put("chatId", chatId);
        body.setInitContext(bodyContext);
        persistChat(body, context, chatId);
        QueryChatRequest queryBody = QueryChatRequest.builder()
                .offset(0)
                .limit(10)
                .build();
        return queryChat(queryBody, chatId, context);
    }
}
