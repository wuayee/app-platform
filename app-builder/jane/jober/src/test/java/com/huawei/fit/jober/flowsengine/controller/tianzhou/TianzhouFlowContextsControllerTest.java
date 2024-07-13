/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.controller.tianzhou;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.jober.flowsengine.controller.FlowContextsController;
import com.huawei.fit.waterflow.biz.common.vo.FlowDataVO;
import com.huawei.fitframework.plugin.Plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthorizationController
 *
 * @author l00815032
 * @since 2023/11/28
 */
@ExtendWith(MockitoExtension.class)
class TianzhouFlowContextsControllerTest {
    @Mock
    private FlowContextsController mockFlowContextsController;

    @Mock
    private Plugin mockPlugin;

    private TianzhouFlowContextsController tianzhouFlowContextsControllerUnderTest;

    @BeforeEach
    void setUp() {
        tianzhouFlowContextsControllerUnderTest = new TianzhouFlowContextsController(mockFlowContextsController,
                mockPlugin);
    }

    @Test
    void testStartFlows() {
        // Setup
        when(mockFlowContextsController.startFlows(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("flowId"), any())).thenReturn(
                new HashMap<>());
        // Run the test
        final Map<String, Object> result = tianzhouFlowContextsControllerUnderTest.startFlows(null, null, "tenantId",
                "flowId", new FlowDataVO());
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindStartFlowContexts() {
        // Setup
        when(mockFlowContextsController.findStartNodeContexts(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("metaId"), eq("version"))).thenReturn(
                new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowContextsControllerUnderTest.findStartNodeContexts(null, null,
                "tenantId", "metaId", "version");
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindStartFlowContexts_FlowContextsControllerReturnsNoItems() {
        // Setup
        when(mockFlowContextsController.findStartNodeContexts(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("metaId"), eq("version"))).thenReturn(
                new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowContextsControllerUnderTest.findStartNodeContexts(null, null,
                "tenantId", "metaId", "version");
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindNodeFlowContexts() {
        // Setup
        when(mockFlowContextsController.findNodeContexts(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("metaId"), eq("version"),
                eq("nodeId"))).thenReturn(new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowContextsControllerUnderTest.findNodeContexts(null, null,
                "tenantId", "metaId", "version", "nodeId");
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindNodeFlowContexts_FlowContextsControllerReturnsNoItems() {
        // Setup
        when(mockFlowContextsController.findNodeContexts(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("metaId"), eq("version"),
                eq("nodeId"))).thenReturn(new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowContextsControllerUnderTest.findNodeContexts(null, null,
                "tenantId", "metaId", "version", "nodeId");
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindFlowContextStatusView() {
        // Setup
        when(mockFlowContextsController.findContextStatusViewCount(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("metaId"), eq("version"))).thenReturn(
                new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowContextsControllerUnderTest.findContextStatusViewCount(null,
                null, "tenantId", "metaId", "version");
        Assertions.assertEquals(-1, result.get("code"));
    }
}
