/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.MetaUtils;
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
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.po.AppBuilderAppPO;
import com.huawei.fit.jober.aipp.service.AippChatService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AippChatServiceImpl
 *
 * @since 2024-06-07
 */
@Component
public class AippChatServiceImpl implements AippChatService {
    private final AippChatMapper aippChatMapper;
    private final MetaService metaService;
    private final AppBuilderAppMapper appBuilderAppMapper;

    @Fit
    private com.huawei.fit.jober.aipp.genericable.AippRunTimeService aippRunTimeService;

    public AippChatServiceImpl(AippChatMapper aippChatMapper, MetaService metaService,
                               AppBuilderAppMapper appBuilderAppMapper) {
        this.aippChatMapper = aippChatMapper;
        this.metaService = metaService;
        this.appBuilderAppMapper = appBuilderAppMapper;
    }

    @Override
    public QueryChatRsp createChat(CreateChatRequest body, OperationContext context) {
        String chatId = UUIDUtil.uuid();
        Map<String, Object> initContext = body.getInitContext();
        Map<String, Object> result = (Map<String, Object>) initContext.get("initContext");
        if (result.get("Question") == null) {
            throw new IllegalArgumentException("Question is not null");
        }
        String chatName = result.get("Question").toString();
        String instId = persistChat(body, context, chatId, chatName);
        AppBuilderAppPO appInfo = convertAippToApp(body.getAippId(), body.getAippVersion(), context);
        return QueryChatRsp.builder()
                .appId(appInfo.getId())
                .aippVersion(body.getAippVersion())
                .aippId(body.getAippId())
                .chatName(chatName)
                .chatId(chatId)
                .msgId(instId)
                .version(appInfo.getVersion())
                .massageList(new ArrayList<>())
                .updateTime(LocalDateTime.now().toString())
                .build();
    }

    private String persistChat(CreateChatRequest body, OperationContext context, String chatId, String chatName) {
        String instId = this.aippRunTimeService.createAippInstance(body.getAippId(),
                body.getAippVersion(), body.getInitContext(), context);
        AppBuilderAppPO appInfo = convertAippToApp(body.getAippId(), body.getAippVersion(), context);
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("instId", instId);
        ChatInfo chatInfo = ChatInfo.builder()
                .appId(appInfo.getId())
                .version(appInfo.getVersion())
                .attributes(JsonUtils.toJsonString(attributesMap))
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
        this.aippChatMapper.insertChat(chatInfo);
        this.aippChatMapper.insertWideRelationship(wideRelationInfo);
        return instId;
    }

    private AppBuilderAppPO convertAippToApp(String aippId, String appVersion, OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, appVersion, context);
        String appId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        return this.appBuilderAppMapper.selectWithId(appId);
    }

    private QueryChatRequest buildQueryChatRequest(QueryChatRequest body, OperationContext context) {
        QueryChatRequest request = QueryChatRequest.builder().build();
        if (body.getAippId() != null && body.getAippVersion() != null) {
            AppBuilderAppPO appBuilderAppPO = convertAippToApp(body.getAippId(), body.getAippVersion(), context);
            request.setAppId(appBuilderAppPO.getId());
            request.setAppVersion(appBuilderAppPO.getVersion());
        }
        return request;
    }

    @Override
    public QueryChatRsp queryChat(QueryChatRequest body, String chatId, OperationContext context) {
        QueryChatRsp rsp = new QueryChatRsp();
        QueryChatRequest request = buildQueryChatRequest(body, context);
        List<QueryChatRsp> chatResult = this.aippChatMapper.selectChatList(request, chatId);
        if (chatResult != null && chatResult.size() > 0 && chatResult.get(0) != null) {
            rsp = chatResult.get(0);
        } else {
            return rsp;
        }
        List<ChatDto> result = this.aippChatMapper.selectChat(chatId, body.getOffset(), body.getLimit());
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
        Integer total = this.aippChatMapper.countChat(chatId);
        Map<String, Object> mapTypes = JsonUtils.parseObject(rsp.getMsgId());
        rsp.setMsgId(String.valueOf(mapTypes.get("instId")));
        rsp.setAippId(body.getAippId());
        rsp.setAippVersion(body.getAippVersion());
        rsp.setTotal(total * 2);
        rsp.setMassageList(msgList);
        return rsp;
    }

    @Override
    public List<QueryChatRsp> queryChatList(QueryChatRequest body, OperationContext context) {
        QueryChatRequest request = buildQueryChatRequest(body, context);
        List<QueryChatRsp> result = this.aippChatMapper.selectChatList(request, null);
        result.forEach((chat) -> {
            chat.setUpdateTimeStamp(Timestamp.valueOf(chat.getUpdateTime()).getTime());
            chat.setCurrentTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
            chat.setRecentInfo("暂无新消息");
            Map<String, Object> mapTypes = JsonUtils.parseObject(chat.getMsgId());
            chat.setMsgId(String.valueOf(mapTypes.get("instId")));
            String log = this.aippChatMapper.selectMsgByInstance(chat.getMsgId());
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
            this.aippChatMapper.deleteApp(appId);
            return null;
        }
        this.aippChatMapper.deleteChat(chatId);
        return null;
    }

    @Override
    public QueryChatRsp updateChat(String chatId, CreateChatRequest body, OperationContext context) {
        List<QueryChatRsp> chatResult = this.aippChatMapper.selectChatList(null, chatId);
        if (chatResult == null || chatResult.size() == 0 || chatResult.get(0) == null) {
            throw new IllegalArgumentException("chatId is not exist");
        }
        // 只查询该应用的近次记录
        Map<String, Object> bodyContext = body.getInitContext();
        bodyContext.put("chatId", chatId);
        body.setInitContext(bodyContext);
        Map<String, Object> result = (Map<String, Object>) bodyContext.get("initContext");
        if (result.get("Question") == null) {
            throw new IllegalArgumentException("Question is not null");
        }
        String chatName = result.get("Question").toString();
        persistChat(body, context, chatId, chatName);
        QueryChatRequest queryBody = QueryChatRequest.builder()
                .aippId(body.getAippId())
                .aippVersion(body.getAippVersion())
                .offset(0)
                .limit(10)
                .build();
        return queryChat(queryBody, chatId, context);
    }
}
