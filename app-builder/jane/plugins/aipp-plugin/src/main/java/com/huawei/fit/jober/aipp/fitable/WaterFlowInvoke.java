/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.WaterFlowService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.genericable.AippRunTimeService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 工具流接口实现
 *
 * @author l00498867
 * @since 2024/4/24
 */
@Component
public class WaterFlowInvoke implements WaterFlowService {
    private final AippRunTimeService aippRunTimeService;

    public WaterFlowInvoke(@Fit AippRunTimeService aippRunTimeService) {
        this.aippRunTimeService = aippRunTimeService;
    }

    @Override
    @Fitable(id = "water.flow.invoke")
    public String invoke(String tenantId, String aippId, String version, Map<String, Object> inputParams) {
        Map<String, Object> initContext = this.buildInitContext(inputParams);
        return this.aippRunTimeService.createAippInstance(aippId,
                version,
                initContext,
                this.buildOperationContext(tenantId, initContext));
    }

    private OperationContext buildOperationContext(String tenantId, Map<String, Object> initContext) {
        Map<String, Object> businessData = (Map<String, Object>) initContext.get(AippConst.BS_INIT_CONTEXT_KEY);
        String userId = ObjectUtils.cast(businessData.getOrDefault(AippConst.CONTEXT_USER_ID, StringUtils.EMPTY));
        OperationContext context = new OperationContext();
        context.setTenantId(tenantId);
        context.setOperator(userId);
        context.setGlobalUserId(null);
        context.setW3Account("dmx000000");
        context.setEmployeeNumber(null);
        context.setName("大模型");
        context.setOperatorIp("0:0:0:0:0:0:0:1");
        context.setSourcePlatform(StringUtils.EMPTY);
        context.setLanguage(StringUtils.EMPTY);
        return context;
    }

    private Map<String, Object> buildInitContext(Map<String, Object> inputParams) {
        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, inputParams).build();
        Map<String, Object> businessData = (Map<String, Object>) initContext.get(AippConst.BS_INIT_CONTEXT_KEY);
        if (businessData.containsKey(AippConst.TRACE_ID) && businessData.containsKey(AippConst.CALLBACK_ID)) {
            String traceId = ObjectUtils.cast(businessData.remove(AippConst.TRACE_ID));
            businessData.put(AippConst.PARENT_INSTANCE_ID, traceId);
            String callbackId = ObjectUtils.cast(businessData.remove(AippConst.CALLBACK_ID));
            businessData.put(AippConst.PARENT_CALLBACK_ID, callbackId);
        }
        return initContext;
    }
}
