/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.WaterFlowService;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.genericable.AippRunTimeService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 工具流接口实现
 *
 * @author 李鑫
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
        context.setAccount("dmx000000");
        context.setEmployeeNumber(null);
        context.setName("大模型");
        context.setOperatorIp("0:0:0:0:0:0:0:1");
        context.setSourcePlatform(StringUtils.EMPTY);
        context.setLanguage(StringUtils.EMPTY);
        return context;
    }

    private Map<String, Object> buildInitContext(Map<String, Object> inputParams) {
        return MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, inputParams).build();
    }
}
