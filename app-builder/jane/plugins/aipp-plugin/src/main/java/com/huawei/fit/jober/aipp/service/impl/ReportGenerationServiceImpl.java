/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.QADto;
import com.huawei.fit.jober.aipp.service.ReportGenerationService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;

import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.ChatModelService;
import modelengine.fel.chat.ChatOptions;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.models.ChatBlockModel;
import modelengine.fel.engine.operators.prompts.Prompts;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 生成报告接口实现类。
 *
 * @author 李鑫
 * @since 2024-06-15
 */
@Component
public class ReportGenerationServiceImpl implements ReportGenerationService {
    private static final Logger log = Logger.get(ReportGenerationService.class);

    private final AiProcessFlow<Tip, ChatMessage> agentFlow;

    public ReportGenerationServiceImpl(ChatModelService chatModelService) {
        this.agentFlow = AiFlows.<Tip>create()
                .prompt(Prompts.human("给下面的问题和答案做个总结：\n\n{{content}}"))
                .generate(new ChatBlockModel(chatModelService).bind(ChatOptions.builder()
                        .model("Qwen-72B")
                        .temperature(0.0)
                        .build()))
                .close();
    }

    @Fitable("default")
    @Override
    public String generateOperationReport(String chatHistory) {
        List<QADto> histories = JsonUtils.parseArray(chatHistory, QADto[].class);
        List<QADto> filteredHistories =
                histories.stream().filter(h -> this.isLegalAnswer(h.getAnswer())).collect(Collectors.toList());
        filteredHistories.stream().map(QADto::getAnswer).forEach(a -> {
            a.setType(AippConst.ANSWER_TYPE);
            a.setChartSummary(this.buildChartSummaryList(a.getChartData(), a.getChartAnswer()));
        });
        return JsonUtils.toJsonString(filteredHistories);
    }

    private boolean isLegalAnswer(QADto.Answer answer) {
        return CollectionUtils.isNotEmpty(answer.getChartData()) && CollectionUtils.isNotEmpty(answer.getChartAnswer());
    }

    private List<String> buildChartSummaryList(List<Map<String, Object>> chartDataList, List<String> chartAnswerList) {
        List<String> chartSummaryList = new ArrayList<>();
        for (int i = chartAnswerList.size(); i < chartDataList.size(); ++i) {
            chartAnswerList.add(chartAnswerList.get(i - 1));
        }
        for (int i = 0; i < chartDataList.size(); i++) {
            String charJsonString = JsonUtils.toJsonString(chartDataList.get(i));
            String answer = chartAnswerList.get(i);
            AtomicReference<Boolean> errorFlag = new AtomicReference<>(false);
            agentFlow.converse().doOnSuccess(msg -> chartSummaryList.add(msg.text())).doOnError(throwable -> {
                errorFlag.set(true);
                log.error("ask model failed.", throwable.getMessage());
            }).offer(Tip.from("content", answer + "\n" + charJsonString)).await(60, TimeUnit.SECONDS);
            if (errorFlag.get()) {
                throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "ask model failed.");
            }
        }
        return chartSummaryList;
    }
}
