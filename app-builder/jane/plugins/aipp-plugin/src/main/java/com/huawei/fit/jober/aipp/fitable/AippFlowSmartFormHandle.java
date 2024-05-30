/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jober.FlowSmartFormService;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.service.AippStreamService;
import com.huawei.fit.jober.aipp.service.AppBuilderFormService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * aipp智能表单实现实现
 *
 * @author l00611472
 * @since 2023-12-25
 */
@Component
public class AippFlowSmartFormHandle implements FlowSmartFormService {
    private static final Logger log = Logger.get(AippFlowSmartFormHandle.class);
    private final AppBuilderFormService formService;
    private final AippStreamService aippStreamService;

    public AippFlowSmartFormHandle(@Fit AppBuilderFormService formService, @Fit AippStreamService aippStreamService) {
        this.formService = formService;
        this.aippStreamService = aippStreamService;
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
        String nodeId = (String) contexts.get(0).get(AippConst.BS_NODE_ID_KEY);
        Map<String, Object> businessData = Utils.getBusiness(contexts);
        log.debug("handleSmartForm nodeId {} businessData {}", nodeId, businessData);

        AppBuilderForm appBuilderForm = this.formService.selectWithId(sheetId);
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String instanceId = StringUtils.isEmpty(parentInstanceId)
                ? ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY))
                : parentInstanceId;
        Map<String, Object> form = new HashMap<>();
        form.put(AippConst.FORM_APPEARANCE_KEY, appBuilderForm.getAppearance());
        Map<String, Object> formDataMap = new HashMap<>();
        appBuilderForm.getFormProperties()
                .stream()
                .map(AppBuilderFormProperty::getName)
                .forEach(name -> formDataMap.put(name, businessData.getOrDefault(name, StringUtils.EMPTY)));
        form.put(AippConst.FORM_DATA_KEY, formDataMap);
        this.aippStreamService.send(instanceId, form);
    }
}
