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
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.genericable.AppFlowFinishObserver;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.FormUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.conf.runtime.SerializationFormat;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.metrics.po.ConversationRecordPo;
import com.huawei.jade.app.engine.metrics.service.ConversationRecordService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程结束回调节点
 *
 * @author 邬涨财 w00575064
 * @since 2024-05-24
 */
@Component
public class AippFlowEndCallback implements FlowCallbackService {
    private static final Logger log = Logger.get(AippFlowEndCallback.class);
    private final MetaInstanceService metaInstanceService;
    private final MetaService metaService;
    private final AippLogService aippLogService;
    private final AppBuilderFormRepository formRepository;
    private final BrokerClient brokerClient;
    private final BeanContainer beanContainer;
    private final ConversationRecordService conversationRecordService;

    public AippFlowEndCallback(
            @Fit MetaInstanceService metaInstanceService,
            @Fit MetaService metaService,
            @Fit AippLogService aippLogService,
            @Fit AppBuilderFormRepository formRepository,
            @Fit BrokerClient brokerClient,
            @Fit BeanContainer beanContainer,
            @Fit ConversationRecordService conversationRecordService) {
        this.metaInstanceService = metaInstanceService;
        this.metaService = metaService;
        this.aippLogService = aippLogService;
        this.formRepository = formRepository;
        this.brokerClient = brokerClient;
        this.beanContainer = beanContainer;
        this.conversationRecordService = conversationRecordService;
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.AippFlowEndCallback")
    @Override
    public void callback(List<Map<String, Object>> contexts) {
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        log.debug("AippFlowEndCallback businessData {}", businessData);

        String versionId = (String) businessData.get(AippConst.BS_META_VERSION_ID_KEY);
        OperationContext context =
                JsonUtils.parseObject((String) businessData.get(AippConst.BS_HTTP_CONTEXT_KEY), OperationContext.class);
        Meta meta = metaService.retrieve(versionId, context);
        Map<String, Object> attr = meta.getAttributes();
        String endFormId = (String) attr.get(AippConst.ATTR_END_FORM_ID_KEY);
        String endFormVersion = (String) attr.get(AippConst.ATTR_END_FORM_VERSION_KEY);

        // update all result data
        InstanceDeclarationInfo declarationInfo =
                InstanceDeclarationInfo.custom()
                        .putInfo(AippConst.INST_CURR_FORM_ID_KEY, endFormId)
                        .putInfo(AippConst.INST_CURR_FORM_VERSION_KEY, endFormVersion)
                        .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                        .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.ARCHIVED.name())
                        .putInfo(AippConst.INST_AGENT_RESULT_KEY, "") // 结果表单的参数作为agent结果
                        .build();
        businessData.forEach(
                (key, value) -> {
                    if (meta.getProperties().stream().anyMatch(item -> item.getName().equals(key))) {
                        declarationInfo.getInfo().getValue().put(key, value);
                    }
                });

        String aippInstId = (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY);
        this.metaInstanceService.patchMetaInstance(versionId, aippInstId, declarationInfo, context);

        // 持久化aipp实例表单记录
        if (StringUtils.isNotEmpty(endFormId) && StringUtils.isNotEmpty(endFormVersion)) {
            AippLogData logData =
                    FormUtils.buildLogDataWithFormData(this.formRepository, endFormId, endFormVersion, businessData);
            aippLogService.insertLog(AippInstLogType.FORM.name(), logData, businessData);
        }

        businessData.put(AippConst.ATTR_APP_ID_KEY, attr.get(AippConst.ATTR_APP_ID_KEY));
        this.logFinalOutput(contexts, businessData, aippInstId);

        // 子流程 callback 主流程
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String parentCallbackId = ObjectUtils.cast(businessData.get(AippConst.PARENT_CALLBACK_ID));
        if (StringUtils.isNotEmpty(parentInstanceId) && StringUtils.isNotEmpty(parentCallbackId)) {
            this.brokerClient
                    .getRouter(FlowCallbackService.class, "w8onlgq9xsw13jce4wvbcz3kbmjv3tuw")
                    .route(new FitableIdFilter(parentCallbackId))
                    .format(SerializationFormat.CBOR)
                    .invoke(contexts);
        }
    }

    private void logFinalOutput(
            List<Map<String, Object>> contexts, Map<String, Object> businessData, String aippInstId) {
        // todo: 表明流程结果是否需要再经过模型加工，当前场景全为false。
        //  正常情况下应该是在结束节点配上该key并放入businessData中，此处模拟该过程。
        //  如果子流程结束后需要再经过模型加工，子流程结束节点不打印日志；否则子流程结束节点需要打印日志。
        //  如果前一个节点是人工检查节点，并在结束节点reference到了表单，那么这里一定会打印消息。
        businessData.put(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM, false);
        if (ObjectUtils.<Boolean>cast(businessData.get(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM))) {
            return;
        }
        Object finalOutput = businessData.get(AippConst.BS_AIPP_FINAL_OUTPUT);
        if (businessData.get(AippConst.OUTPUT_IS_FROM_CHILD) != null
                && ObjectUtils.<Boolean>cast(businessData.get(AippConst.OUTPUT_IS_FROM_CHILD))) {
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
                    JsonUtils.parseObject(
                            ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)), OperationContext.class);
            // 构造用户历史对话记录并插表
            ConversationRecordPo conversationRecordPo =
                    ConversationRecordPo.builder()
                            .appId(ObjectUtils.cast(businessData.get(AippConst.ATTR_APP_ID_KEY)))
                            .question(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_QUESTION_KEY)))
                            .answer(logMsg)
                            .createUser(context.getName())
                            .createTime(LocalDateTime.parse(businessData.get(AippConst.INSTANCE_START_TIME).toString()))
                            .finishTime(LocalDateTime.now())
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
