/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowSmartFormService;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;

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
    private final MetaInstanceService metaInstanceService;

    public AippFlowSmartFormHandle(@Fit MetaInstanceService metaInstanceService) {
        this.metaInstanceService = metaInstanceService;
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

        InstanceDeclarationInfo declarationInfo = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_CURR_FORM_ID_KEY, sheetId)
                .putInfo(AippConst.INST_CURR_FORM_VERSION_KEY, "1.0.0")
                .putInfo(AippConst.INST_CURR_NODE_ID_KEY, nodeId)
                .build();

        this.metaInstanceService.patchMetaInstance((String) businessData.get(AippConst.BS_AIPP_ID_KEY),
                (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY),
                declarationInfo,
                JsonUtils.parseObject((String) businessData.get(AippConst.BS_HTTP_CONTEXT_KEY),
                        OperationContext.class));
    }
}
