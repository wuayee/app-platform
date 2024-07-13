/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.tianzhou.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fitframework.plugin.Plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TianzhouFlowsControllerTest
 *
 * @author lwx1291633
 * @since 2023/11/03
 */
@ExtendWith(MockitoExtension.class)
class TianzhouFlowsControllerTest {
    @Mock
    private FlowsController mockFlowsController;

    @Mock
    private Plugin mockPlugin;

    private TianzhouFlowsController tianzhouFlowsControllerUnderTest;

    @BeforeEach
    void setUp() {
        tianzhouFlowsControllerUnderTest = new TianzhouFlowsController(mockFlowsController, mockPlugin);
    }

    @Test
    void testCreateFlows() {
        // Setup
        when(mockFlowsController.createFlows(any(HttpClassicServerRequest.class), any(HttpClassicServerResponse.class),
                eq("tenantId"), anyMap())).thenReturn(new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowsControllerUnderTest.createFlows(null, null, "tenantId",
                new HashMap<>());
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testUpdateFlows() {
        // Setup
        when(mockFlowsController.updateFlows(any(HttpClassicServerRequest.class), any(HttpClassicServerResponse.class),
                eq("tenantId"), eq("flowId"), anyMap())).thenReturn(new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowsControllerUnderTest.updateFlows(null, null, "tenantId",
                "flowId", new HashMap<>());
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindFlows() {
        // Setup
        when(mockFlowsController.findFlowsById(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("flowId"))).thenReturn(new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowsControllerUnderTest.findFlowsById(null, null, "tenantId",
                "flowId");
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindFlowsByTenant() {
        // Setup
        when(mockFlowsController.findFlowsByTenant(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"))).thenReturn(Arrays.asList(new HashMap<>()));

        // Run the test
        final Map<String, Object> result = tianzhouFlowsControllerUnderTest.findFlowsByTenant(null, null, "tenantId");
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindFlowsByTenant_FlowsControllerReturnsNoItems() {
        // Setup
        when(mockFlowsController.findFlowsByTenant(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"))).thenReturn(Collections.emptyList());

        // Run the test
        final Map<String, Object> result = tianzhouFlowsControllerUnderTest.findFlowsByTenant(null, null, "tenantId");
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindFlowDefinitionByFlowVersion() {
        // Setup
        when(mockFlowsController.findFlowsByName(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("name"), eq("version"))).thenReturn(
                new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowsControllerUnderTest.findFlowsByName(null, null, "tenantId",
                "name", "version");
        Assertions.assertEquals(-1, result.get("code"));
    }

    @Test
    void testFindFlowDefinitionByMetaIdAndVersion() {
        // Setup
        when(mockFlowsController.findFlowsByMetaIdAndVersion(any(HttpClassicServerRequest.class),
                any(HttpClassicServerResponse.class), eq("tenantId"), eq("metaId"), eq("version"))).thenReturn(
                new HashMap<>());

        // Run the test
        final Map<String, Object> result = tianzhouFlowsControllerUnderTest.findFlowsByMetaIdAndVersion(null, null,
                "tenantId", "metaId", "version");
        Assertions.assertEquals(-1, result.get("code"));
    }
}
