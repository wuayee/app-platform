/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
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
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final AippLogService aippLogService;

    @Fit
    private com.huawei.fit.jober.aipp.genericable.AippRunTimeService aippRunTimeService;

    public AippChatServiceImpl(AippChatMapper aippChatMapper, MetaService metaService,
                               AppBuilderAppMapper appBuilderAppMapper, AippLogService aippLogService) {
        this.aippChatMapper = aippChatMapper;
        this.metaService = metaService;
        this.appBuilderAppMapper = appBuilderAppMapper;
        this.aippLogService = aippLogService;
    }

    @Override
    public QueryChatRsp createChat(CreateChatRequest body, OperationContext context) {
        if (body.getOriginApp() != null) {
            String chatId = UUIDUtil.uuid();
            body.setChatId(chatId);
        }
        Map<String, Object> initContext = body.getInitContext();
        Map<String, Object> result = (Map<String, Object>) initContext.get("initContext");
        if (result.get("Question") == null) {
            throw new IllegalArgumentException("Question is not null");
        }
        String chatName = result.get("Question").toString();
        String originChatId = UUIDUtil.uuid();
        AppBuilderAppPO appInfo = this.convertAippToApp(body.getAippId(), body.getAippVersion(), context);
        return QueryChatRsp.builder()
                .appId(appInfo.getId())
                .aippVersion(body.getAippVersion())
                .aippId(body.getAippId())
                .chatName(chatName)
                .chatId(body.getChatId())
                .originChatId(originChatId)
                .msgId(this.persistChat(body, context, originChatId, chatName))
                .version(appInfo.getVersion())
                .massageList(new ArrayList<>())
                .updateTime(LocalDateTime.now().toString())
                .build();
    }

    private String persistChat(CreateChatRequest body, OperationContext context,
                String chatId, String unCutChatName) {
        String chatName = (unCutChatName.length() > 64) ? unCutChatName.substring(0, 32) : unCutChatName;
        String instId = this.aippRunTimeService.createAippInstance(body.getAippId(),
                body.getAippVersion(), body.getInitContext(), context);
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("instId", instId);
        if (body.getOriginApp() != null) {
            attributesMap.put("originApp", body.getOriginApp());
            this.persistOriginAppChat(body, context, chatId, chatName, instId);
        }
        AppBuilderAppPO appInfo = this.convertAippToApp(body.getAippId(), body.getAippVersion(), context);
        ChatInfo chatInfo = ChatInfo.builder()
                .appId(appInfo.getId())
                .version(appInfo.getVersion())
                .attributes(JsonUtils.toJsonString(attributesMap))
                .chatId(body.getChatId() == null ? chatId : body.getChatId())
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
                .chatId(body.getChatId() == null ? chatId : body.getChatId())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        this.aippChatMapper.insertChat(chatInfo);
        this.aippChatMapper.insertWideRelationship(wideRelationInfo);
        return instId;
    }

    private void persistOriginAppChat(CreateChatRequest body, OperationContext context,
                String chatId, String chatName, String instId) {
        // @应用对话，插入主应用记录
        Map<String, String> attributesMapOrigin = new HashMap<>();
        attributesMapOrigin.put("instId", instId);
        ChatInfo chatInfoOrigin = ChatInfo.builder()
                .appId(body.getOriginApp())
                .version(body.getOriginAppVersion())
                .attributes(JsonUtils.toJsonString(attributesMapOrigin))
                .chatId(chatId)
                .chatName(chatName)
                .status(AippConst.CHAT_STATUS)
                .updater(context.getName())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .creator(context.getName())
                .build();
        ChatAndInstanceMap wideRelationInfoOrigin = ChatAndInstanceMap.builder()
                .msgId(UUIDUtil.uuid())
                .instanceId(instId)
                .chatId(chatId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        this.aippChatMapper.insertChat(chatInfoOrigin);
        this.aippChatMapper.insertWideRelationship(wideRelationInfoOrigin);
    }

    private AppBuilderAppPO convertAippToApp(String aippId, String appVersion, OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, appVersion, context);
        String appId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        return this.appBuilderAppMapper.selectWithId(appId);
    }

    private QueryChatRequest buildQueryChatRequest(QueryChatRequest body, OperationContext context) {
        QueryChatRequest request = QueryChatRequest.builder().build();
        if (body.getAippId() != null && body.getAippVersion() != null) {
            AppBuilderAppPO appBuilderAppPO = this.convertAippToApp(body.getAippId(), body.getAippVersion(), context);
            request.setAppId(appBuilderAppPO.getId());
            request.setAppVersion(appBuilderAppPO.getVersion());
            return request;
        }
        if (body.getAppId() != null && body.getAppVersion() != null) {
            request.setAppId(body.getAppId());
            request.setAppVersion(body.getAppVersion());
        }
        return request;
    }

    @Override
    public QueryChatRsp queryChat(QueryChatRequest body, String chatId, OperationContext context) {
        QueryChatRsp rsp = new QueryChatRsp();
        QueryChatRequest request = this.buildQueryChatRequest(body, context);
        List<QueryChatRsp> chatResult = this.aippChatMapper.selectChatList(request, chatId);
        if (chatResult != null && chatResult.size() > 0 && chatResult.get(0) != null) {
            rsp = chatResult.get(0);
        } else {
            return rsp;
        }
        List<ChatDto> result = this.aippChatMapper.selectChat(chatId, body.getOffset(), body.getLimit());
        getChatAppInfo(result, body.getAippId(), context);
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
                    .appName(chat.getAppName())
                    .appIcon(chat.getAppIcon())
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

    private void getChatAppInfo(List<ChatDto> chatList, String originAippId, OperationContext context) {
        // 620出包需要 与logService的getAippLogWithAppInfo逻辑雷同 后续要整改
        List<String> atAippIds = chatList.stream()
                .filter(data -> !Objects.equals(data.getAippId(), originAippId))
                .map(ChatDto::getAippId)
                .collect(Collectors.toList());
        RangedResultSet<Meta> metas =
                metaService.list(this.buildAippIdFilter(atAippIds), true, 0, atAippIds.size(), context);
        if (!metas.getResults().isEmpty()) {
            List<Meta> meta = metas.getResults();
            Map<String, Meta> metaMap = meta.stream().collect(Collectors.toMap(Meta::getId, Function.identity()));
            chatList.stream().forEach(data -> setChatAppInfoWithMetaMap(metaMap, data));
        }
    }

    private void setChatAppInfoWithMetaMap(Map<String, Meta> metaMap, ChatDto chat) {
        if (!metaMap.containsKey(chat.getAippId())) {
            return;
        }
        Meta meta = metaMap.get(chat.getAippId());
        chat.setAppName(meta.getName());
        Object icon = meta.getAttributes().get("meta_icon");
        if (icon instanceof String) {
            chat.setAppIcon((String) icon);
        }
    }

    private MetaFilter buildAippIdFilter(List<String> aippIds) {
        MetaFilter filter = new MetaFilter();
        filter.setMetaIds(aippIds);
        return filter;
    }

    @Override
    public List<QueryChatRsp> queryChatList(QueryChatRequest body, OperationContext context) {
        QueryChatRequest request = this.buildQueryChatRequest(body, context);
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
    public QueryChatRsp updateChat(String originChatId, CreateChatRequest body, OperationContext context) {
        List<QueryChatRsp> chatResult = this.aippChatMapper.selectChatList(null, originChatId);
        if (chatResult == null || chatResult.size() == 0 || chatResult.get(0) == null) {
            throw new IllegalArgumentException("chatId is not exist");
        }
        // 只查询该应用的近次记录
        Map<String, Object> bodyContext = body.getInitContext();
        bodyContext.put("chatId", originChatId);
        body.setInitContext(bodyContext);
        Map<String, Object> result = (Map<String, Object>) bodyContext.get("initContext");
        if (result.get("Question") == null) {
            throw new IllegalArgumentException("Question is not null");
        }
        if (body.getOriginApp() != null && body.getChatId() == null) {
            // 首次@应用对话
            String chatId = UUIDUtil.uuid();
            body.setChatId(chatId);
        }
        String chatName = result.get("Question").toString();
        this.persistChat(body, context, originChatId, chatName);
        QueryChatRequest queryBody = QueryChatRequest.builder()
                .aippId(body.getAippId())
                .aippVersion(body.getAippVersion())
                .offset(0)
                .limit(10)
                .build();
        return body.getChatId() == null
                ? queryChat(queryBody, originChatId, context) : queryChat(queryBody, body.getChatId(), context);
    }

    @Override
    public QueryChatRsp restartChat(String currentInstanceId, CreateChatRequest body,
            OperationContext context) {
        String chatId = this.aippChatMapper.selectChatIdByInstanceId(currentInstanceId);
        if (StringUtils.isEmpty(chatId)) {
            throw new IllegalArgumentException(StringUtils.format("The instance id {0} does not match any chat id.",
                    currentInstanceId));
        }
        this.aippChatMapper.deleteWideRelationshipByInstanceId(currentInstanceId);
        this.aippLogService.deleteInstanceLog(currentInstanceId);
        return this.updateChat(chatId, body, context);
    }
}
