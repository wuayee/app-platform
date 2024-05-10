/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.QADto;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.hllm.model.LlmModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 生成经营报告
 *
 * @author l00498867
 * @since 2024/3/20
 */
@Component
public class LLMGenerateOperationReport implements FlowableService {
    private static final Logger log = Logger.get(LLMGenerateOperationReport.class);

    private final LLMService llmService;

    public LLMGenerateOperationReport(LLMService llmService) {
        this.llmService = llmService;
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
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("LLMGenerateOperationReport businessData {}", businessData);

        String chatHistory = businessData.get(AippConst.INST_CHAT_HISTORY_KEY).toString();
        List<QADto> histories = JsonUtils.parseArray(chatHistory, QADto[].class);
        List<QADto> filteredHistories = histories.stream()
                .filter(history -> this.isLegalAnswer(history.getAnswer()))
                .collect(Collectors.toList());
        filteredHistories.stream().map(QADto::getAnswer).forEach(a -> {
            a.setType(AippConst.ANSWER_TYPE);
            a.setChartSummary(this.buildChartSummaryList(a.getChartData(), a.getChartAnswer()));
        });
        businessData.put(AippConst.INST_OPERATION_REPORT_KEY, JsonUtils.toJsonString(filteredHistories));
        return flowData;
    }

    private boolean isLegalAnswer(QADto.Answer answer) {
        return CollectionUtils.isNotEmpty(answer.getChartData()) && CollectionUtils.isNotEmpty(answer.getChartAnswer());
    }

    private List<String> buildChartSummaryList(List<Map<String, Object>> chartDataList, List<String> chartAnswerList) {
        List<String> chartSummaryList = new ArrayList<>();
        String promptPrefix = "给下面的问题和答案做个总结：\n\n";
        for (int i = chartAnswerList.size(); i < chartDataList.size(); ++i) {
            chartAnswerList.add(chartAnswerList.get(i - 1));
        }
        for (int i = 0; i < chartDataList.size(); i++) {
            String charJsonString = JsonUtils.toJsonString(chartDataList.get(i));
            String answer = chartAnswerList.get(i);
            String prompt = promptPrefix + answer + "\n" + charJsonString;
            try {
                String chartSummary = this.llmService.askModelWithText(prompt, 20000, 0.0, LlmModel.QWEN_72B);
                chartSummaryList.add(chartSummary);
            } catch (IOException e) {
                log.error("ask model failed.", e);
                throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "ask model failed.");
            }
        }
        return chartSummaryList;
    }
}
