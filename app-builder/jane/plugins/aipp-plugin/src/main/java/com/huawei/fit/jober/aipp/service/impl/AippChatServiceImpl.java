/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.chat.ChatDto;
import com.huawei.fit.jober.aipp.dto.chat.CreateChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.MessageInfo;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.entity.ChatAndInstanceMap;
import com.huawei.fit.jober.aipp.entity.ChatInfo;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.mapper.AippChatMapper;
import com.huawei.fit.jober.aipp.mapper.AippLogMapper;
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.po.AppBuilderAppPO;
import com.huawei.fit.jober.aipp.service.AippChatService;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaUtils;
import com.huawei.fit.jober.aipp.util.UUIDUtil;
import com.huawei.fit.jober.aipp.vo.AippLogVO;
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
    private static final String NORMAL_CHAT = "normal";
    private static final String FROM_OTHER_CHAT = "fromOtherApp";

    private final AippChatMapper aippChatMapper;
    private final MetaService metaService;
    private final AppBuilderAppMapper appBuilderAppMapper;
    private final AippLogService aippLogService;
    private final AippLogMapper aippLogMapper;

    @Fit
    private com.huawei.fit.jober.aipp.genericable.AippRunTimeService aippRunTimeService;

    public AippChatServiceImpl(AippChatMapper aippChatMapper, MetaService metaService,
                               AppBuilderAppMapper appBuilderAppMapper, AippLogService aippLogService,
                               AippLogMapper aippLogMapper) {
        this.aippChatMapper = aippChatMapper;
        this.metaService = metaService;
        this.appBuilderAppMapper = appBuilderAppMapper;
        this.aippLogService = aippLogService;
        this.aippLogMapper = aippLogMapper;
    }

    @Override
    public QueryChatRsp createChat(CreateChatRequest body, OperationContext context) {
        if (body.getOriginApp() != null) {
            String chatId = UUIDUtil.uuid();
            body.setChatId(chatId);
        }
        Map<String, Object> initContext = body.getInitContext();
        Map<String, Object> result = (Map<String, Object>) initContext.get("initContext");
        String chatName = getChatName(result);
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
            attributesMap.put("originAppVersion", body.getOriginAppVersion());
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
        Map<String, Object> result = ObjectUtils.cast(bodyContext.get(AippConst.BS_INIT_CONTEXT_KEY));
        if (body.getOriginApp() != null && body.getChatId() == null) {
            // 首次@应用对话
            String chatId = UUIDUtil.uuid();
            body.setChatId(chatId);
        }
        String chatName = getChatName(result);
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
    public QueryChatRsp restartChat(String currentInstanceId, Map<String, Object> additionalContext,
            OperationContext context) {
        String path = this.aippLogService.getParentPath(currentInstanceId);
        AippLogVO aippLogVO = AippLogVO.builder().path(path).build();
        String parentInstanceId = aippLogVO.getAncestors().get(0);
        if (StringUtils.isEmpty(parentInstanceId)) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The instance id {0} does not match ant parentInstanceId.", currentInstanceId));
        }
        List<String> chatIds = this.aippChatMapper.selectChatIdByInstanceId(parentInstanceId);
        if (chatIds.isEmpty()) {
            throw new IllegalArgumentException(StringUtils.format("The instance id {0} does not match any chat id.",
                    parentInstanceId));
        }
        List<QueryChatRsp> chatList = this.aippChatMapper.selectChatListByChatIds(chatIds);
        String chatId;
        String chatType = this.getChatType(chatList.size());
        CreateChatRequest body = this.buildChatBody(parentInstanceId, additionalContext);
        if (chatType == NORMAL_CHAT) {
            chatId = chatList.get(0).getChatId();
        } else if (chatType == FROM_OTHER_CHAT) {
            chatId = this.buildChatBodyWhenAtApp(chatList, body, chatIds);
        } else {
            throw new IllegalArgumentException(StringUtils.format(
                    "The chat ids {0} match illegal num of chat sessions.", chatIds));
        }
        this.aippChatMapper.deleteWideRelationshipByInstanceId(parentInstanceId);
        this.aippLogService.deleteInstanceLog(parentInstanceId);
        return this.updateChat(chatId, body, context);
    }

    private String getChatType(int chatNum) {
        if (chatNum == 1) {
            return NORMAL_CHAT;
        } else if (chatNum == 2) {
            return FROM_OTHER_CHAT;
        } else {
            return StringUtils.EMPTY;
        }
    }

    private CreateChatRequest buildChatBody(String instanceId, Map<String, Object> additionalContext) {
        // 构造updateChat需要的body
        List<AippInstLog> aippInstLogs = this.aippLogMapper.getLogsByInstanceId(instanceId);
        List<AippInstLog> questionAippInstLogs = aippInstLogs.stream()
                .filter(item -> StringUtils.equals(item.getLogType(), AippInstLogType.QUESTION.name()))
                .collect(Collectors.toList());
        AippInstLog questionAippInstLog = questionAippInstLogs.get(0);
        Map<String, Object> initContext = new HashMap<>();
        String question = ObjectUtils.cast(JsonUtils.parseObject(questionAippInstLog.getLogData()).get("msg"));
        additionalContext.put(AippConst.BS_AIPP_QUESTION_KEY, question);
        initContext.put(AippConst.BS_INIT_CONTEXT_KEY, additionalContext);
        return CreateChatRequest.builder()
                .aippId(questionAippInstLog.getAippId())
                .aippVersion(questionAippInstLog.getVersion())
                .initContext(initContext)
                .build();
    }

    private String buildChatBodyWhenAtApp(List<QueryChatRsp> chatList, CreateChatRequest body, List<String> chatIds) {
        String chatId;
        QueryChatRsp firstChat = chatList.get(0);
        QueryChatRsp secondChat = chatList.get(1);
        Map<String, Object> firstChatMap = JsonUtils.parseObject(firstChat.getAttributes());
        Map<String, Object> secondChatMap = JsonUtils.parseObject(secondChat.getAttributes());
        String firstOriginApp = ObjectUtils.cast(firstChatMap.get("originApp"));
        String secondOriginApp = ObjectUtils.cast(secondChatMap.get("originApp"));
        if (!this.validateChatSessionWhenAtApp(firstOriginApp, secondOriginApp)) {
            throw new IllegalArgumentException(StringUtils.format("The chat ids {0} chat sessions are illegal.",
                    chatIds));
        } else if (firstOriginApp != null) {
            chatId = this.buildChatBodyWithOriginApp(firstOriginApp, firstChatMap, firstChat, secondChat, body);
        } else {
            chatId = this.buildChatBodyWithOriginApp(secondOriginApp, secondChatMap, secondChat, firstChat, body);
        }
        return chatId;
    }

    private boolean validateChatSessionWhenAtApp(String firstOriginApp, String secondOriginApp) {
        if ((firstOriginApp != null && secondOriginApp != null)
                || (firstOriginApp == null && secondOriginApp == null)) {
            return false;
        }
        return true;
    }

    private String buildChatBodyWithOriginApp(String originApp, Map<String, Object> chatMap, QueryChatRsp chat,
            QueryChatRsp originChat, CreateChatRequest body) {
        body.setOriginApp(originApp);
        body.setOriginAppVersion(ObjectUtils.cast(chatMap.get("originAppVersion")));
        body.setChatId(chat.getChatId());
        return originChat.getChatId();
    }

    private String getChatName(Map<String, Object> initContext) {
        String chatName;
        if (initContext.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY)) {
            Object data = initContext.get(AippConst.BS_AIPP_FILE_DESC_KEY);
            if (!(data instanceof Map)) {
                throw new AippException(AippErrCode.DATA_TYPE_IS_NOT_SUPPORTED, data.getClass().getName());
            }
            Map<String, String> fileDesc = ObjectUtils.cast(data);
            chatName = fileDesc.get("file_name");
        } else if (initContext.containsKey(AippConst.BS_AIPP_QUESTION_KEY)) {
            chatName = initContext.get(AippConst.BS_AIPP_QUESTION_KEY).toString();
        } else {
            throw new IllegalArgumentException("Chat has no question.");
        }
        return chatName;
    }
}
