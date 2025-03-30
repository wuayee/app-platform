/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.OBTAIN_HISTORY_CONVERSATION_FAILED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.RE_CHAT_FAILED;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.dto.chat.ChatDto;
import modelengine.fit.jober.aipp.dto.chat.ChatInfoRspDto;
import modelengine.fit.jober.aipp.dto.chat.CreateChatRequest;
import modelengine.fit.jober.aipp.dto.chat.MessageInfo;
import modelengine.fit.jober.aipp.dto.chat.QueryChatInfoRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRspDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.RestartModeEnum;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.po.MsgInfoPO;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.AippChatService;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.MetaUtils;
import modelengine.fit.jober.aipp.util.UUIDUtil;
import modelengine.fit.jober.aipp.vo.AippLogVO;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private final AppBuilderAppRepository appRepository;

    @Fit
    private modelengine.fit.jober.aipp.genericable.AippRunTimeService aippRunTimeService;

    public AippChatServiceImpl(AippChatMapper aippChatMapper, MetaService metaService,
            AppBuilderAppMapper appBuilderAppMapper, AippLogService aippLogService,
            AppBuilderAppRepository appRepository) {
        this.aippChatMapper = aippChatMapper;
        this.metaService = metaService;
        this.appBuilderAppMapper = appBuilderAppMapper;
        this.aippLogService = aippLogService;
        this.appRepository = appRepository;
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
        AppBuilderAppPo appInfo = this.convertAippToApp(body.getAippId(), body.getAippVersion(), context);
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

    private String persistChat(CreateChatRequest body, OperationContext context, String chatId, String unCutChatName) {
        String chatName = (unCutChatName.length() > 64) ? unCutChatName.substring(0, 32) : unCutChatName;
        String instId = this.aippRunTimeService.createAippInstance(body.getAippId(), body.getAippVersion(),
                body.getInitContext(), context);
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("instId", instId);
        if (body.getOriginApp() != null) {
            attributesMap.put("originApp", body.getOriginApp());
            attributesMap.put("originAppVersion", body.getOriginAppVersion());
            this.persistOriginAppChat(body, context, chatId, chatName, instId);
        }
        AppBuilderAppPo appInfo = this.convertAippToApp(body.getAippId(), body.getAippVersion(), context);
        attributesMap.putIfAbsent(AippConst.ATTR_CHAT_STATE_KEY, appInfo.getState());
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

    private void persistOriginAppChat(CreateChatRequest body, OperationContext context, String chatId, String chatName,
            String instId) {
        // @应用对话，插入主应用记录
        Map<String, String> attributesMapOrigin = new HashMap<>();
        attributesMapOrigin.put("instId", instId);
        AppBuilderAppPo appBuilderAppPO = this.appBuilderAppMapper.selectWithId(body.getOriginApp());
        attributesMapOrigin.put(AippConst.ATTR_CHAT_STATE_KEY, appBuilderAppPO.getState());
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

    private AppBuilderAppPo convertAippToApp(String aippId, String appVersion, OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, appVersion, context);
        if (meta == null) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
        String appId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        return this.appBuilderAppMapper.selectWithId(appId);
    }

    private QueryChatRequest buildQueryHistoryChatRequest(QueryChatRequest body, OperationContext context)
            throws AippTaskNotFoundException {
        QueryChatRequest request = QueryChatRequest.builder().build();
        request.setAppState(body.getAppState());
        request.setLimit(body.getLimit());
        request.setOffset(body.getOffset());
        this.validate(body);
        if (body.getAippId() != null) {
            request.setAippId(body.getAippId());
            return request;
        }
        if (body.getAppId() != null) {
            List<Meta> metas = MetaUtils.getAllMetasByAppId(this.metaService, body.getAppId(), context);
            if (CollectionUtils.isEmpty(metas)) {
                throw new AippTaskNotFoundException(AippErrCode.TASK_NOT_FOUND);
            }
            String aippId = metas.get(0).getId();
            request.setAippId(aippId);
            return request;
        }
        return request;
    }

    /**
     * 判断入参是否合理：当前应用删除时，会有 aippId、aippVersion、appId、appVersion 都为空的场景
     *
     * @param body 表示待校验的入参
     */
    private void validate(QueryChatRequest body) {
        String aippId = body.getAippId();
        String aippVersion = body.getAippVersion();
        String appId = body.getAppId();
        if (StringUtils.isEmpty(aippId) && StringUtils.isEmpty(appId)) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
    }

    @Override
    public QueryChatRsp queryChat(QueryChatRequest body, String chatId, OperationContext context)
            throws AippTaskNotFoundException {
        QueryChatRsp rsp = new QueryChatRsp();
        QueryChatRequest request = this.buildQueryHistoryChatRequest(body, context);
        List<QueryChatRsp> chatResult = this.aippChatMapper.selectChatList(request, chatId, context.getAccount());
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
                    .content(Arrays.asList(new String[] {content}))
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
        RangedResultSet<Meta> metas = metaService.list(this.buildAippIdFilter(atAippIds), true, 0, atAippIds.size(),
                context);
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
    public RangedResultSet<QueryChatRspDto> queryChatList(QueryChatRequest body, OperationContext context) {
        QueryChatRequest request = null;
        try {
            request = this.buildQueryHistoryChatRequest(body, context);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(OBTAIN_HISTORY_CONVERSATION_FAILED);
        }
        List<QueryChatRsp> result = this.aippChatMapper.selectChatList(request, null, context.getAccount());
        List<String> instanceIds = result.stream().map(this::getInstanceId).collect(Collectors.toList());
        List<MsgInfoPO> logs = new ArrayList<>();
        if (!instanceIds.isEmpty()) {
            logs = this.aippChatMapper.selectMsgByInstanceIds(instanceIds);
        }
        Map<String, String> finalLogs = logs.stream()
            .collect(Collectors.toMap(MsgInfoPO::getInstanceId, MsgInfoPO::getLogData));
        result.forEach((chat) -> {
            chat.setUpdateTimeStamp(Timestamp.valueOf(chat.getUpdateTime()).getTime());
            chat.setCurrentTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
            chat.setRecentInfo("暂无新消息");
            String instId = getInstanceId(chat);
            chat.setMsgId(instId);
            String log = finalLogs.get(instId);
            if (log != null) {
                AippLogData data = JsonUtils.parseObject(log, AippLogData.class);
                chat.setRecentInfo(data.getMsg());
            }
        });
        long total = this.aippChatMapper.getChatListCount(request, null, context.getAccount());
        return RangedResultSet.create(result.stream().map(this::buildQueryChatRspDto).collect(Collectors.toList()),
            body.getOffset(), body.getLimit(), total);
    }

    private String getInstanceId(QueryChatRsp chat) {
        Map<String, Object> mapTypes = JsonUtils.parseObject(chat.getAttributes());
        return String.valueOf(mapTypes.get("instId"));
    }

    private QueryChatRspDto buildQueryChatRspDto(QueryChatRsp rsp) {
        return QueryChatRspDto.builder()
                .appId(rsp.getAppId())
                .version(rsp.getVersion())
                .aippId(rsp.getAippId())
                .aippVersion(rsp.getAippVersion())
                .chatId(rsp.getChatId())
                .chatName(rsp.getChatName())
                .originChatId(rsp.getOriginChatId())
                .attributes(StringUtils.isBlank(rsp.getAttributes())
                        ? new HashMap<>()
                        : JsonUtils.parseObject(rsp.getAttributes()))
                .massageList(rsp.getMassageList())
                .msgId(rsp.getMsgId())
                .updateTime(rsp.getUpdateTime())
                .recentInfo(rsp.getRecentInfo())
                .updateTimeStamp(rsp.getUpdateTimeStamp())
                .currentTime(rsp.getCurrentTime())
                .total(rsp.getTotal())
                .build();
    }

    @Override
    public Void deleteChat(String chatId, String appId, OperationContext context) {
        this.validationApp(appId);
        if (StringUtils.isNotBlank(chatId)) {
            List<QueryChatRsp> queryChatRsps = this.aippChatMapper.selectChatListByChatIds(
                    Collections.singletonList(chatId));
            if (CollectionUtils.isEmpty(queryChatRsps)) {
                throw new AippException(AippErrCode.CHAT_NOT_FOUND);
            }
            this.aippChatMapper.deleteChat(chatId);
            return null;
        }
        String metaId;
        try {
            metaId = MetaUtils.getAippIdByAppId(this.metaService, appId, context);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
        this.aippChatMapper.deleteAppByAippId(metaId);
        return null;
    }

    private void validationApp(String appId) {
        if (StringUtils.isBlank(appId)) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
        AppBuilderApp appBuilderApp = this.appRepository.selectWithId(appId);
        if (appBuilderApp == null || StringUtils.isEmpty(appBuilderApp.getId())) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
    }

    @Override
    public QueryChatRsp updateChat(String originChatId, CreateChatRequest body, OperationContext context)
            throws AippTaskNotFoundException {
        List<QueryChatRsp> chatResult = this.aippChatMapper.selectChatList(null, originChatId, context.getAccount());
        if (chatResult == null || chatResult.size() == 0 || chatResult.get(0) == null) {
            throw new IllegalArgumentException("chatId is not exist");
        }
        // 只查询该应用的近次记录
        Map<String, Object> bodyContext = body.getInitContext();
        bodyContext.put("chatId", originChatId);
        body.setInitContext(bodyContext);
        Map<String, Object> result = ObjectUtils.cast(bodyContext.get(AippConst.BS_INIT_CONTEXT_KEY));
        result.put("chatId", originChatId);
        bodyContext.put(AippConst.BS_INIT_CONTEXT_KEY, result);
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
                ? queryChat(queryBody, originChatId, context)
                : queryChat(queryBody, body.getChatId(), context);
    }

    @Override
    public List<ChatInfoRspDto> queryChatInfo(QueryChatInfoRequest queryChatInfoRequest, OperationContext context) {
        List<QueryChatRsp> queryChatRsps = this.aippChatMapper.selectChatByCondition(
                queryChatInfoRequest.getCondition(), queryChatInfoRequest);
        return queryChatRsps.stream().map(this::buildChatInfoRspDto).collect(Collectors.toList());
    }

    private ChatInfoRspDto buildChatInfoRspDto(QueryChatRsp queryChatRsp) {
        return ChatInfoRspDto.builder().chatId(queryChatRsp.getChatId()).build();
    }

    @Override
    public QueryChatRsp restartChat(String currentInstanceId, Map<String, Object> additionalContext,
            OperationContext context) {
        String path = this.aippLogService.getParentPath(currentInstanceId);
        AippLogVO aippLogVO = AippLogVO.builder().path(path).build();
        String parentInstanceId = aippLogVO.getAncestors().get(0);
        if (StringUtils.isEmpty(parentInstanceId)) {
            throw new IllegalArgumentException(
                    StringUtils.format("The instance id {0} does not match ant parentInstanceId.", currentInstanceId));
        }
        List<String> chatIds = this.aippChatMapper.selectChatIdByInstanceId(parentInstanceId);
        if (chatIds.isEmpty()) {
            throw new IllegalArgumentException(
                    StringUtils.format("The instance id {0} does not match any chat id.", parentInstanceId));
        }
        List<QueryChatRsp> chatList = this.aippChatMapper.selectChatListByChatIds(chatIds);
        String chatId;
        String chatType = this.getChatType(chatList.size());
        String restartMode = ObjectUtils.cast(
                additionalContext.getOrDefault(AippConst.RESTART_MODE, RestartModeEnum.OVERWRITE.getMode()));
        additionalContext.put(AippConst.RESTART_MODE, restartMode);
        CreateChatRequest body = this.buildChatBody(parentInstanceId, additionalContext);
        if (chatType == NORMAL_CHAT) {
            chatId = chatList.get(0).getChatId();
        } else if (chatType == FROM_OTHER_CHAT) {
            chatId = this.buildChatBodyWhenAtApp(chatList, body, chatIds);
        } else {
            throw new IllegalArgumentException(
                    StringUtils.format("The chat ids {0} match illegal num of chat sessions.", chatIds));
        }
        if (StringUtils.equals(RestartModeEnum.OVERWRITE.getMode(), restartMode)) {
            this.aippChatMapper.deleteWideRelationshipByInstanceId(parentInstanceId);
            this.aippLogService.deleteInstanceLog(parentInstanceId);
        }
        try {
            return this.updateChat(chatId, body, context);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(RE_CHAT_FAILED);
        }
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
        List<String> filterLogTypes = new ArrayList<>(
                Arrays.asList(AippInstLogType.HIDDEN_MSG.name(), AippInstLogType.HIDDEN_FORM.name()));
        List<AippInstLog> aippInstLogs = this.aippLogService.queryAndFilterLogsByLogType(instanceId, filterLogTypes);
        List<AippInstLog> questionAippInstLogs = aippInstLogs.stream()
                .filter(item -> StringUtils.equals(item.getLogType(), AippInstLogType.QUESTION.name())
                        || StringUtils.equals(item.getLogType(), AippInstLogType.HIDDEN_QUESTION.name()))
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
            throw new IllegalArgumentException(
                    StringUtils.format("The chat ids {0} chat sessions are illegal.", chatIds));
        } else if (firstOriginApp != null) {
            chatId = this.buildChatBodyWithOriginApp(firstOriginApp, firstChatMap, firstChat, secondChat, body);
        } else {
            chatId = this.buildChatBodyWithOriginApp(secondOriginApp, secondChatMap, secondChat, firstChat, body);
        }
        return chatId;
    }

    private boolean validateChatSessionWhenAtApp(String firstOriginApp, String secondOriginApp) {
        if ((firstOriginApp != null && secondOriginApp != null) || (firstOriginApp == null
                && secondOriginApp == null)) {
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
                throw new AippException(AippErrCode.FILE_FORMAT_VERIFICATION_FAILED, data.getClass().getName());
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
