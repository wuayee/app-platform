/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.jane.flow.graph.repo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.huawei.fit.elsa.generable.GraphExposeService;
import com.huawei.fit.elsa.generable.entity.BatchResult;
import com.huawei.fit.elsa.generable.entity.DocumentVO;
import com.huawei.fit.elsa.generable.entity.Result;
import com.huawei.fit.jane.flow.graph.client.ElsaClient;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphQueryParam;
import com.huawei.fit.jane.flow.graph.entity.FlowSaveEntity;
import com.huawei.fit.jane.flow.graph.entity.elsa.GraphParam;
import com.huawei.fitframework.model.RangedResultSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * ElsaFlowsGraphRepo相关测试
 *
 * @author yangxiangyu
 * @since 2024/8/8
 */
class ElsaFlowsGraphRepoTest {
    private ElsaFlowsGraphRepo elsaFlowsGraphRepo;

    private ElsaClient elsaClient;

    private final GraphExposeService graphExposeService = Mockito.mock(GraphExposeService.class);

    private String endpoint;

    private String elsaAccessKey;

    @BeforeEach
    void setUp() {
        elsaFlowsGraphRepo = new ElsaFlowsGraphRepo(elsaClient, graphExposeService, endpoint, elsaAccessKey);
    }

    @Test
    @DisplayName("更新流程到elsa成功")
    void testUpgradeFlowsSuccessful() {
        GraphParam graphParam = new GraphParam();
        graphParam.setGraphId("123");
        graphParam.setJson("");
        graphParam.setVersion("1.0.0");
        Result<Integer> res = new Result<>();
        res.setCode(1);

        when(graphExposeService.upgrade(any())).thenReturn(res);
        int ans = elsaFlowsGraphRepo.upgradeFlows(graphParam);

        Assertions.assertEquals(1, ans);
    }

    @Test
    @DisplayName("获取流程成功")
    void testGetFlowSuccessful() {
        FlowSaveEntity flowSaveEntity = FlowSaveEntity.builder().build();
        when(graphExposeService.get(any(), any())).thenReturn("123");

        String flow = elsaFlowsGraphRepo.getFlow(flowSaveEntity, null);

        Assertions.assertEquals("123", flow);
    }

    @Test
    @DisplayName("测试获取流程列表成功")
    void TestGetFlowListSuccessful() {
        FlowGraphQueryParam queryParam = FlowGraphQueryParam.builder().build();
        BatchResult<DocumentVO> result = new BatchResult<>();
        List<DocumentVO> documentVOS = new ArrayList<>();
        documentVOS.add(new DocumentVO());
        result.setData(documentVOS);
        result.setCount(1L);
        result.setCursor(1);
        result.setSize(1);
        when(graphExposeService.pagingDocumentsByIds(any())).thenReturn(result);

        RangedResultSet<FlowGraphDefinition> flowList = elsaFlowsGraphRepo.getFlowList(queryParam, null);

        Assertions.assertEquals(1, flowList.getResults().size());
    }
}