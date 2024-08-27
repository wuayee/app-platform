/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowCallbackService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.dto.chat.AppChatRsp;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.genericable.AppFlowFinishObserver;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AippStreamService;
import com.huawei.fit.jober.aipp.service.AppBuilderFormService;
import com.huawei.fit.jober.aipp.service.AppChatSseService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.FormUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.jade.app.engine.metrics.po.ConversationRecordPo;
import com.huawei.jade.app.engine.metrics.service.ConversationRecordService;

import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程结束回调节点
 *
 * @author 邬涨财
 * @since 2024-05-24
 */
@Component
public class AippFlowEndCallback implements FlowCallbackService {
    private static final Logger log = Logger.get(AippFlowEndCallback.class);
    private static final String DEFAULT_END_FORM_VERSION = "1.0.0";

    private final MetaService metaService;
    private final AippLogService aippLogService;
    private final AppBuilderFormRepository formRepository;
    private final BrokerClient brokerClient;
    private final BeanContainer beanContainer;
    private final ConversationRecordService conversationRecordService;
    private final AppBuilderFormService formService;
    private final AippStreamService aippStreamService;
    private final MetaInstanceService metaInstanceService;
    private final AppChatSseService appChatSseService;

    public AippFlowEndCallback(@Fit MetaService metaService, @Fit AippLogService aippLogService,
            @Fit AppBuilderFormRepository formRepository, @Fit BrokerClient brokerClient,
            @Fit BeanContainer beanContainer, @Fit ConversationRecordService conversationRecordService,
            @Fit AppBuilderFormService formService, @Fit AippStreamService aippStreamService,
            @Fit MetaInstanceService metaInstanceService, @Fit AppChatSseService appChatSseService) {
        this.formService = formService;
        this.metaService = metaService;
        this.aippLogService = aippLogService;
        this.formRepository = formRepository;
        this.brokerClient = brokerClient;
        this.beanContainer = beanContainer;
        this.aippStreamService = aippStreamService;
        this.metaInstanceService = metaInstanceService;
        this.conversationRecordService = conversationRecordService;
        this.appChatSseService = appChatSseService;
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.AippFlowEndCallback")
    @Override
    public void callback(List<Map<String, Object>> contexts) {
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        log.debug("AippFlowEndCallback businessData {}", businessData);

        String versionId = ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY));
        OperationContext context =
                JsonUtils.parseObject(
                        ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)), OperationContext.class);
        Meta meta = this.metaService.retrieve(versionId, context);
        String aippInstId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        this.saveInstance(businessData, versionId, aippInstId, context, meta);
        Map<String, Object> attr = meta.getAttributes();
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        businessData.put(AippConst.ATTR_APP_ID_KEY, attr.get(AippConst.ATTR_APP_ID_KEY));
        //  表明流程结果是否需要再经过模型加工，当前场景全为false。
        //  正常情况下应该是在结束节点配上该key并放入businessData中，此处模拟该过程。
        //  如果子流程结束后需要再经过模型加工，子流程结束节点不打印日志；否则子流程结束节点需要打印日志。
        //  如果前一个节点是人工检查节点，并在结束节点reference到了表单，那么这里一定会打印消息。
        businessData.put(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM, false);
        if (businessData.containsKey(AippConst.BS_END_FORM_ID_KEY)) {
            String endFormId = ObjectUtils.cast(businessData.get(AippConst.BS_END_FORM_ID_KEY));
            String endFormVersion = DEFAULT_END_FORM_VERSION;
            AppBuilderForm appBuilderForm = this.formService.selectWithId(endFormId);
            Map<String, Object> formDataMap = FormUtils.buildFormData(businessData, appBuilderForm, parentInstanceId);
            String chatId = ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID));
            String atChatId = ObjectUtils.cast(businessData.get(AippConst.BS_AT_CHAT_ID));
            String returnedLogId = null;
            if (StringUtils.isNotEmpty(endFormId) && StringUtils.isNotEmpty(endFormVersion)) {
                returnedLogId = this.saveFormToLog(businessData, endFormId, endFormVersion, formDataMap);
            }
            AppChatRsp appChatRsp = AppChatRsp.builder().chatId(chatId).atChatId(atChatId)
                    .status(FlowTraceStatus.ARCHIVED.name())
                    .answer(Collections.singletonList(AppChatRsp.Answer.builder()
                            .content(formDataMap).type(AippInstLogType.FORM.name()).build()))
                    .instanceId(aippInstId).logId(returnedLogId)
                    .build();
            this.appChatSseService.sendToAncestorLastData(aippInstId, appChatRsp);
        } else {
            this.logFinalOutput(contexts, businessData, aippInstId);
        }

        // 子流程 callback 主流程
        String parentCallbackId = ObjectUtils.cast(businessData.get(AippConst.PARENT_CALLBACK_ID));
        if (StringUtils.isNotEmpty(parentInstanceId) && StringUtils.isNotEmpty(parentCallbackId)) {
            this.brokerClient.getRouter(FlowCallbackService.class, "w8onlgq9xsw13jce4wvbcz3kbmjv3tuw")
                    .route(new FitableIdFilter(parentCallbackId))
                    .format(SerializationFormat.CBOR)
                    .invoke(contexts);
        }
    }

    private void saveInstance(Map<String, Object> businessData, String versionId, String aippInstId,
            OperationContext context, Meta meta) {
        InstanceDeclarationInfo declarationInfo = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.ARCHIVED.name())
                .build();
        businessData.forEach((key, value) -> {
            if (meta.getProperties().stream().anyMatch(item -> item.getName().equals(key))) {
                declarationInfo.getInfo().getValue().put(key, value);
            }
        });
        this.metaInstanceService.patchMetaInstance(versionId, aippInstId, declarationInfo, context);
    }

    private String saveFormToLog(Map<String, Object> businessData, String endFormId, String endFormVersion,
            Map<String, Object> formDataMap) {
        AippLogData logData =
                FormUtils.buildLogDataWithFormData(this.formRepository, endFormId, endFormVersion, businessData);
        logData.setFormAppearance(JsonUtils.toJsonString(formDataMap.get(AippConst.FORM_APPEARANCE_KEY)));
        logData.setFormData(JsonUtils.toJsonString(formDataMap.get(AippConst.FORM_DATA_KEY)));
        return this.aippLogService.insertLog(AippInstLogType.FORM.name(), logData, businessData);
    }

    private void logFinalOutput(List<Map<String, Object>> contexts, Map<String, Object> businessData,
            String aippInstId) {
        if (ObjectUtils.<Boolean>cast(businessData.get(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM))) {
            return;
        }
        if (!businessData.containsKey(AippConst.BS_AIPP_FINAL_OUTPUT)) {
            return;
        }
        Object finalOutput = businessData.get(AippConst.BS_AIPP_FINAL_OUTPUT);
        if (businessData.get(AippConst.OUTPUT_IS_FROM_CHILD) != null && ObjectUtils.<Boolean>cast(businessData.get(
                AippConst.OUTPUT_IS_FROM_CHILD))) {
            return;
        }
        String finalOutputStr =
                ObjectUtils.cast(finalOutput instanceof String ? finalOutput : JsonUtils.toJsonString(finalOutput));
        String logMsg = finalOutput == null ? "获取到的结果为 null，请检查配置。" : finalOutputStr;
        this.aippLogService.insertMsgLog(logMsg, contexts);
        this.beanContainer.all(AppFlowFinishObserver.class).stream()
                .<AppFlowFinishObserver>map(BeanFactory::get)
                .forEach(finishObserver -> finishObserver.onFinished(logMsg, this.buildAttributes(aippInstId)));

        // 评估调用接口时不记录历史会话
        Object isEval = businessData.get(AippConst.IS_EVAL_INVOCATION);
        if (isEval == null || !ObjectUtils.<Boolean>cast(isEval)) {
            OperationContext context =
                    JsonUtils.parseObject(ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)),
                            OperationContext.class);

            // 构造用户历史对话记录并插表
            String resumeDuration =
                    ObjectUtils.cast(businessData.getOrDefault(AippConst.INST_RESUME_DURATION_KEY, "0"));
            Object createTimeObj = Validation.notNull(businessData.get(AippConst.INSTANCE_START_TIME),
                    "The create time cannot be null.");
            LocalDateTime createTime = LocalDateTime.parse(createTimeObj.toString());
            LocalDateTime finishTime = LocalDateTime.now();
            long realCost = Duration.between(createTime, finishTime).toMillis() - Long.parseLong(resumeDuration);
            LocalDateTime realFinishTime = (realCost > 0) ? createTime.plus(realCost, ChronoUnit.MILLIS) : finishTime;
            ConversationRecordPo conversationRecordPo = ConversationRecordPo.builder()
                    .appId(ObjectUtils.cast(businessData.get(AippConst.ATTR_APP_ID_KEY)))
                    .question(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_QUESTION_KEY)))
                    .answer(logMsg)
                    .createUser(context.getName())
                    .createTime(createTime)
                    .finishTime(realFinishTime)
                    .instanceId(aippInstId)
                    .build();
            conversationRecordService.insertConversationRecord(conversationRecordPo);
        }
    }

    private Map<String, Object> buildAttributes(String aippInstId) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.BS_AIPP_INST_ID_KEY, aippInstId);
        return attributes;
    }
}
