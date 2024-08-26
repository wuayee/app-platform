/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import com.huawei.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import com.huawei.fit.jober.aipp.service.AippFlowRuntimeInfoService;
import com.huawei.fit.jober.aipp.service.impl.AippFlowRuntimeInfoServiceImpl;
import com.huawei.fit.jober.common.RangeResult;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.entity.consts.NodeTypes;
import com.huawei.fit.runtime.entity.Parameter;
import com.huawei.fit.runtime.entity.RuntimeData;

import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * runtimeInfo服务测试用例.
 *
 * @author 张越
 * @since 2024-07-29
 */
@ExtendWith(MockitoExtension.class)
public class AippFlowRuntimeInfoServiceTest {
    @Mock
    private MetaService metaService;

    @Mock
    private MetaInstanceService metaInstanceService;

    @Mock
    private AppBuilderRuntimeInfoRepository repository;

    private AippFlowRuntimeInfoService service;

    /**
     * 初始化.
     */
    @BeforeEach
    void setUp() {
        this.service = new AippFlowRuntimeInfoServiceImpl(this.metaService, this.metaInstanceService, this.repository);
    }

    /**
     * 测试用例.
     */
    @Test
    void shouldOptionalEmptyWhenNoRuntimeInfo() {
        // before
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, "app1");
        Meta meta = new Meta();
        meta.setAttributes(attributes);
        meta.setVersionId("version1");
        RangedResultSet<Meta> metaRangedResultSet = new RangedResultSet<>();
        metaRangedResultSet.setResults(Collections.singletonList(meta));
        doReturn(metaRangedResultSet).when(this.metaService)
                .list(any(MetaFilter.class), anyBoolean(), anyLong(), anyInt(), any(OperationContext.class),
                        any(MetaFilter.class));

        Map<String, String> info = new HashMap<>();
        info.put(AippConst.INST_FLOW_INST_ID_KEY, "trace1");
        Instance instance = new Instance();
        instance.setInfo(info);
        RangedResultSet<Instance> resultSet = new RangedResultSet<>();
        resultSet.setResults(Collections.singletonList(instance));
        resultSet.setRange(new RangeResult(10, 10, 10));
        doReturn(resultSet).when(this.metaInstanceService)
                .list(anyString(), any(MetaInstanceFilter.class), anyLong(), anyInt(), any(OperationContext.class));

        doReturn(Collections.emptyList()).when(this.repository).selectByTraceId(anyString());

        // when
        Optional<RuntimeData> optionalRuntimeData = this.service.getRuntimeData("app1", "", "instance1",
                new OperationContext());

