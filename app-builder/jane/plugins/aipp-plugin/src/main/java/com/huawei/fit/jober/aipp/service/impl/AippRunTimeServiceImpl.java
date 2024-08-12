/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.dynamicform.DynamicFormService;
import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.enums.DirectionEnum;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jober.FlowInstanceService;
import com.huawei.fit.jober.FlowsService;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippForbiddenException;
import com.huawei.fit.jober.aipp.common.exception.AippNotFoundException;
import com.huawei.fit.jober.aipp.condition.AippInstanceQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.AippInstanceCreateDto;
import com.huawei.fit.jober.aipp.dto.AippInstanceDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppStartDto;
import com.huawei.fit.jober.aipp.dto.MemoryConfigDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.dto.chat.AppChatRsp;
import com.huawei.fit.jober.aipp.dto.form.AippFormRsp;
import com.huawei.fit.jober.aipp.dto.xiaohai.QADto;
import com.huawei.fit.jober.aipp.dto.xiaohai.UploadChatHistoryDto;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.AippTypeEnum;
import com.huawei.fit.jober.aipp.enums.FormEdgeEnum;
import com.huawei.fit.jober.aipp.enums.MetaInstSortKeyEnum;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.enums.RestartModeEnum;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.genericable.entity.AippCreate;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AippRunTimeService;
import com.huawei.fit.jober.aipp.service.AippStreamService;
import com.huawei.fit.jober.aipp.service.AopAippLogService;
import com.huawei.fit.jober.aipp.service.AppChatSseService;
import com.huawei.fit.jober.aipp.service.UploadedFileManageService;
import com.huawei.fit.jober.aipp.util.AippFileUtils;
import com.huawei.fit.jober.aipp.util.AippLogUtils;
import com.huawei.fit.jober.aipp.util.AippStringUtils;
import com.huawei.fit.jober.aipp.util.FormUtils;
import com.huawei.fit.jober.aipp.util.HttpUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.aipp.util.MetaUtils;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.jober.entity.FlowInstanceResult;
import com.huawei.fit.jober.entity.FlowStartParameter;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.waterflow.domain.enums.FlowTraceStatus;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.Tuple;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * aipp运行时服务层接口实现
 *
 * @author l00611472
 * @since 2023-12-15
 */
