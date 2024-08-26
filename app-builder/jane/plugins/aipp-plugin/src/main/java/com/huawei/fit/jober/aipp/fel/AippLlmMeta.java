/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fel;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.util.DataUtils;

import lombok.Getter;
import lombok.Setter;
import modelengine.fel.chat.Prompt;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * AippLlmMeta
 *
 * @author 易文渊
 * @since 2024-04-24
 */
@Getter
public class AippLlmMeta {
    private List<Map<String, Object>> flowData;
    private Map<String, Object> businessData;
    private OperationContext context;
    private String versionId;
    private String instId;
    private String flowTraceId;
    private String flowDefinitionId;

    @Setter
    private Prompt trace;

    private AippLlmMeta() {}

    /**
     * 根据businessData解析大模型节点元数据。
     *
     * @param flowData 表示携带元数据的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code ,}
     *                               {@link Object}{@code >}{@code >}。
     * @param metaService 表示元数据服务的 {@link MetaService}。
     * @return 返回表示大模型节点元数据的 {@link AippLlmMeta}。
     */
    public static AippLlmMeta parse(List<Map<String, Object>> flowData, MetaService metaService) {
        AippLlmMeta aippLlmMeta = new AippLlmMeta();
        aippLlmMeta.flowData = flowData;
        aippLlmMeta.businessData = DataUtils.getBusiness(flowData);
        aippLlmMeta.versionId = ObjectUtils.cast(aippLlmMeta.businessData.get(AippConst.BS_META_VERSION_ID_KEY));
        aippLlmMeta.instId = ObjectUtils.cast(aippLlmMeta.businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        aippLlmMeta.context = DataUtils.getOpContext(aippLlmMeta.businessData);
        aippLlmMeta.flowTraceId = DataUtils.getFlowTraceId(flowData);
        aippLlmMeta.flowDefinitionId = DataUtils.getFlowDefinitionId(aippLlmMeta.businessData, metaService);
        return aippLlmMeta;
    }
}