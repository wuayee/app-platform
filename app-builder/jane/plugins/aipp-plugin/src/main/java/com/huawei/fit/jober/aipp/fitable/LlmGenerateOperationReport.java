/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.service.ReportGenerationService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;

import java.util.List;
import java.util.Map;

/**
 * 生成经营报告
 *
 * @author 李鑫
 * @since 2024/3/20
 */
@Component
public class LlmGenerateOperationReport implements FlowableService {
    private static final Logger log = Logger.get(LlmGenerateOperationReport.class);

    private final ReportGenerationService reportGenerationService;

    public LlmGenerateOperationReport(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    /**
     * 根据聊天记录生成经营报告的实现
     *
     * @param flowData 流程执行上下文数据，包含聊天记录的数据
     * @return 流程执行上下文数据，包含生成的经营报告
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMGenerateOperationReport")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LLMGenerateOperationReport businessData {}", businessData);

        String chatHistory = businessData.get(AippConst.INST_CHAT_HISTORY_KEY).toString();
        String operationReport = this.reportGenerationService.generateOperationReport(chatHistory);
        businessData.put(AippConst.INST_OPERATION_REPORT_KEY, operationReport);
        return flowData;
    }
}
