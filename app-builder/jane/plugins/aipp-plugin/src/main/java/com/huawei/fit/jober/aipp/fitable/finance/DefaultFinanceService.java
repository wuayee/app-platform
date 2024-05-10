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
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;

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
    @Fitable("default")
    @Override
    public NLRouter nlRouter(String query) {
        HttpPost httpPost = new HttpPost("http://51.36.52.241:7861/v1/emodelchain/router");
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
        HttpPost httpPost = new HttpPost("http://51.36.52.241:31502/v1/emodelchain/autograph");
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

    private void formatChartAnswer(List<String> chartAnswer) {
        chartAnswer.forEach(ca -> {
            if (Objects.equals(ca, " ")) {
                ca.trim();
            }
        });
    }
}