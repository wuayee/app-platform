/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.huawei.fit.dynamicform.DynamicFormMetaService;
import com.huawei.fit.dynamicform.DynamicFormService;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.FlowInstanceService;
import com.huawei.fit.jober.FlowsService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.impl.AippRunTimeServiceImpl;
import com.huawei.fit.jober.common.RangeResult;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.jober.entity.FlowNodeInfo;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 为{@link com.huawei.fit.jober.aipp.service.impl.AippRunTimeServiceImpl} 提供测试
 *
 * @author 姚江
 * @since 2024-08-13
 */
@ExtendWith(MockitoExtension.class)
public class AippRuntimeServiceImplTest {
    @InjectMocks
    private AippRunTimeServiceImpl aippRunTimeService;

    @Mock
    private AopAippLogService aopAippLogServiceMock;
    @Mock
    private DynamicFormMetaService dynamicFormMetaServiceMock;
    @Mock
    private MetaService metaService;
    @Mock
    private DynamicFormService dynamicFormService;
    @Mock
    private MetaInstanceService metaInstanceService;
    @Mock
    private FlowInstanceService flowInstanceService;
    @Mock
    private UploadedFileManageService uploadedFileManageService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpClassicClientFactory httpClientFactory;
    @Mock
    private AippLogService aippLogService;
    @Mock
    private BrokerClient client;
    @Mock
    private AppBuilderFormRepository formRepository;
    @Mock
    private AppBuilderFormPropertyRepository formPropertyRepository;
    @Mock
    private FlowsService flowsService;
    @Mock
    private AippStreamService aippStreamService;
    @Mock
    private AopAippLogService aopAippLogService;
    @Mock
    private AppChatSseService appChatSSEService;
    @Mock
    private AippLogService logService;
    @Mock
    private AppBuilderAppFactory appFactory;

    @Test
    @DisplayName("测试startFlowWithUserSelectMemory方法")
    void testStartFlowWithUserSelectMemory() {
        Map<String, Object> businessData = MapBuilder.<String, Object>get().build();
        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, businessData).build();
        Meta meta = new Meta();
        meta.setAttributes(MapBuilder.<String, Object>get().put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_id").build());
        Mockito.when(metaService.retrieve(Mockito.eq("versionId"), Mockito.any())).thenReturn(meta);
        Mockito.when(metaInstanceService.getMetaVersionId(Mockito.eq("instanceId"))).thenReturn("versionId");

        Assertions.assertDoesNotThrow(() -> this.aippRunTimeService.startFlowWithUserSelectMemory("instanceId",
                initContext, new OperationContext()));
    }

    @Test
    @DisplayName("测试createAippInstance方法")
    void testCreateAippInstance() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        context.setOperator("UT 123456");
        this.mockMetaQuery("UserSelect");
        Map<String, Object> initContext = this.genInitContext();
        Assertions.assertDoesNotThrow(() -> this.aippRunTimeService.createAippInstance("aipp_id",
                "version", initContext, context));
    }

    @Test
    @DisplayName("测试createInstanceByApp方法")
    void testCreateInstanceByApp() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        context.setOperator("UT 123456");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, "app1");
        Meta meta = new Meta();
        meta.setAttributes(attributes);
        meta.setVersionId("version1");
        RangedResultSet<Meta> metaRangedResultSet = new RangedResultSet<>();
        metaRangedResultSet.setResults(Collections.singletonList(meta));
        metaRangedResultSet.setRange(new RangeResult(0, 1, 1));

        Instance metaInst = new Instance();
        metaInst.setId("instId");
        when(this.metaInstanceService.createMetaInstance(any(), any(), any())).thenReturn(metaInst);

        when(this.metaService.list(any(MetaFilter.class),
                anyBoolean(),
                anyLong(),
                anyInt(),
                any(OperationContext.class),
                any(MetaFilter.class))).thenReturn(metaRangedResultSet);

        Map<String, Object> initContext = this.genInitContext();
        Assertions.assertDoesNotThrow(() -> this.aippRunTimeService.createInstanceByApp("aipp_id",
                "question", initContext, context, false));
    }

    private void mockMetaQuery(String type) {
        Meta meta = new Meta();
        meta.setId("meta_id");
        meta.setVersionId("meta_version_id");
        meta.setVersion("version");
        meta.setAttributes(MapBuilder.<String, Object>get().put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_definition_id")
                .build());
        FlowNodeInfo flowNodeInfo = new FlowNodeInfo();
        flowNodeInfo.setType("start");
        flowNodeInfo.setProperties(MapBuilder.<String, Object>get()
                .put("inputParams", Collections.singletonList(MapBuilder.<String, Object>get()
                        .put("name", AippConst.MEMORY_CONFIG_KEY)
                        .put("value", Arrays.asList(MapBuilder.<String, Object>get()
                                .put("name", AippConst.MEMORY_SWITCH_KEY)
                                .put("value", true).build(), MapBuilder.<String, Object>get()
                                .put("name", "type")
                                .put("value", type).build())).build()))
                .build());
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowNodes(Collections.singletonList(flowNodeInfo));
        Mockito.when(flowsService.getFlows(Mockito.eq("flow_definition_id"), Mockito.any())).thenReturn(flowInfo);
        Mockito.when(metaService.list(Mockito.any(), Mockito.eq(true),
                Mockito.eq(0L), Mockito.eq(1), Mockito.any(),
                Mockito.any())).thenReturn(RangedResultSet.create(Collections.singletonList(meta), 0, 1, 1));
        Instance metaInst = new Instance();
        metaInst.setId("instId");
        Mockito.when(this.metaInstanceService.createMetaInstance(Mockito.eq("meta_version_id"),
                Mockito.any(), Mockito.any())).thenReturn(metaInst);
    }

    private Map<String, Object> genInitContext() {
        Map<String, Object> businessData = MapBuilder.<String, Object>get()
                .put(AippConst.BS_AIPP_QUESTION_KEY, "你好")
                .build();
        return MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, businessData).build();
    }
}
