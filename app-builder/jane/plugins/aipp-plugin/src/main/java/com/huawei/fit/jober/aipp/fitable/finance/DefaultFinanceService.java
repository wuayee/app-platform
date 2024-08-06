/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable.finance;

import static com.huawei.fit.jober.aipp.util.HttpUtils.sendHttpRequest;

import com.huawei.fit.finance.AutoGraph;
import com.huawei.fit.finance.ChartType;
import com.huawei.fit.finance.FinanceService;
import com.huawei.fit.finance.NlRouter;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;

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
    private HttpClassicClientFactory httpClientFactory;

    /**
     * 构造函数，用于创建DefaultFinanceService对象
     *
     * @param httpClientFactory 表示http客户端工厂的{@link HttpClassicClientFactory}
     */
    public DefaultFinanceService(HttpClassicClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @Fitable("default")
    @Override
    public NlRouter nlRouter(String query) {
        // 临时方案，暂时走模型网关转发，待整改
        String body = JsonUtils.toJsonString(MapBuilder.<String, String>get().put("query", query).build());
        HttpClassicClientRequest postRequest = httpClientFactory.create().createRequest(HttpRequestMethod.POST,
                "http://tzaip-beta.paas.huawei.com/model-gateway/v1/emodelchain/router");
        postRequest.entity(ObjectEntity.create(postRequest, body));
        NlRouter nlRouter = new NlRouter();
        try {
            String jsonString = sendHttpRequest(postRequest);
            Map<String, Object> map = JsonUtils.parseObject(jsonString);
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
        String body = JsonUtils.toJsonString(MapBuilder.<String, String>get()
                .put("sql", condition)
                .put("query", query)
                .build());
        HttpClassicClientRequest postRequest = httpClientFactory.create().createRequest(HttpRequestMethod.POST,
                "http://tzaip-beta.paas.huawei.com/model-gateway/v1/emodelchain/autograph");
        postRequest.entity(ObjectEntity.create(postRequest, body));
        AutoGraph autoGraph = new AutoGraph();
        try {
            Map<String, Object> map = JsonUtils.parseObject(sendHttpRequest(postRequest));
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
            } else {
                if (title instanceof List) {
                    autoGraph.setChartTitle((List<String>) title);
                }
            }
            Object type = ObjectUtils.cast(map.get("type"));
            if (type instanceof List) {
                autoGraph.setChartType(((List<String>) type)
                        .stream().map(ChartType::from).collect(Collectors.toList()));
            }
            List<String> chartAnswer = new ArrayList<>(Arrays.asList(autoGraph.getAnswer().split("-----")));
            this.formatChartAnswer((chartAnswer));
            autoGraph.setAnswer(autoGraph.getAnswer().replaceAll("-----", ""));
            autoGraph.setChartAnswer(chartAnswer);
            return JsonUtils.toJsonString(autoGraph);
        } catch (IOException e) {
            throw new FitException(e);
        }
    }

    private void formatChartAnswer(List<String> chartAnswer) {
        chartAnswer.forEach(ca -> {
            if (Objects.equals(ca, " ")) {
                ca.trim();
            }
        });
    }
}