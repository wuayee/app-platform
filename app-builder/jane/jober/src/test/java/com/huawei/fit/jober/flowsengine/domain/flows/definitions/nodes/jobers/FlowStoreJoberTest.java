/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers;

import static com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowStoreJober.TOOL_EXECUTE_GENERICABLE_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import com.huawei.fitframework.broker.CommunicationType;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.broker.client.filter.route.DefaultFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * store工具调用的测试
 *
 * @author s00558940
 * @since 2024/5/09
 */
class FlowStoreJoberTest {
    public static final String TOOL_ID = "toolId";

    private BrokerClient brokerClient;

    private Router router;

    private Invoker invoker;

    private FlowStoreJober target;

    private FlowDataConverter converter;

    @BeforeEach
    void before() {
        brokerClient = mock(BrokerClient.class);
        router = mock(Router.class);
        invoker = mock(Invoker.class);
        converter = mock(FlowDataConverter.class);

        target = new FlowStoreJober();
        target.setBrokerClient(brokerClient);
        target.setServiceMeta(
                FlowStoreJober.ServiceMeta.builder().uniqueName(TOOL_ID).params(Arrays.asList("name")).build());
        target.setConverter(converter);
        target.setProperties(new HashMap<>());
    }

    @Test
    @DisplayName("执行storeJober调用并输出结果的成功场景")
    void shouldReturnResultWhenExecuteGivenBrokerClientInvokeSuccess() {
        when(brokerClient.getRouter(TOOL_EXECUTE_GENERICABLE_ID)).thenReturn(router);
        when(invoker.communicationType(CommunicationType.ASYNC)).thenReturn(invoker);
        when(router.route(any(DefaultFilter.class))).thenReturn(invoker);
        String expectArgs = "{\"name\":\"hello\"}";
        String expectToolResult = "tool result";
        when(invoker.invoke(any())).thenReturn("\"" + expectToolResult + "\"");
        when(converter.convertOutput(any())).thenAnswer(invocation -> {
            Map<String, Object> map = new HashMap<>();
            map.put("toolResult", invocation.getArgument(0));
            return map;
        });

        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        flowData.getBusinessData().put("name", "hello");
        List<FlowData> result = target.executeJober(Collections.singletonList(flowData));

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
        verify(invoker).invoke(arg1.capture(), arg2.capture());
        Assertions.assertEquals(TOOL_ID, arg1.getValue());
        Assertions.assertEquals(expectArgs, arg2.getValue());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectToolResult, result.get(0).getBusinessData().get("toolResult"));
    }
}