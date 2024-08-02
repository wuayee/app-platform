/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.finance.AutoGraph;
import com.huawei.fit.finance.NlRouter;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fit.jober.aipp.fitable.finance.DefaultFinanceService;
import com.huawei.fit.jober.aipp.util.JsonUtils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * DefaultFinanceService测试类
 *
 * @since 2024-07-31
 */
public class FinanceServiceTest {
    private static final HttpClassicClientFactory FACTORY_MOCK =
            mock(HttpClassicClientFactory.class, RETURNS_DEEP_STUBS);

    @Test
    void testGetNlRoute() {
        DefaultFinanceService financeService = new DefaultFinanceService(FACTORY_MOCK);
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(FACTORY_MOCK.create().createRequest(any(), any())).thenReturn(requestMock);
        TextEntity textEntityMock = mock(TextEntity.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.textEntity()).thenReturn(Optional.of(textEntityMock));
        when(textEntityMock.content())
                .thenReturn("{\"result\":\"result\", \"matched\": true, \"complete_query\":\"query\"}");
        NlRouter nlRouter = financeService.nlRouter("query");
        assertEquals("result", nlRouter.getResult());
        assertTrue(nlRouter.isMatched());
        assertEquals("query", nlRouter.getCompleteQuery());
    }

    @Test
    void testAutoGraph() {
        DefaultFinanceService financeService = new DefaultFinanceService(FACTORY_MOCK);
        HttpClassicClientRequest requestMock = mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> responseMock = mock(HttpClassicClientResponse.class);
        when(FACTORY_MOCK.create().createRequest(any(), any())).thenReturn(requestMock);
        TextEntity textEntityMock = mock(TextEntity.class);
        when(requestMock.exchange()).thenReturn(responseMock);
        when(responseMock.statusCode()).thenReturn(200);
        when(responseMock.textEntity()).thenReturn(Optional.of(textEntityMock));
        when(textEntityMock.content())
                .thenReturn("{\"answer\":\"question-----answer\", \"data\": \"data\", \"title\":\"title\"}");
        String res = financeService.autoGraph("cond", "query");
        AutoGraph expected = new AutoGraph();
        expected.setChartData(null);
        expected.setChartType(null);
        expected.setChartTitle(Collections.singletonList("title"));
        expected.setChartAnswer(Arrays.asList("question", "answer"));
        expected.setAnswer("questionanswer");
        assertEquals(JsonUtils.toJsonString(expected), res);
    }
}
