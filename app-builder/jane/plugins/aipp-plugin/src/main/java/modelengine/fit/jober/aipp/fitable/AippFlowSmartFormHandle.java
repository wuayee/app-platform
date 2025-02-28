/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_NODE_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BUSINESS_DATA_INTERNAL_KEY;

import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jober.FlowSmartFormService;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.service.RuntimeInfoService;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.FormUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.MetaInstanceUtils;
import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * aipp智能表单实现实现
 *
 * @author 刘信宏
 * @since 2023-12-25
 */
@Component
public class AippFlowSmartFormHandle implements FlowSmartFormService {
    private static final Logger log = Logger.get(AippFlowSmartFormHandle.class);

    private static final String DEFAULT_CURR_FORM_VERSION = "1.0.0";

    private final AppBuilderFormService formService;

    private final MetaInstanceService metaInstanceService;

    private final AppChatSseService appChatSseService;

    private final AippLogService aippLogService;

    private final AppBuilderAppFactory appFactory;

    private final FlowInstanceService flowInstanceService;

    private final MetaService metaService;

    private final AopAippLogService aopAippLogService;

    private final RuntimeInfoService runtimeInfoService;

    public AippFlowSmartFormHandle(@Fit AppBuilderFormService formService, @Fit MetaInstanceService metaInstanceService,
            AppChatSseService appChatSseService, @Fit AippLogService aippLogService,
            @Fit AppBuilderAppFactory appFactory, @Fit FlowInstanceService flowInstanceService,
            @Fit MetaService metaService, @Fit AopAippLogService aopAippLogService,
            @Fit RuntimeInfoService runtimeInfoService) {
        this.formService = formService;
        this.metaInstanceService = metaInstanceService;
        this.appChatSseService = appChatSseService;
        this.aippLogService = aippLogService;
        this.appFactory = appFactory;
        this.flowInstanceService = flowInstanceService;
        this.metaService = metaService;
        this.aopAippLogService = aopAippLogService;
        this.runtimeInfoService = runtimeInfoService;
    }

    /**
     * 智能表单处理
     *
     * @param contexts 流程上下文信息
     * @param sheetId 表单Id
     */
    @Override
    @Fitable("qz90ufu144m607hfud1ecbk0dnq3xavd")
    public void handleSmartForm(List<Map<String, Object>> contexts, String sheetId) {
        long startTime = ConvertUtils.toLong(LocalDateTime.now());
        String nodeId = ObjectUtils.cast(contexts.get(0).get(BS_NODE_ID_KEY));
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        log.debug("handleSmartForm nodeId {} businessData {}", nodeId, businessData);
        businessData.put(BS_NODE_ID_KEY, nodeId);

        this.updateInstance(sheetId, nodeId, businessData);

        AppBuilderForm appBuilderForm = this.formService.selectWithId(sheetId);
        if (appBuilderForm == null) {
            log.warn("Failed to handle smart form: form is not exist. {}", sheetId);
            this.exceptionHandler(businessData);
            return;
        }
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String chatId = ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID));
        String atChatId = ObjectUtils.cast(businessData.get(AippConst.BS_AT_CHAT_ID));
        String appId = ObjectUtils.cast(businessData.get(AippConst.CONTEXT_APP_ID));
        AppBuilderApp app = this.appFactory.create(appId);
        Map<String, Object> formDataMap = FormUtils.buildFormData(businessData, appBuilderForm, parentInstanceId);
        formDataMap.put(AippConst.NODE_START_TIME_KEY, startTime);
        if (businessData.containsKey(BUSINESS_DATA_INTERNAL_KEY)) {
            formDataMap.put(BUSINESS_DATA_INTERNAL_KEY, businessData.get(BUSINESS_DATA_INTERNAL_KEY));
        }
        formDataMap.put(BS_NODE_ID_KEY, nodeId);
        String logId = this.insertFormLog(app.getFormProperties(), sheetId, businessData, formDataMap);
        AppChatRsp appChatRsp = AppChatRsp.builder()
                .chatId(chatId)
                .atChatId(atChatId)
                .status(FlowTraceStatus.RUNNING.name())
                .answer(Collections.singletonList(
                        AppChatRsp.Answer.builder().content(formDataMap).type(AippInstLogType.FORM.name()).build()))
                .instanceId(instanceId)
                .logId(logId)
                .build();
        this.appChatSseService.sendToAncestorLastData(instanceId, appChatRsp);
    }

    /**
     * 1. 关闭表单流程（terminate）  2、error 信息写入日志，并推送给前端
     *
     * @param businessData 表示业务数据
     */
    private void exceptionHandler(Map<String, Object> businessData) {
        OperationContext context = JsonUtils.parseObject(
                ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)), OperationContext.class);
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.CONTEXT_INSTANCE_ID));
        String versionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Meta meta = this.metaService.retrieve(versionId, context);
        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context,
                this.metaInstanceService);
        String flowTraceId = instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
        this.flowInstanceService.terminateFlows(null, flowTraceId, Collections.emptyMap(), context);
        InstanceDeclarationInfo info = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.TERMINATED.name())
                .build();
        this.metaInstanceService.patchMetaInstance(versionId, instanceId, info, context);
        String message = AippErrCode.FORM_RUNNING_FAILED_CAUSE_NOT_EXISTED.getMessage();
        this.aopAippLogService.insertLog(AippLogCreateDto.builder()
                .aippId(meta.getId())
                .version(meta.getVersion())
                .aippType(ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY)))
                .aippType(ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY)))
                .instanceId(instanceId)
                .logType(AippInstLogType.ERROR.name())
                .logData(JsonUtils.toJsonString(AippLogData.builder().msg(message).build()))
                .createUserAccount(context.getAccount())
                .path(this.aippLogService.buildPath(instanceId, null))
                .build());
        this.runtimeInfoService.insertRuntimeInfo(instanceId, businessData, MetaInstStatusEnum.ERROR, message, context);
    }

    private String insertFormLog(List<AppBuilderFormProperty> formProperties, String sheetId,
            Map<String, Object> businessData, Map<String, Object> formDataMap) {
        AippLogData logData = FormUtils.buildLogDataWithFormData(formProperties, sheetId, DEFAULT_CURR_FORM_VERSION,
                businessData);
        Object appearance = formDataMap.get(AippConst.FORM_APPEARANCE_KEY);
        logData.setFormAppearance(ObjectUtils.cast(JsonUtils.toJsonString(appearance)));
        logData.setFormData(ObjectUtils.cast(JsonUtils.toJsonString(formDataMap.get(AippConst.FORM_DATA_KEY))));
        return this.aippLogService.insertLog(AippInstLogType.FORM.name(), logData, businessData);
    }

    private void updateInstance(String sheetId, String nodeId, Map<String, Object> businessData) {
        InstanceDeclarationInfo declarationInfo = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_CURR_FORM_ID_KEY, sheetId)
                .putInfo(AippConst.INST_CURR_FORM_VERSION_KEY, DEFAULT_CURR_FORM_VERSION)
                .putInfo(AippConst.INST_CURR_NODE_ID_KEY, nodeId)
                .putInfo(AippConst.INST_SMART_FORM_TIME_KEY, LocalDateTime.now())
                .build();

        this.metaInstanceService.patchMetaInstance(ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY)),
                ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY)), declarationInfo,
                JsonUtils.parseObject(ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)),
                        OperationContext.class));
    }
}
