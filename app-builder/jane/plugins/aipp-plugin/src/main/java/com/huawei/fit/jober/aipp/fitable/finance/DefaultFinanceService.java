/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable.finance;

import static com.huawei.fit.jober.aipp.common.HttpUtils.sendHttpRequest;

import com.huawei.fit.finance.AutoGraph;
import com.huawei.fit.finance.ChartType;
import com.huawei.fit.finance.FinanceService;
import com.huawei.fit.finance.NLRouter;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.QADto;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.hllm.model.LlmModel;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DefaultFinanceService
 *
 * @author 易文渊
 * @since 2024-04-27
 */
@Component
public class DefaultFinanceService implements FinanceService {
    private static final Logger log = Logger.get(DefaultFinanceService.class);

    private final LLMService llmService;

    public DefaultFinanceService(LLMService llmService) {this.llmService = llmService;}

    @Fitable("default")
    @Override
    public NLRouter nlRouter(String query) {
        // todo: 临时方案，暂时走模型网关转发，待整改
        HttpPost httpPost = new HttpPost("http://tzaip-beta.paas.huawei.com/model-gateway/v1/emodelchain/router");
        String body = JsonUtils.toJsonString(MapBuilder.<String, String>get().put("query", query).build());
        httpPost.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        NLRouter nlRouter = new NLRouter();
        try {
            Map<String, Object> map = JsonUtils.parseObject(sendHttpRequest(httpPost));
            nlRouter.setResult(ObjectUtils.cast(map.get("result")));
            nlRouter.setMatched(ObjectUtils.cast(map.get("matched")));
            nlRouter.setCompleteQuery(ObjectUtils.cast(map.get("complete_query")));
        } catch (IOException e) {
            nlRouter.setMatched(false);
        }
        return nlRouter;
    }

    @Fitable("default")
    @Override
    public String autoGraph(String condition, String query) {
        HttpPost httpPost = new HttpPost("http://tzaip-beta.paas.huawei.com/model-gateway/v1/emodelchain/autograph");
        String body = JsonUtils.toJsonString(MapBuilder.<String, String>get()
                .put("sql", condition)
                .put("query", query)
                .build());
        httpPost.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        AutoGraph autoGraph = new AutoGraph();
        try {
            Map<String, Object> map = JsonUtils.parseObject(sendHttpRequest(httpPost));
            autoGraph.setAnswer(ObjectUtils.cast(map.get("answer")));
            Object data = map.get("data");
            if (data instanceof List) {
                autoGraph.setChartData(((List<?>) data)
                        .stream()
                        .map(JsonUtils::toJsonString)
                        .collect(Collectors.toList()));
            }
            Object title = map.get("title");
            if (title instanceof String) {
                autoGraph.setChartTitle(Collections.singletonList((String) title));
            } else if (title instanceof List) {
                autoGraph.setChartTitle((List<String>) title);
            }
            Object type = ObjectUtils.cast(map.get("type"));
            if (type instanceof List) {
                autoGraph.setChartType(((List<String>) type).stream().map(ChartType::from).collect(Collectors.toList()));
            }
            List<String> chartAnswer = new ArrayList<>(Arrays.asList(autoGraph.getAnswer().split("-----")));
            this.formatChartAnswer((chartAnswer));
            autoGraph.setAnswer(autoGraph.getAnswer().replaceAll("-----", ""));
            autoGraph.setChartAnswer(chartAnswer);
            return JsonUtils.toJsonString(autoGraph);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Fitable("default")
    @Override
    public String generateOperationReport(String chatHistory) {
        List<QADto> histories = JsonUtils.parseArray(chatHistory, QADto[].class);
        List<QADto> filteredHistories = histories.stream()
                .filter(h -> this.isLegalAnswer(h.getAnswer()))
                .collect(Collectors.toList());
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

    private void formatChartAnswer(List<String> chartAnswer) {
        chartAnswer.forEach(ca -> {
            if (Objects.equals(ca, " ")) {
                ca.trim();
            }
        });
    }
}