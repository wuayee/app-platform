/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.util;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowContextPO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The ViewsTest
 *
 * @author 肖峰
 * @since 2023/11/1
 */
class ViewsTest {
    @Test
    @DisplayName("测试返回视图中去掉isDeleted字段成功")
    void viewOfFlowGraphDefinitionSuccess() {
        FlowGraphDefinition flowGraphDefinition = buildFlowGraphDefinition();
        Map<String, Object> map = Views.viewOfFlowGraphDefinition(flowGraphDefinition);
        Assertions.assertEquals(flowGraphDefinition.getFlowId(), map.getOrDefault("flowId", "error"));
        Assertions.assertEquals("error", map.getOrDefault("isDeleted", "error"));
    }

    @Test
    @DisplayName("测试null时成功回显空Map")
    void viewOfFlowGraphDefinitionNull() {
        Map<String, Object> map = Views.viewOfFlowGraphDefinition(null);
        Assertions.assertEquals(0, map.size());
    }

    @Test
    @DisplayName("测试返回视图中去掉isDeleted、tags、graphData与tenant字段成功")
    void viewOfFlowGraphList() {
        FlowGraphDefinition flowGraphDefinition = buildFlowGraphDefinition();
        List<FlowGraphDefinition> lists = new ArrayList<>();
        lists.add(flowGraphDefinition);
        Map<String, Object> map = Views.viewOfFlowGraphList(lists);
        List<Map<String, Object>> graphDefinitions = cast(map.get("flowList"));
        Map<String, Object> graphDefinition = graphDefinitions.get(0);
        Assertions.assertEquals("error", graphDefinition.getOrDefault("isDeleted", "error"));
        Assertions.assertEquals("error", graphDefinition.getOrDefault("graphData", "error"));
        Assertions.assertEquals("error", graphDefinition.getOrDefault("tags", "error"));
        Assertions.assertEquals("error", graphDefinition.getOrDefault("tenant", "error"));
    }

    @Test
    @DisplayName("测试null时成功回显空Map")
    void viewOfFlowGraphListNull() {
        Map<String, Object> map = Views.viewOfFlowGraphList(null);
        List<Map<String, Object>> graphDefinitions = cast(map.get("flowList"));
        Assertions.assertEquals(0, graphDefinitions.size());
    }

    private FlowGraphDefinition buildFlowGraphDefinition() {
        return FlowGraphDefinition.builder()
                .flowId("123")
                .name("name")
                .version("1.0.0")
                .status("unpublish")
                .graphData("data")
                .tags(new ArrayList<>())
                .createdBy("user")
                .createdAt(LocalDateTime.now())
                .updatedBy("user")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("流程实例上下文列表视图，FlowContext<String>")
    void testViewOfContextsForFlowContext() {
        // Setup
        FlowContext<String> context = new FlowContext<>("1", "1", "s", Collections.singleton("1"), "");
        List<FlowContext<String>> contexts = new ArrayList<>();
        contexts.add(context);

        // Run the test
        List<FlowContext<String>> result = cast(Views.viewOfContexts(contexts).get("contexts"));

        // Verify the results
        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.get(0).getStreamId());
        Assertions.assertEquals("s", result.get(0).getData());
    }

    @Test
    @DisplayName("流程实例上下文列表视图，FlowContextPO")
    void testViewOfContextsForFlowContextPO() {
        // Setup
        FlowContextPO context = FlowContextPO.builder()
                .streamId("1")
                .rootId("1")
                .flowData("s")
                .traceId("1")
                .positionId("1")
                .build();
        List<FlowContextPO> contexts = new ArrayList<>();
        contexts.add(context);

        // Run the test
        List<FlowContextPO> result = cast(Views.viewOfContexts(contexts).get("contexts"));

        // Verify the results
        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.get(0).getStreamId());
        Assertions.assertEquals("s", result.get(0).getFlowData());
    }
}