@Component
public class AippRunTimeServiceImpl
        implements AippRunTimeService, com.huawei.fit.jober.aipp.genericable.AippRunTimeService {
    private static final String DEFAULT_QUESTION = "请解析以下文件。";
    private static final Logger log = Logger.get(AippRunTimeServiceImpl.class);
    private static final String DOWNLOAD_FILE_ORIGIN = "/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?";
    private static final String NAME_KEY = "name";
    private static final String VALUE_KEY = "value";
    private static final String TYPE_KEY = "type";

    private final MetaService metaService;
    private final DynamicFormService dynamicFormService;
    private final MetaInstanceService metaInstanceService;
    private final FlowInstanceService flowInstanceService;
    private final AippLogService aippLogService;
    private final UploadedFileManageService uploadedFileManageService;
    private final String uploadChatHistoryUrl;
    private final BrokerClient client;
    private final AppBuilderFormRepository formRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final FlowsService flowsService;
    private final String sharedUrl;
    private final String appEngineUrl;
    private final AippStreamService aippStreamService;
    private final AopAippLogService aopAippLogService;
    private final AppChatSseService appChatSSEService;
    private final AippLogService logService;
    private final HttpClassicClientFactory httpClientFactory;
    private final AppBuilderAppFactory appFactory;

    public AippRunTimeServiceImpl(@Fit MetaService metaService, @Fit DynamicFormService dynamicFormService,
            @Fit MetaInstanceService metaInstanceService, @Fit FlowInstanceService flowInstanceService,
            @Fit AippLogService aippLogService, @Fit UploadedFileManageService uploadedFileManageService,
            @Value("${xiaohai.upload_chat_history_url}") String uploadChatHistoryUrl, BrokerClient client,
            @Fit AppBuilderFormRepository formRepository, @Fit AppBuilderFormPropertyRepository formPropertyRepository,
            @Fit FlowsService flowsService, @Value("${xiaohai.share_url}") String sharedUrl,
            @Fit AippStreamService aippStreamService, @Value("${app-engine.endpoint}") String appEngineUrl,
            @Fit AopAippLogService aopAippLogService, @Fit AppChatSseService appChatSSEService,
            @Fit AippLogService logService, @Fit HttpClassicClientFactory httpClientFactory,
            @Fit AppBuilderAppFactory appFactory) {
        this.metaService = metaService;
        this.dynamicFormService = dynamicFormService;
        this.metaInstanceService = metaInstanceService;
        this.flowInstanceService = flowInstanceService;
        this.aippLogService = aippLogService;
        this.uploadedFileManageService = uploadedFileManageService;
        this.uploadChatHistoryUrl = uploadChatHistoryUrl;
        this.formRepository = formRepository;
        this.client = client;
        this.formPropertyRepository = formPropertyRepository;
        this.flowsService = flowsService;
        this.sharedUrl = sharedUrl;
        this.aippStreamService = aippStreamService;
        this.appEngineUrl = appEngineUrl;
        this.aopAippLogService = aopAippLogService;
        this.httpClientFactory = httpClientFactory;
        this.appChatSSEService = appChatSSEService;
        this.logService = logService;
        this.appFactory = appFactory;
    }

    private static void setExtraBusinessData(OperationContext context, Map<String, Object> businessData, Meta meta,
            String instId) {
        businessData.put(AippConst.BS_AIPP_ID_KEY, meta.getId());
        businessData.put(AippConst.BS_AIPP_VERSION_KEY, meta.getVersion());
        businessData.put(AippConst.BS_META_VERSION_ID_KEY, meta.getVersionId());
        businessData.put(AippConst.ATTR_AIPP_TYPE_KEY,
                meta.getAttributes().getOrDefault(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.NORMAL.name()));
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, instId);
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(context));
    }

    /**
     * 查询aippd的node节点对应的表单
     *
     * @param aippId aipp id
     * @param version aipp version
     * @param startOrEnd 开始或结束节点信息
     * @param context 操作上下文
     * @return 表单信息
     */
    @Override
    public AippFormRsp queryEdgeSheetData(String aippId, String version, String startOrEnd, OperationContext context) {
        FormEdgeEnum edge = FormEdgeEnum.getFormEdge(startOrEnd);
        Meta meta;
        if (version != null && AippStringUtils.isPreview(version)) {
            meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        } else {
            meta = MetaUtils.getLastPublishedMeta(metaService, aippId, context);
        }

        String formId = ObjectUtils.<String>cast(meta.getAttributes().get(edge.getFormIdKey()));
        Validation.notBlank(formId, () -> new AippNotFoundException(context, edge.name() + " formId"));
        String formVersion = ObjectUtils.<String>cast(meta.getAttributes().get(edge.getVersionKey()));
        Validation.notBlank(formVersion, () -> new AippNotFoundException(context, edge.name() + " formVersion"));
        DynamicFormDetailEntity entity = FormUtils.queryFormDetailByPrimaryKey(formId,
                formVersion,
                context,
                this.formRepository,
                this.formPropertyRepository);
        if (entity == null) {
            throw new AippNotFoundException(context, edge.name() + " form");
        }
        return new AippFormRsp(entity.getMeta(), entity.getData(), meta.getVersion());
    }

    private InstanceDeclarationInfo genMetaInstInitialInfo(Map<String, Object> businessData, String creator) {
        return InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_NAME_KEY, businessData.getOrDefault(AippConst.INST_NAME_KEY, "无标题"))
                .putInfo(AippConst.INST_CREATOR_KEY, creator)
                .putInfo(AippConst.INST_CREATE_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.RUNNING.name())
                .putInfo(AippConst.INST_PROGRESS_KEY, "0")
                .build();
    }

    /**
     * 启动一个Aipp
     *
     * @param aippId aippId
     * @param version aipp 版本
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @return 实例id
     */
    @Override
    @Fitable("default")
    public String createAippInstance(String aippId, String version, Map<String, Object> initContext,
            OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        return createInstanceHandle(initContext, context, meta);
    }

    /**
     * 启动一个运行Aipp
     *
     * @param appId appId
     * @param question 会话问题
     * @param businessData 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @param isDebug 是否为debug会话
     * @return 实例id
     */
    @Override
    public Tuple createInstanceByApp(String appId, String question, Map<String, Object> businessData,
            OperationContext context, boolean isDebug) {
        List<Meta> meta = MetaUtils.getAllMetasByAppId(metaService, appId, context);
        if (isDebug) {
            if (CollectionUtils.isEmpty(meta)) {
                throw new AippException(AippErrCode.APP_CHAT_DEBUG_META_NOT_FOUND);
            }
            return createInstanceHandle(question, businessData, meta.get(0), context);
        }
        if (CollectionUtils.isEmpty(meta)) {
            throw new AippException(AippErrCode.APP_CHAT_PUBLISHED_META_NOT_FOUND);
        }
        String aippId = meta.get(0).getId();
        List<Meta> allPublishedMeta = MetaUtils.getAllPublishedMeta(metaService, aippId, context);
        if (CollectionUtils.isEmpty(allPublishedMeta)) {
            throw new AippException(AippErrCode.APP_CHAT_PUBLISHED_META_NOT_FOUND);
        }
        return createInstanceHandle(question, businessData, allPublishedMeta.get(0), context);
    }

    @Override
    public String startFlowWithUserSelectMemory(String metaInstId, Map<String, Object> initContext,
            OperationContext context) {
        String versionId = this.metaInstanceService.getMetaVersionId(metaInstId);
        Meta meta = this.metaService.retrieve(versionId, context);
        Map<String, Object> businessData = (Map<String, Object>) initContext.get(AippConst.BS_INIT_CONTEXT_KEY);
        String flowDefinitionId = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_FLOW_DEF_ID_KEY));
        this.startFlow(versionId, flowDefinitionId, metaInstId, businessData, context);
        return metaInstId;
    }

    /**
     * 启动一个最新版本的Aipp
     *
     * @param aippId aippId
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @return 实例响应
     */
    @Override
    public AippInstanceCreateDto createAippInstanceLatest(String aippId, Map<String, Object> initContext,
            OperationContext context) {
        Meta meta = MetaUtils.getLastPublishedMeta(metaService, aippId, context);
        String instId = createInstanceHandle(initContext, context, meta);
        return AippInstanceCreateDto.builder()
                .instanceId(instId)
                .version(meta.getVersion())
                .versionId(meta.getVersionId())
                .build();
    }

    private String createInstanceHandle(Map<String, Object> initContext, OperationContext context, Meta meta) {
        Map<String, Object> businessData = (Map<String, Object>) initContext.get(AippConst.BS_INIT_CONTEXT_KEY);
        businessData.put("startNodeInputParams",
                JSONObject.parse(JSONObject.toJSONString(initContext.get(AippConst.BS_INIT_CONTEXT_KEY))));
        String restartMode = ObjectUtils.cast(businessData.getOrDefault(AippConst.RESTART_MODE,
                RestartModeEnum.OVERWRITE.getMode()));
        businessData.put(AippConst.RESTART_MODE, restartMode);
        // 记录启动时间
        businessData.put(AippConst.INSTANCE_START_TIME, LocalDateTime.now());

        // 创建meta实例
        String metaVersionId = meta.getVersionId();
        Instance metaInst = this.metaInstanceService.createMetaInstance(metaVersionId,
                genMetaInstInitialInfo(businessData, context.getOperator()),
                context);
        String metaInstId = metaInst.getId();
        setExtraBusinessData(context, businessData, meta, metaInstId);

        // 持久化日志
        this.persistAippLog(businessData);

        // 添加文件记录标记, 使用aippId
        uploadedFileManageService.addFileRecord(meta.getId(),
                context.getW3Account(),
                Paths.get(AippFileUtils.NAS_SHARE_DIR, metaInstId).toAbsolutePath().toString());

        // 持久化aipp实例表单记录
        this.persistAippFormLog(meta, businessData);

        // 记录上下文
        this.recordContext(context, meta, businessData, metaInst);

        // 添加memory
        String flowDefinitionId = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_FLOW_DEF_ID_KEY));
        List<Map<String, Object>> memoryConfigs = this.getMemoryConfigs(flowDefinitionId, context);
        String memoryType = this.getMemoryType(memoryConfigs);
        boolean isMemorySwitch = this.getMemorySwitch(memoryConfigs, businessData);
        if (!isMemorySwitch) {
            this.startFlow(metaVersionId, flowDefinitionId, metaInstId, businessData, context);
            return metaInstId;
        }
        if (!StringUtils.equalsIgnoreCase("UserSelect", memoryType)) {
            String chatId = (businessData.get("chatId") == null) ? null : businessData.get("chatId").toString();
            String aippType = ObjectUtils.cast(meta.getAttributes()
                    .getOrDefault(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.NORMAL.name()));
            businessData.put(AippConst.BS_AIPP_MEMORIES_KEY,
                    this.getMemories(meta.getId(), memoryType, chatId, memoryConfigs, aippType, businessData, context));
            this.startFlow(metaVersionId, flowDefinitionId, metaInstId, businessData, context);
        } else {
            this.aippStreamService.sendToAncestor(metaInstId,
                    this.buildMemoryConfigDto(initContext, metaInstId, "UserSelect"));
        }
        return metaInstId;
    }

    private Tuple createInstanceHandle(String question, Map<String, Object> businessData, Meta meta,
            OperationContext context) {
        businessData.put("startNodeInputParams", JSONObject.parse(JSONObject.toJSONString(businessData)));
        businessData.put(AippConst.RESTART_MODE,
                businessData.getOrDefault(AippConst.RESTART_MODE, RestartModeEnum.OVERWRITE.getMode()));
        // 记录启动时间
        businessData.put(AippConst.INSTANCE_START_TIME, LocalDateTime.now());

        // 创建meta实例
        Instance metaInst = this.metaInstanceService.createMetaInstance(meta.getVersionId(),
                genMetaInstInitialInfo(businessData, context.getOperator()),
                context);
        AippRunTimeServiceImpl.setExtraBusinessData(context, businessData, meta, metaInst.getId());

        // 持久化日志
        businessData.put(AippConst.BS_AIPP_QUESTION_KEY, question);
        this.persistAippLog(businessData);

        // 添加文件记录标记, 使用aippId
        uploadedFileManageService.addFileRecord(meta.getId(),
                context.getW3Account(),
                Paths.get(AippFileUtils.NAS_SHARE_DIR, metaInst.getId()).toAbsolutePath().toString());

        // 持久化aipp实例表单记录
        this.persistAippFormLog(meta, businessData);

        // 记录上下文
        this.recordContext(context, meta, businessData, metaInst);
        return Tuple.duet(metaInst.getId(),
                Choir.create(emitter -> this.startFlowWithMemoryOrNot(businessData, meta, context, metaInst, emitter)));
    }

    private void startFlowWithMemoryOrNot(Map<String, Object> businessData, Meta meta, OperationContext context,
            Instance metaInst, Emitter<Object> emitter) {
        String flowDefinitionId = cast(meta.getAttributes().get(AippConst.ATTR_FLOW_DEF_ID_KEY));
        List<Map<String, Object>> memoryConfigs = this.getMemoryConfigs(flowDefinitionId, context);
        this.appChatSSEService.addEmitter(metaInst.getId(), emitter, new CountDownLatch(1));
        this.appChatSSEService.send(metaInst.getId(), AppChatRsp.builder()
                .instanceId(metaInst.getId())
                .atChatId(ObjectUtils.cast(businessData.get(AippConst.BS_AT_CHAT_ID)))
                .chatId(ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID)))
                .status(FlowTraceStatus.READY.name())
                .build());
        boolean isMemorySwitch = this.getMemorySwitch(memoryConfigs, businessData);
        if (!isMemorySwitch) {
            this.startFlow(meta.getVersionId(), flowDefinitionId, metaInst.getId(), businessData, context);
            this.appChatSSEService.latchAwait(metaInst.getId());
            return;
        }
        String memoryType = this.getMemoryType(memoryConfigs);
        if (!StringUtils.equalsIgnoreCase("UserSelect", memoryType)) {
            String memoryChatId = ObjectUtils.cast(businessData.get("chatId"));
            String aippType = ObjectUtils.cast(meta.getAttributes()
                    .getOrDefault(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.NORMAL.name()));
            businessData.put(AippConst.BS_AIPP_MEMORIES_KEY,
                    this.getMemories(meta.getId(), memoryType, memoryChatId, memoryConfigs, aippType,
                            businessData,
                            context));
            this.startFlow(meta.getVersionId(), flowDefinitionId, metaInst.getId(), businessData, context);
            this.appChatSSEService.latchAwait(metaInst.getId());
        } else {
            String processedInstanceId = metaInst.getId();
            String path = this.logService.getParentPath(processedInstanceId);
            if (StringUtils.isNotEmpty(path)) {
                processedInstanceId = path.split(AippLogUtils.PATH_DELIMITER)[1];
            }
            this.appChatSSEService.sendLastData(processedInstanceId,
                    this.buildMemoryConfigDto(businessData, metaInst.getId(), "UserSelect"));
        }
    }

    private void recordContext(OperationContext context, Meta meta, Map<String, Object> businessData,
            Instance metaInst) {
        businessData.put(AippConst.CONTEXT_APP_ID, meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        businessData.put(AippConst.CONTEXT_INSTANCE_ID, metaInst.getId());
        businessData.put(AippConst.BS_AIPP_MEMORIES_KEY, new ArrayList<>());
        businessData.put(AippConst.CONTEXT_USER_ID, context.getOperator());
        if (businessData.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY)) {
            Map<String, String> fileDescription = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_FILE_DESC_KEY));
            String filePath = fileDescription.get("file_path");
            String fileName = fileDescription.get("file_name");
            String fileUrl = appEngineUrl + DOWNLOAD_FILE_ORIGIN + "filePath=" + filePath + "&fileName=" + fileName;
            businessData.put(AippConst.BS_AIPP_FILE_DOWNLOAD_KEY, fileUrl);
        }
    }

    private void persistAippFormLog(Meta meta, Map<String, Object> businessData) {
        String formId = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_START_FORM_ID_KEY));
        String formVersion = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_START_FORM_VERSION_KEY));
        if (StringUtils.isNotEmpty(formId) && StringUtils.isNotEmpty(formVersion)) {
            AippLogData logData =
                    FormUtils.buildLogDataWithFormData(this.formRepository, formId, formVersion, businessData);
            aippLogService.insertLog(AippInstLogType.FORM.name(), logData, businessData);
        }
    }

    private boolean getMemorySwitch(List<Map<String, Object>> memoryConfig, Map<String, Object> businessData) {
        if (memoryConfig == null || memoryConfig.isEmpty()) {
            return true;
        }
        Map<String, Object> memorySwitchConfig = memoryConfig.stream()
                .filter(config -> StringUtils.equals(ObjectUtils.cast(config.get(NAME_KEY)),
                        AippConst.MEMORY_SWITCH_KEY))
                .findFirst()
                .orElse(null);
        if (memorySwitchConfig == null) {
            return true;
        }
        Boolean shouldUseMemory = ObjectUtils.cast(businessData.getOrDefault(AippConst.BS_AIPP_USE_MEMORY_KEY,
                memorySwitchConfig.get(VALUE_KEY)));
        businessData.put(AippConst.BS_AIPP_USE_MEMORY_KEY, shouldUseMemory);
        return shouldUseMemory;
    }

    private String getMemoryType(List<Map<String, Object>> memoryConfigs) {
        if (memoryConfigs == null || memoryConfigs.isEmpty()) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> typeConfig = memoryConfigs.stream()
                .filter(config -> StringUtils.equals(ObjectUtils.<String>cast(config.get(NAME_KEY)), TYPE_KEY))
                .findFirst()
                .orElseThrow(() -> new AippException(AippErrCode.PARSE_MEMORY_CONFIG_FAILED));
        return ObjectUtils.cast(typeConfig.get(VALUE_KEY));
    }

    private List<Map<String, Object>> getMemoryConfigs(String flowDefinitionId, OperationContext context) {
        FlowInfo flowInfo = this.flowsService.getFlows(flowDefinitionId, context);
        return flowInfo.getInputParamsByName(AippConst.MEMORY_CONFIG_KEY);
    }

    private MemoryConfigDto buildMemoryConfigDto(Map<String, Object> initContext, String instanceId, String memory) {
        return MemoryConfigDto.builder().initContext(initContext).instanceId(instanceId).memory(memory).build();
    }

    private void startFlow(String metaVersionId, String flowDefinitionId, String metaInstId,
            Map<String, Object> businessData, OperationContext context) {
        FlowStartParameter parameter = new FlowStartParameter(context.getOperator(), businessData);
        FlowInstanceResult flowInst = flowInstanceService.startFlow(flowDefinitionId, parameter, context);

        InstanceDeclarationInfo info = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_FLOW_INST_ID_KEY, flowInst.getTraceId())
                .build();
        // 记录流程实例id到meta实例
        this.metaInstanceService.patchMetaInstance(metaVersionId, metaInstId, info, context);
    }

    private void persistAippLog(Map<String, Object> businessData) {
        String question = ObjectUtils.<String>cast(businessData.get(AippConst.BS_AIPP_QUESTION_KEY));
        String fileDesc =
                businessData.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY) ? JsonUtils.toJsonString(businessData.get(
                        AippConst.BS_AIPP_FILE_DESC_KEY)) : StringUtils.EMPTY;
        // 持久化日志
        if (StringUtils.isEmpty(fileDesc)) {
            if (this.isChildInstance(businessData) || this.isIncreamentMode(businessData)) {
                // 如果是子流程，或者处于增长式的重新对话中，插入 hidden_question
                aippLogService.insertLog(AippInstLogType.HIDDEN_QUESTION.name(),
                        AippLogData.builder().msg(question).build(),
                        businessData);
            } else {
                // 插入question日志
                aippLogService.insertLog(AippInstLogType.QUESTION.name(),
                        AippLogData.builder().msg(question).build(),
                        businessData);
            }
        } else {
            // 插入 hidden_question及file日志
            aippLogService.insertLog(AippInstLogType.HIDDEN_QUESTION.name(),
                    AippLogData.builder().msg(DEFAULT_QUESTION).build(),
                    businessData);
            aippLogService.insertLog(AippInstLogType.FILE.name(),
                    AippLogData.builder().msg(fileDesc).build(),
                    businessData);
            businessData.put(AippConst.BS_AIPP_QUESTION_KEY, DEFAULT_QUESTION);
        }
    }

    private boolean isIncreamentMode(Map<String, Object> businessData) {
        return StringUtils.equals(RestartModeEnum.INCREMENT.getMode(),
                ObjectUtils.cast(businessData.get(AippConst.RESTART_MODE)));
    }

    private boolean isChildInstance(Map<String, Object> businessData) {
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String parentCallbackId = ObjectUtils.cast(businessData.get(AippConst.PARENT_CALLBACK_ID));
        return StringUtils.isNotEmpty(parentInstanceId) && StringUtils.isNotEmpty(parentCallbackId);
    }

    private boolean checkInstanceStatus(String aippId, Instance instDetail, Function<String, Boolean> handler) {
        Map<String, String> instInfo = instDetail.getInfo();
        if (!instInfo.containsKey(AippConst.INST_STATUS_KEY)) {
            log.error("aipp {} inst{} status key not found.", aippId, instDetail.getId());
            return false;
        }
        String status = instInfo.get(AippConst.INST_STATUS_KEY);
        if (!handler.apply(status)) {
            log.error("aipp {} inst{} status {}, not allow to operate.", aippId, instDetail.getId(), status);
            return false;
        }
        return true;
    }

    private List<Map<String, Object>> getMemories(String aippId, String memoryType, String chatId,
            List<Map<String, Object>> memoryConfigs, String aippType, Map<String, Object> businessData,
            OperationContext context) {
        if (memoryConfigs == null || memoryConfigs.isEmpty()) {
            return this.getConversationTurns(aippId, aippType, 5, context, chatId);
        }
        Map<String, Object> valueConfig = memoryConfigs.stream()
                .filter(config -> StringUtils.equals(ObjectUtils.<String>cast(config.get(NAME_KEY)), VALUE_KEY))
                .findFirst()
                .orElseThrow(() -> new AippException(AippErrCode.PARSE_MEMORY_CONFIG_FAILED));
        switch (memoryType) {
            case "ByConversationTurn":
                Integer turnNum = Integer.parseInt(ObjectUtils.<String>cast(valueConfig.get(VALUE_KEY)));
                return getConversationTurns(aippId, aippType, turnNum, context, chatId);
            case "NotUseMemory":
                // 兼容旧应用
                businessData.put(AippConst.BS_AIPP_USE_MEMORY_KEY, false);
                return new ArrayList<>();
            case "Customizing":
                // 如何定义这个genericable接口，入参为一个map?
                String fitableId = ObjectUtils.<String>cast(valueConfig.get(VALUE_KEY));
                // 目前flow graph中并没有params的配置，暂时用一个空map
                Map<String, Object> params = new HashMap<>();
                return getCustomizedLogs(fitableId, params, aippId, aippType, context);
            default:
                return getConversationTurns(aippId, aippType, 5, context, chatId);
        }
    }

    private List<Map<String, Object>> getConversationTurns(String aippId, String aippType, Integer count,
            OperationContext context, String chatId) {
        List<AippInstLogDataDto> logs;
        if (chatId != null) {
            logs = aippLogService.queryChatRecentInstLog(aippId, aippType, count, context, chatId);
            return getLogMaps(logs);
        }
        return new ArrayList<>();
    }

    /**
     * 将日志数据转换为前端可展示的格式
     *
     * @param logs 日志数据列表
     * @return 转换后的前端展示数据列表
     */
    public static List<Map<String, Object>> getLogMaps(List<AippInstLogDataDto> logs) {
        List<Map<String, Object>> memories = new ArrayList<>();
        logs.forEach(log -> {
            Map<String, Object> logMap = new HashMap<>();
            AippInstLogDataDto.AippInstanceLogBody question = log.getQuestion();
            if (question == null) {
                return;
            }
            logMap.put("question", getLogData(question.getLogData()));
            List<AippInstLogDataDto.AippInstanceLogBody> answers = log.getInstanceLogBodies()
                    .stream()
                    .filter(l -> (l.getLogType().equals(AippInstLogType.MSG.name()) || l.getLogType()
                            .equals(AippInstLogType.FORM.name())))
                    .collect(Collectors.toList());
            List<AippInstLogDataDto.AippInstanceLogBody> files = log.getInstanceLogBodies()
                    .stream()
                    .filter(l -> l.getLogType().equals(AippInstLogType.FILE.name()))
                    .collect(Collectors.toList());
            if (!answers.isEmpty()) {
                logMap.put("answer", getLogData(answers.get(answers.size() - 1).getLogData()));
            }
            if (!files.isEmpty()) {
                logMap.put("fileDescription", getLogData(files.get(0).getLogData()));
            }
            memories.add(logMap);
        });
        return memories.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 获取日志数据
     *
     * @param logData 日志数据
     * @return 返回日志数据中的form_args或者msg字段的值
     */
    public static String getLogData(String logData) {
        Map<String, String> logInfo = ObjectUtils.<Map<String, String>>cast(JSON.parse(logData));
        if (!StringUtils.isEmpty(logInfo.get("form_args"))) {
            return logInfo.get("form_args");
        } else {
            return logInfo.get("msg");
        }
    }

    private List<Map<String, Object>> getCustomizedLogs(String fitableId, Map<String, Object> params, String aippId,
            String aippType, OperationContext context) {
        final String genericableId = "68dc66a6185cf64c801e55c97fc500e4";
        if (fitableId == null) {
            log.warn("no fitable id in customized log selection.");
            return Collections.emptyList();
        }
        List<Map<String, Object>> logs;
        try {
            logs = this.client.getRouter(genericableId)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(params, aippId, aippType, context);
        } catch (FitException t) {
            log.error("Error occurred when get history logs, error: {}", t.getMessage());
            throw new AippException(AippErrCode.GET_HISTORY_LOG_FAILED);
        }
        return logs;
    }

    /**
     * 删除应用实例
     *
     * @param aippId aippId
     * @param version aipp 版本
     * @param instanceId 实例id
     * @param context 操作上下文
     */
    @Override
    public void deleteAippInstance(String aippId, String version, String instanceId, OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        String versionId = meta.getVersionId();

        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        Function<String, Boolean> handler = status -> MetaInstStatusEnum.getMetaInstStatus(status).getValue()
                > MetaInstStatusEnum.RUNNING.getValue();
        if (!checkInstanceStatus(aippId, instDetail, handler)) {
            log.error("aipp {} version {} inst{}, not allow terminate.", aippId, version, instanceId);
            throw new AippForbiddenException(context, AippErrCode.DELETE_INSTANCE_FORBIDDEN);
        }
        metaInstanceService.deleteMetaInstance(versionId, instanceId, context);
    }

    private AippInstanceDto instanceToAippInstanceDto(Instance instance, List<AippInstLog> instanceLogs,
            OperationContext context) {
        Map<String, String> info = instance.getInfo();
        DynamicFormDetailEntity entity =
                FormUtils.queryFormDetailByPrimaryKey(info.get(AippConst.INST_CURR_FORM_ID_KEY),
                        info.get(AippConst.INST_CURR_FORM_VERSION_KEY),
                        context,
                        this.formRepository,
                        this.formPropertyRepository);
        return AippInstanceDto.builder()
                .aippInstanceId(instance.getId())
                .tenantId(context.getTenantId())
                .aippInstanceName(info.get(AippConst.INST_NAME_KEY))
                .status(info.get(AippConst.INST_STATUS_KEY))
                .formMetadata(entity == null ? null : entity.getData())
                .formArgs(info)
                .startTime(info.get(AippConst.INST_CREATE_TIME_KEY))
                .endTime(info.getOrDefault(AippConst.INST_FINISH_TIME_KEY, null))
                .aippInstanceLogs(instanceLogs)
                .build();
    }

    /**
     * 查询单个应用实例信息
     *
     * @param aippId aippId
     * @param version aipp 版本
     * @param instanceId 实例id
     * @param context 操作上下文
     * @return AIPP 实例
     */
    @Override
    public AippInstanceDto getInstance(String aippId, String version, String instanceId, OperationContext context) {
        String metaVersionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Meta meta = this.metaService.retrieve(metaVersionId, null);
        context.setTenantId(meta.getTenant());
        return getInstanceByVersionId(meta.getVersionId(), instanceId, context);
    }

    /**
     * 通过versionId唯一标识查询单个应用实例信息
     *
     * @param versionId aipp 版本id
     * @param instanceId 实例id
     * @param context 操作上下文
     * @return AIPP 实例
     */
    @Override
    public AippInstanceDto getInstanceByVersionId(String versionId, String instanceId, OperationContext context) {
        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        List<AippInstLog> instanceLogs = aippLogService.queryInstanceLogSince(instDetail.getId(), null);
        return instanceToAippInstanceDto(instDetail, instanceLogs, context);
    }

    private MetaInstanceFilter genInstFilter(AippInstanceQueryCondition cond) {
        MetaInstanceFilter filter = new MetaInstanceFilter();

        String sortEncode = String.format(Locale.ROOT,
                "%s(info.%s)",
                DirectionEnum.getDirection(nullIf(cond.getOrder(), DirectionEnum.DESCEND.name())).getValue(),
                MetaInstSortKeyEnum.getInstSortKey(nullIf(cond.getSort(), MetaInstSortKeyEnum.START_TIME.name()))
                        .getKey());
        List<String> orderBy = Collections.singletonList(sortEncode);

        filter.setOrderBy(orderBy);
        Map<String, List<String>> infos = new HashMap<>();
        if (StringUtils.isNotBlank(cond.getCreator())) {
            infos.put(AippConst.INST_CREATOR_KEY, Collections.singletonList(cond.getCreator()));
        }
        if (StringUtils.isNotBlank(cond.getAippInstanceName())) {
            infos.put(AippConst.INST_NAME_KEY, Collections.singletonList(cond.getAippInstanceName()));
        }
        filter.setInfos(infos);
        return filter;
    }

    /**
     * 查询应用实例信息列表
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param version aipp 版本
     * @param cond 查询条件
     * @param page 分页条件
     * @return AIPP 实例列表
     */
    @Override
    public PageResponse<AippInstanceDto> listInstance(String aippId, String version, AippInstanceQueryCondition cond,
            PaginationCondition page, OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        String aippName = meta.getName();
        RangedResultSet<Instance> instDetail = metaInstanceService.list(meta.getVersionId(),
                genInstFilter(cond),
                page.getOffset(),
                page.getPageSize(),
                context);
        if (instDetail.getRange().getTotal() == 0) {
            log.error("aipp {} version {} inst not found.", aippId, meta.getVersion());
            return new PageResponse<>(0L, aippName, Collections.emptyList());
        }
        List<FormMetaQueryParameter> parameters = instDetail.getResults()
                .stream()
                .map(item -> new FormMetaQueryParameter(item.getInfo().get(AippConst.INST_CURR_FORM_ID_KEY),
                        item.getInfo().get(AippConst.INST_CURR_FORM_VERSION_KEY)))
                .collect(Collectors.toList());
        List<Map<FormMetaQueryParameter, DynamicFormDetailEntity>> entityMaps =
                this.dynamicFormService.queryFormDetailByPrimaryKeyAndMap(parameters, context);
        List<AippInstanceDto> aippInst = instDetail.getResults()
                .stream()
                .map(item -> this.buildAippInstanceDtoFromEntityList(item, entityMaps, context))
                .collect(Collectors.toList());
        return new PageResponse<>(instDetail.getRange().getTotal(), aippName, aippInst);
    }

    private AippInstanceDto buildAippInstanceDtoFromEntityList(Instance instance,
            List<Map<FormMetaQueryParameter, DynamicFormDetailEntity>> entityMaps, OperationContext context) {
        String formId = instance.getInfo().get(AippConst.INST_CURR_FORM_ID_KEY);
        String version = instance.getInfo().get(AippConst.INST_CURR_FORM_VERSION_KEY);
        DynamicFormDetailEntity entity = null;
        if (this.checkParameter(formId, version) && !entityMaps.isEmpty()) {
            entity = entityMaps.stream().filter(Objects::nonNull).filter(map -> {
                FormMetaQueryParameter parameterOfMap = map.keySet().iterator().next();
                return Objects.equals(parameterOfMap.getFormId(), formId) && Objects.equals(parameterOfMap.getVersion(),
                        version);
            }).map(Map::values).flatMap(Collection::stream).findFirst().orElse(null);
        }
        Map<String, String> info = instance.getInfo();
        return AippInstanceDto.builder()
                .aippInstanceId(instance.getId())
                .tenantId(context.getTenantId())
                .aippInstanceName(info.get(AippConst.INST_NAME_KEY))
                .status(info.get(AippConst.INST_STATUS_KEY))
                .formMetadata(entity == null ? null : entity.getData())
                .formArgs(info)
                .startTime(info.get(AippConst.INST_CREATE_TIME_KEY))
                .endTime(info.getOrDefault(AippConst.INST_FINISH_TIME_KEY, null))
                .aippInstanceLogs(null)
                .build();
    }

    private boolean checkParameter(String formId, String version) {
        if (StringUtils.isEmpty(formId) || Objects.equals(formId, AippConst.INVALID_FORM_ID)) {
            return false;
        }
        return StringUtils.isNotEmpty(version) && !Objects.equals(version, AippConst.INVALID_FORM_VERSION_ID);
    }

    private Map<String, Object> buildInfo(List<TaskProperty> props, Map<String, Object> businessData) {
        Map<String, Object> info = new HashMap<>();
        businessData.forEach((targetKey, targetValue) -> {
            if (props.stream().anyMatch(item -> item.getName().equals(targetKey))) {
                info.put(targetKey, targetValue);
            }
        });
        return info;
    }

    /**
     * 更新表单数据并上传到小海
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     */
    @Override
    public void updateAndUploadAippInstance(String aippId, String instanceId, Map<String, Object> formArgs,
            OperationContext context) {
        Map<String, Object> businessData = ObjectUtils.cast(formArgs.get(AippConst.BS_DATA_KEY));
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(context));
        businessData.put(AippConst.BS_AIPP_ID_KEY, aippId);
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, instanceId);
        log.debug("updateAippInstance businessData {}", businessData);
        this.updateAippLog(aippId, instanceId, context, businessData);
        Meta aipp = this.metaService.retrieve(aippId, context);
        this.updateAippInstance(aippId, aipp.getProperties(), instanceId, context, businessData);
        this.uploadChatHistory(aippId, context, businessData);
    }

    private void updateAippLog(String aippId, String instanceId, OperationContext context,
            Map<String, Object> businessData) {
        Instance oldInstDetail = MetaInstanceUtils.getInstanceDetail(aippId, instanceId, context, metaInstanceService);
        String formId = oldInstDetail.getInfo().get(AippConst.INST_CURR_FORM_ID_KEY);
        String formVersion = oldInstDetail.getInfo().get(AippConst.INST_CURR_FORM_VERSION_KEY);
        AippLogData newLogData =
                FormUtils.buildLogDataWithFormData(this.formRepository, formId, formVersion, businessData);
        Long logId = this.getLodId(instanceId, context);
        aippLogService.updateLog(logId, JsonUtils.toJsonString(newLogData));
    }

    private void updateAippInstance(String aippId, List<TaskProperty> props, String instanceId,
            OperationContext context, Map<String, Object> businessData) {
        this.updateAippInstance(aippId, props, instanceId, context, businessData, (info) -> {});
    }

    private void updateAippInstance(String aippId, List<TaskProperty> props, String instanceId,
            OperationContext context, Map<String, Object> businessData, Consumer<Map<String, Object>> afterInfoBuild) {
        Map<String, Object> info = this.buildInfo(props, businessData);
        afterInfoBuild.accept(info);
        log.debug("build info {} businessData {}", info, businessData);
        InstanceDeclarationInfo instanceDeclarationInfo = InstanceDeclarationInfo.custom().info(info).build();
        this.metaInstanceService.patchMetaInstance(aippId, instanceId, instanceDeclarationInfo, context);
    }

    private Long getLodId(String instanceId, OperationContext context) {
        AippInstLog oldAippInstLog = aippLogService.queryLastInstanceFormLog(instanceId);
        if (oldAippInstLog == null) {
            log.error("instanceId {} log is null", instanceId);
            throw new AippException(context, AippErrCode.AIPP_INSTANCE_LOG_IS_NULL);
        }
        return oldAippInstLog.getLogId();
    }

    private void uploadChatHistory(String aippId, OperationContext context, Map<String, Object> businessData) {
        HttpClassicClientRequest postRequest = httpClientFactory.create()
                .createRequest(HttpRequestMethod.POST, uploadChatHistoryUrl);
        postRequest.entity(ObjectEntity.create(postRequest, this.buildUploadHttpBody(businessData, context)));
        try (HttpClassicClientResponse<Object> response = HttpUtils.execute(postRequest)) {
            if (response.statusCode() != HttpResponseStatus.OK.statusCode()) {
                log.error("aipp {} uploadChatHistory fail:{}", aippId, response.reasonPhrase());
                throw new AippException(context, AippErrCode.XIAOHAI_UPLOAD_CHAT_HISTORY_HTTP_ERROR);
            }
            String respContent = response.textEntity().get().content();
            Map<String, Object> respObj = JsonUtils.parseObject(respContent);
            int code = (int) respObj.get("code");
            if (code != 0) {
                log.error("aipp {} uploadChatHistory fail: code {} msg {}", aippId, code, respObj.get("msg"));
                throw new AippException(context, AippErrCode.XIAOHAI_UPLOAD_CHAT_HISTORY_INNER_ERROR);
            }
        } catch (IOException e) {
            log.error("aipp {} uploadChatHistory fail", aippId, e.getMessage());
            throw new AippException(context, AippErrCode.XIAOHAI_UPLOAD_CHAT_HISTORY_HTTP_ERROR);
        }
    }

    private String buildUploadHttpBody(Map<String, Object> businessData, OperationContext context) {
        String chatHistoryJson = JsonUtils.toJsonString(businessData.get(AippConst.INST_OPERATION_REPORT_KEY));
        List<QADto> qaDtoList = JsonUtils.parseArray(chatHistoryJson, QADto[].class);
        if (CollectionUtils.isEmpty(qaDtoList)) {
            log.error("qaDtoList is empty");
            throw new AippException(context, AippErrCode.INPUT_PARAM_IS_INVALID);
        }

        String appId = qaDtoList.get(0).getQuestion().getAppId();
        Integer conversationId = qaDtoList.get(0).getQuestion().getConversationId();
        String createUser = qaDtoList.get(0).getQuestion().getCreateUser();

        UploadChatHistoryDto dto = UploadChatHistoryDto.builder()
                .answer(JsonUtils.toJsonString(qaDtoList.stream()
                        .map(this::buildXiaohaiDto)
                        .collect(Collectors.toList())))
                .appId(appId)
                .conversationId(conversationId)
                .createUser(createUser)
                .query("请基于我选择的内容撰写经营分析报告")
                .build();
        return JsonUtils.toJsonString(dto);
    }

    private UploadChatHistoryDto.XiaohaiQADto buildXiaohaiDto(QADto qaDto) {
        UploadChatHistoryDto.XiaohaiQADto xiaohaiQADto = new UploadChatHistoryDto.XiaohaiQADto();
        xiaohaiQADto.setQuestion(qaDto.getQuestion());
        xiaohaiQADto.setAnswer(this.buildXiaohaiAnswer(qaDto));
        return xiaohaiQADto;
    }

    private UploadChatHistoryDto.XiaohaiQADto.Answer buildXiaohaiAnswer(QADto qaDto) {
        QADto.Answer answer = qaDto.getAnswer();
        if (answer == null) {
            return null;
        }
        UploadChatHistoryDto.XiaohaiQADto.Answer xiaohaiAnswer = new UploadChatHistoryDto.XiaohaiQADto.Answer();
        xiaohaiAnswer.setAnswer(answer.getAnswer());
        xiaohaiAnswer.setChartType(answer.getChartType());
        if (CollectionUtils.isNotEmpty(answer.getChartData())) {
            List<String> chartData =
                    answer.getChartData().stream().map(JsonUtils::toJsonString).collect(Collectors.toList());
            xiaohaiAnswer.setChartData(chartData);
        }
        xiaohaiAnswer.setChartAnswer(answer.getChartAnswer());
        xiaohaiAnswer.setType(answer.getType());
        xiaohaiAnswer.setChartSummary(answer.getChartSummary());
        return xiaohaiAnswer;
    }

    /**
     * 更新表单数据，并恢复实例任务执行
     *
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     * @param context 操作上下文
     * @return 返回一个Choir对象，用于流式处理
     */
    @Override
    public Choir<Object> resumeAndUpdateAippInstance(String instanceId, Map<String, Object> formArgs,
            OperationContext context) {
        String metaVersionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Meta meta = this.metaService.retrieve(metaVersionId, context);
        String versionId = meta.getVersionId();

        // 更新表单数据
        Map<String, Object> businessData = ObjectUtils.cast(formArgs.get(AippConst.BS_DATA_KEY));
        setExtraBusinessData(context, businessData, meta, instanceId);

        // 获取旧的实例数据
        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        // 持久化aipp实例表单记录
        String formId = instDetail.getInfo().get(AippConst.INST_CURR_FORM_ID_KEY);
        String formVersion = instDetail.getInfo().get(AippConst.INST_CURR_FORM_VERSION_KEY);
        AippLogData logData =
                FormUtils.buildLogDataWithFormData(this.formRepository, formId, formVersion, businessData);

        // 设置表单的渲染数据和填充数据
        logData.setFormAppearance(ObjectUtils.cast(formArgs.get(AippConst.FORM_APPEARANCE_KEY)));
        logData.setFormData(ObjectUtils.cast(formArgs.get(AippConst.FORM_DATA_KEY)));
        aippLogService.insertLog(AippInstLogType.HIDDEN_FORM.name(), logData, businessData);

        // 更新实例并清空当前表单数据
        this.updateAippInstance(versionId, meta.getProperties(), instanceId, context, businessData, this::clearFormId);

        String flowTraceId = instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
        Validation.notNull(flowTraceId, "flowTraceId can not be null");
        String flowDefinitionId = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_FLOW_DEF_ID_KEY));
        return Choir.create(emitter -> {
            this.appChatSSEService.addEmitter(instanceId, emitter, new CountDownLatch(1));
            this.flowInstanceService.resumeFlow(flowDefinitionId, flowTraceId, formArgs, context);
            this.appChatSSEService.latchAwait(instanceId);
        });
    }

    private void clearFormId(Map<String, Object> info) {
        info.put(AippConst.INST_CURR_FORM_ID_KEY, AippConst.INVALID_FORM_ID);
        info.put(AippConst.INST_CURR_FORM_VERSION_KEY, AippConst.INVALID_FORM_VERSION_ID);
    }

    /**
     * 终止aipp实例
     *
     * @param context 操作上下文
     * @param instanceId 实例id
     * @param msgArgs 用于终止时返回的信息
     */
    @Override
    public void terminateInstance(String instanceId, Map<String, Object> msgArgs, OperationContext context) {
        String versionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        Function<String, Boolean> handler = status -> MetaInstStatusEnum.getMetaInstStatus(status).getValue()
                == MetaInstStatusEnum.RUNNING.getValue();
        Meta meta = this.metaService.retrieve(versionId, context);
        String aippId = meta.getId();
        if (!checkInstanceStatus(aippId, instDetail, handler)) {
            log.error("aipp {} inst{}, not allow terminate.", aippId, instanceId);
            throw new AippException(context, AippErrCode.TERMINATE_INSTANCE_FORBIDDEN);
        }

        String flowTraceId = instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
        Validation.notNull(flowTraceId, "flowTraceId can not be null");
        this.flowInstanceService.terminateFlows(null, flowTraceId, Collections.emptyMap(), context);

        // 更新实例状态
        InstanceDeclarationInfo info = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.TERMINATED.name())
                .build();
        this.metaInstanceService.patchMetaInstance(versionId, instanceId, info, context);

        String message =
                msgArgs.get(AippConst.TERMINATE_MESSAGE_KEY) != null ? msgArgs.get(AippConst.TERMINATE_MESSAGE_KEY)
                        .toString() : "已终止对话";
        String version = meta.getVersion();
        this.aopAippLogService.insertLog(AippLogCreateDto.builder()
                .aippId(aippId)
                .version(version)
                .aippType(ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY)))
                .aippType(ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY)))
                .instanceId(instanceId)
                .logType(AippInstLogType.MSG.name())
                .logData(JsonUtils.toJsonString(AippLogData.builder().msg(message).build()))
                .createUserAccount(context.getW3Account())
                .path(this.aippLogService.buildPath(instanceId, null)) // 这块在子流程调用时，得考虑下
                .build());
    }

    /**
     * 终止aipp全部实例
     *
     * @param aippId aipp Id
     * @param versionId versionId
     * @param isDeleteLog 是否删除aipp log
     * @param context 操作上下文
     */
    @Override
    public void terminateAllPreviewInstances(String aippId, String versionId, boolean isDeleteLog,
            OperationContext context) {
        final int limit = 15;
        Stream<Instance> instances = MetaUtils.getAllFromRangedResult(limit,
                offset -> metaInstanceService.list(versionId, new MetaInstanceFilter(), offset, limit, context));
        Function<String, Boolean> handler = status -> MetaInstStatusEnum.getMetaInstStatus(status).getValue()
                == MetaInstStatusEnum.RUNNING.getValue();
        instances
                // 只停止正在运行的
                .filter(instance -> checkInstanceStatus("versionId: " + versionId, instance, handler))
                .forEach(instance -> {
                    String flowTraceId = instance.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
                    Validation.notNull(flowTraceId, "flowTraceId can not be null");
                    flowInstanceService.terminateFlows(null, flowTraceId, Collections.emptyMap(), context);

                    // 更新实例状态
                    InstanceDeclarationInfo info = InstanceDeclarationInfo.custom()
                            .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.TERMINATED.name())
                            .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                            .build();
                    metaInstanceService.patchMetaInstance(versionId, instance.getId(), info, context);
                });

        if (isDeleteLog) {
            aippLogService.deleteAippPreviewLog(aippId, context);
        }
    }

    @Override
    public Map<String, Object> shared(List<Map<String, Object>> chats) {
        HttpClassicClientRequest postRequest = httpClientFactory.create()
                .createRequest(HttpRequestMethod.POST, this.sharedUrl);
        postRequest.entity(ObjectEntity.create(postRequest, JsonUtils.toJsonString(chats)));
        try {
            String respContent = HttpUtils.sendHttpRequest(postRequest);
            return JsonUtils.parseObject(respContent);
        } catch (IOException e) {
            log.error("Failed to share:", e.getMessage());
            throw new AippException(AippErrCode.XIAOHAI_SHARED_CHAT_HTTP_ERROR);
        }
    }

    @Override
    public Map<String, Object> getShareData(String shareId) {
        HttpClassicClientRequest getRequest = this.httpClientFactory.create().createRequest(
                HttpRequestMethod.GET,
                this.sharedUrl + "?shareId=" + shareId);
        try {
            String respContent = HttpUtils.sendHttpRequest(getRequest);
            return JsonUtils.parseObject(respContent);
        } catch (IOException e) {
            throw new AippException(AippErrCode.UNKNOWN);
        }
    }

    @Override
    public AppBuilderAppStartDto startInstance(AppBuilderAppDto appDto, Map<String, Object> initContext,
            OperationContext context) {
        AippCreate aippCreate;
        final String genericableId = "com.huawei.fit.jober.aipp.service.app.debug";
        final String fitableId = "default";
        this.validateApp(appDto.getId());
        try {
            aippCreate =
                    this.client.getRouter(genericableId).route(new FitableIdFilter(fitableId)).invoke(appDto, context);
        } catch (FitException t) {
            String errorMsg = t.getMessage();
            log.error("Error occurred when create debug aipp, error: {}", errorMsg);
            throw new AippException(AippErrCode.CREATE_DEBUG_AIPP_FAILED, errorMsg);
        }
        String instanceId = createAippInstance(aippCreate.getAippId(), aippCreate.getVersion(), initContext, context);
        return AppBuilderAppStartDto.builder().instanceId(instanceId).aippCreate(aippCreate).build();
    }

    private void validateApp(String appId) {
        this.appFactory.create(appId);
    }
}
