/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowSmartFormService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.dto.chat.AppChatRsp;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.service.AppBuilderFormService;
import com.huawei.fit.jober.aipp.service.AppChatSseService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.FormUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.waterflow.domain.enums.FlowTraceStatus;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;

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

    private final AppBuilderFormService formService;
    private final MetaInstanceService metaInstanceService;
    private final AppChatSseService appChatSseService;

    public AippFlowSmartFormHandle(@Fit AppBuilderFormService formService, @Fit MetaInstanceService metaInstanceService,
            AppChatSseService appChatSseService) {
        this.formService = formService;
        this.metaInstanceService = metaInstanceService;
        this.appChatSseService = appChatSseService;
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
        String nodeId = ObjectUtils.cast(contexts.get(0).get(AippConst.BS_NODE_ID_KEY));
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        log.debug("handleSmartForm nodeId {} businessData {}", nodeId, businessData);

        this.updateInstance(sheetId, nodeId, businessData);

        AppBuilderForm appBuilderForm = this.formService.selectWithId(sheetId);
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String chatId = ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID));
        String atChatId = ObjectUtils.cast(businessData.get(AippConst.BS_AT_CHAT_ID));
        Map<String, Object> formDataMap = FormUtils.buildFormData(businessData, appBuilderForm, parentInstanceId);
        AppChatRsp appChatRsp = AppChatRsp.builder().chatId(chatId).atChatId(atChatId)
                .status(FlowTraceStatus.RUNNING.name())
                .answer(Collections.singletonList(AppChatRsp.Answer.builder()
                        .content(formDataMap).type(AippInstLogType.FORM.name()).build()))
                .instanceId(instanceId)
                .build();
        this.appChatSseService.sendToAncestorLastData(instanceId, appChatRsp);
    }

    private void updateInstance(String sheetId, String nodeId, Map<String, Object> businessData) {
        InstanceDeclarationInfo declarationInfo = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_CURR_FORM_ID_KEY, sheetId)
                .putInfo(AippConst.INST_CURR_FORM_VERSION_KEY, "1.0.0")
                .putInfo(AippConst.INST_CURR_NODE_ID_KEY, nodeId)
                .build();

        this.metaInstanceService.patchMetaInstance(ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY)),
                ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY)),
                declarationInfo,
                JsonUtils.parseObject(ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)),
                        OperationContext.class));
    }
}
