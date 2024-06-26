/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 任意genericable调用的测试
 *
 * @author s00558940
 * @since 2024/4/23
 */
class FlowGenericableJoberTest {
    private BrokerClient brokerClient;

    private Router router;

    private Invoker invoker;

    private FlowGenericableJober target;

    private FlowDataConverter converter;

    @BeforeEach
    void before() {
        brokerClient = mock(BrokerClient.class);
        invoker = mock(Invoker.class);
        router = mock(Router.class);
        converter = mock(FlowDataConverter.class);

        when(invoker.communicationType(any())).thenReturn(invoker);
        when(router.route(any())).thenReturn(invoker);
        when(brokerClient.getRouter((String) any())).thenReturn(router);

        target = new FlowGenericableJober();
        target.setBrokerClient(brokerClient);
        target.setFitables(new HashSet<>(Arrays.asList("fitableId")));
        target.loadFitableId();
        target.setGenericableConfig(
                FlowGenericableJober.GenericableConfig.builder().id("xxx").params(Arrays.asList("name")).build());
        target.setConverter(converter);
        target.setProperties(new HashMap<>());
    }

    @Test
    @DisplayName("执行genericableJober调用并输出结果的成功场景")
    void shouldReturnResultWhenExecuteGivenBrokerClientInvokeSuccess() throws Throwable {
        when(router.route(argThat(filter -> filter instanceof FitableIdFilter))).thenReturn(invoker);
        String expectGenericableResult = "helloWorld";
        when(invoker.invoke(any())).thenReturn(expectGenericableResult);
        when(converter.convertOutput(any())).thenAnswer(invocation -> {
            Map<String, Object> map = new HashMap<>();
            map.put("_result", invocation.getArgument(0));
            return map;
        });

        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        List<FlowData> result = target.executeJober(Collections.singletonList(flowData));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectGenericableResult, result.get(0).getBusinessData().get("_result"));
    }
}