        // then
        assertFalse(optionalRuntimeData.isPresent());
    }

    /**
     * 测试用例.
     */
    @Test
    void shouldThrowIllegalStateExceptionWhenNoStartNodeInfo() {
        // before
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, "app1");
        Meta meta = new Meta();
        meta.setAttributes(attributes);
        meta.setVersionId("version1");
        RangedResultSet<Meta> metaRangedResultSet = new RangedResultSet<>();
        metaRangedResultSet.setResults(Collections.singletonList(meta));
        doReturn(metaRangedResultSet).when(this.metaService)
                .list(any(MetaFilter.class), anyBoolean(), anyLong(), anyInt(), any(OperationContext.class),
                        any(MetaFilter.class));

        Map<String, String> info = new HashMap<>();
        info.put(AippConst.INST_FLOW_INST_ID_KEY, "trace1");
        Instance instance = new Instance();
        instance.setInfo(info);
        RangedResultSet<Instance> resultSet = new RangedResultSet<>();
        resultSet.setResults(Collections.singletonList(instance));
        resultSet.setRange(new RangeResult(10, 10, 10));
        doReturn(resultSet).when(this.metaInstanceService)
                .list(anyString(), any(MetaInstanceFilter.class), anyLong(), anyInt(), any(OperationContext.class));

        List<AppBuilderRuntimeInfo> infos = new ArrayList<>();
        infos.add(AppBuilderRuntimeInfo.builder().nodeType(NodeTypes.STATE.getType()).build());
        doReturn(infos).when(this.repository).selectByTraceId(anyString());

        // when
        // then
        assertThrows(IllegalStateException.class, () -> {
            this.service.getRuntimeData("app1", "", "instance1", new OperationContext());
        });
    }

    /**
     * 测试用例.
     */
    @Test
    void shouldBeNormalWhenInfosIsRight() {
        // before
        mockData();

        // when
        Optional<RuntimeData> optionalRuntimeData = this.service.getRuntimeData("app1", "", "instance1",
                new OperationContext());

        // then
        assertTrue(optionalRuntimeData.isPresent());
        RuntimeData runtimeData = optionalRuntimeData.get();
        assertEquals(runtimeData.getTraceId(), "trace1");
        assertEquals(runtimeData.getFlowDefinitionId(), "flow1");
        assertEquals(runtimeData.getStartTime(), 1);
        assertEquals(runtimeData.getEndTime(), 31);
        assertTrue(runtimeData.isPublished());
        assertEquals(runtimeData.getAippInstanceId(), "instance1");
        assertEquals(runtimeData.getExecuteTime(), 30);
        assertEquals(runtimeData.getNodeInfos().get(0).getNodeId(), "node1");
        assertEquals(runtimeData.getNodeInfos().get(0).getNodeType(), NodeTypes.START.getType());
        assertEquals(runtimeData.getNodeInfos().get(0).getStartTime(), 1);
        assertEquals(runtimeData.getNodeInfos().get(0).getRunCost(), 9);
        assertEquals(runtimeData.getNodeInfos().get(0).getStatus(), FlowNodeStatus.ARCHIVED.name());
        assertEquals(runtimeData.getNodeInfos().get(0).getParameters().get(0).getInput(), "1");
        assertEquals(runtimeData.getNodeInfos().get(0).getParameters().get(0).getOutput(), "2");
        assertEquals(runtimeData.getNodeInfos().get(1).getNodeId(), "node2");
        assertEquals(runtimeData.getNodeInfos().get(1).getNodeType(), NodeTypes.END.getType());
        assertEquals(runtimeData.getNodeInfos().get(1).getStartTime(), 11);
        assertEquals(runtimeData.getNodeInfos().get(1).getRunCost(), 20);
        assertEquals(runtimeData.getNodeInfos().get(1).getStatus(), FlowNodeStatus.ERROR.name());
        assertEquals(runtimeData.getNodeInfos().get(1).getErrorMsg(), "11111");
        assertEquals(runtimeData.getNodeInfos().get(1).getParameters().get(0).getInput(), "3");
        assertEquals(runtimeData.getNodeInfos().get(1).getParameters().get(0).getOutput(), "4");
    }

    private void mockData() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, "app1");
        Meta meta = new Meta();
        meta.setAttributes(attributes);
        meta.setVersionId("version1");
        RangedResultSet<Meta> metaRangedResultSet = new RangedResultSet<>();
        metaRangedResultSet.setResults(Collections.singletonList(meta));
        doReturn(metaRangedResultSet).when(this.metaService)
                .list(any(MetaFilter.class), anyBoolean(), anyLong(), anyInt(), any(OperationContext.class),
                        any(MetaFilter.class));

        Map<String, String> info = new HashMap<>();
        info.put(AippConst.INST_FLOW_INST_ID_KEY, "trace1");
        Instance instance = new Instance();
        instance.setInfo(info);
        RangedResultSet<Instance> resultSet = new RangedResultSet<>();
        resultSet.setResults(Collections.singletonList(instance));
        resultSet.setRange(new RangeResult(10, 10, 10));
        doReturn(resultSet).when(this.metaInstanceService)
                .list(anyString(), any(MetaInstanceFilter.class), anyLong(), anyInt(), any(OperationContext.class));

        List<AppBuilderRuntimeInfo> infos = new ArrayList<>();
        Parameter startParameter = new Parameter();
        startParameter.setInput("1");
        startParameter.setOutput("2");
        infos.add(AppBuilderRuntimeInfo.builder().startTime(1).endTime(10).flowDefinitionId("flow1").traceId("trace1")
                .nodeId("node1").instanceId("instance1").nodeType(NodeTypes.START.getType())
                .status(FlowNodeStatus.ARCHIVED.name()).published(true)
                .parameters(Collections.singletonList(startParameter))
                .build());

        Parameter endParameter = new Parameter();
        endParameter.setInput("3");
        endParameter.setOutput("4");
        infos.add(AppBuilderRuntimeInfo.builder().startTime(11).endTime(31).flowDefinitionId("flow1").traceId("trace1")
                .nodeId("node2").instanceId("instance1").nodeType(NodeTypes.END.getType()).errorMsg("11111")
                .status(FlowNodeStatus.ERROR.name()).published(true).parameters(Collections.singletonList(endParameter))
                .build());
        doReturn(infos).when(this.repository).selectByTraceId(anyString());
    }
}
