/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.huawei.fit.dynamicform.DynamicFormMetaService;
import com.huawei.fit.dynamicform.entity.FormMetaInfo;
import com.huawei.fit.dynamicform.entity.FormMetaItem;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jober.FlowsService;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippForbiddenException;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.condition.AippQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.convertor.TaskPropertyConvertor;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AippDetailDto;
import com.huawei.fit.jober.aipp.dto.AippDto;
import com.huawei.fit.jober.aipp.dto.AippOverviewRspDto;
import com.huawei.fit.jober.aipp.enums.AippMetaStatusEnum;
import com.huawei.fit.jober.aipp.enums.AppCategory;
import com.huawei.fit.jober.aipp.enums.JaneCategory;
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.impl.AippFlowServiceImpl;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.jober.entity.FlowNodeFormInfo;
import com.huawei.fit.jober.entity.FlowNodeInfo;
import modelengine.fit.waterflow.domain.enums.FlowNodeType;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class AippFlowServiceTest {
    private static final String DUMMY_FLOW_CONFIG_ID = "testFlowConfigId";
    private static final String DUMMY_FLOW_CONFIG_VERSION = "1.0.0";
    private static final String DUMMY_META_VERSION_NEW = "1.0.1";
    private static final String DUMMY_META_VERSION_OLD = "1.0.0";

    @InjectMocks
    private AippFlowServiceImpl aippFlowServiceImpl;

    @Mock
    private MetaService metaServiceMock;

    @Mock
    private FlowsService flowsServiceMock;

    @Mock
    private DynamicFormMetaService formMetaServiceMock;

    @Mock
    private AppBuilderFormRepository appBuilderFormRepositoryMock;

    @Mock
    private BrokerClient brokerClientMock;

    @Mock
    private Router routerMock;

    @Mock
    private Invoker invokerMock;

    @Mock
    private AppBuilderAppMapper appBuilderAppMapperMock;

    @BeforeEach
    void setUp() {
        // setup
    }

    Meta GenTestMeta() {
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime modifyTime = LocalDateTime.now();
        Meta expectMeta = new Meta();
        expectMeta.setName("testName");
        expectMeta.setId("testId");
        expectMeta.setCreator("testUser");
        expectMeta.setCreationTime(createTime);
        expectMeta.setLastModificationTime(modifyTime);
        expectMeta.setVersion(DUMMY_META_VERSION_OLD);

        Map<String, Object> attribute = new HashMap<>();
        attribute.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, DUMMY_FLOW_CONFIG_ID);
        attribute.put(AippConst.ATTR_VERSION_KEY, DUMMY_FLOW_CONFIG_VERSION);
        attribute.put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        expectMeta.setAttributes(attribute);
        return expectMeta;
    }

    Meta GenerateInactiveMeta() {
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime modifyTime = LocalDateTime.now();
        Meta expectMeta = new Meta();
        expectMeta.setName("testName");
        expectMeta.setId("testId");
        expectMeta.setVersion(DUMMY_META_VERSION_NEW);
        expectMeta.setCreator("testUser");
        expectMeta.setCreationTime(createTime);
        expectMeta.setLastModificationTime(modifyTime);

        Map<String, Object> attribute = new HashMap<>();
        attribute.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, DUMMY_FLOW_CONFIG_ID);
        attribute.put(AippConst.ATTR_BASELINE_VERSION_KEY, DUMMY_META_VERSION_OLD);
        attribute.put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        expectMeta.setAttributes(attribute);
        return expectMeta;
    }

    OperationContext GenTestOperationContext() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        context.setOperator("testOperator");
        return context;
    }

    @Test
    @Disabled
    void testQueryAippDetailThenOk() {
        final String defaultVersion = "1.0.0";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, DUMMY_FLOW_CONFIG_ID);
        attributes.put(AippConst.ATTR_VERSION_KEY, defaultVersion);
        attributes.put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());

        Meta meta = new Meta();
        meta.setId("testAippId");
        meta.setName("testMeta");
        meta.setCreator("testUser");
        meta.setAttributes(attributes);

        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowId((String) attributes.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY));
        flowInfo.setVersion((String) attributes.get(AippConst.ATTR_VERSION_KEY));
        flowInfo.setConfigData("{\"id\": \"testFlowConfigId\"}");
        flowInfo.setFlowDefinitionId("testFlowDefinitionId");

        RangedResultSet<Meta> mockResult = RangedResultSet.create(Collections.singletonList(meta), 0L, 1, 1);
        when(metaServiceMock.list(argThat(metaFilter -> metaFilter.getCategories().size() == 1
                        && metaFilter.getCategories().get(0).equals(JaneCategory.AIPP.name())
                        && metaFilter.getMetaIds().size() == 1 && metaFilter.getMetaIds().get(0).equals("testAippId")
                        && metaFilter.getVersions().size() == 1 && metaFilter.getVersions().get(0).equals(defaultVersion)),
                eq(true),
                eq(0L),
                eq(1),
                any(OperationContext.class))).thenReturn(mockResult);
        when(flowsServiceMock.getFlows(eq((String) attributes.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY)),
                eq((String) attributes.get(AippConst.ATTR_VERSION_KEY)),
                any(OperationContext.class))).thenReturn(flowInfo);

        Rsp<AippDetailDto> rsp =
                aippFlowServiceImpl.queryAippDetail(meta.getId(), defaultVersion, GenTestOperationContext());

        Assertions.assertEquals(0, rsp.getCode());
        Assertions.assertTrue(rsp.getData().getFlowViewData().containsKey("id"));
        Assertions.assertEquals(DUMMY_FLOW_CONFIG_ID, rsp.getData().getFlowViewData().get("id"));
    }

    @Test
    @Disabled
    void shouldSetDraftVersionWhenCallListAippWithInactiveAipp() {
        Meta expectMeta = GenerateInactiveMeta();

        when(metaServiceMock.list(any(MetaFilter.class),
                eq(true),
                eq(0L),
                eq(10),
                any(OperationContext.class))).thenReturn(RangedResultSet.create(Collections.singletonList(expectMeta),
                0L,
                10,
                1L));
        PageResponse<AippOverviewRspDto> rsp =
                aippFlowServiceImpl.listAipp(AippQueryCondition.builder().name("testName").build(),
                        PaginationCondition.builder().pageNum(1).pageSize(10).build(),
                        GenTestOperationContext());

        Assertions.assertEquals(1, rsp.getTotal());
        List<AippOverviewRspDto> data = rsp.getItems();
        Assertions.assertEquals(1, data.size());
        Assertions.assertEquals(expectMeta.getName(), data.get(0).getName());
        Assertions.assertEquals(expectMeta.getId(), data.get(0).getAippId());
        Assertions.assertEquals(expectMeta.getCreator(), data.get(0).getCreator());
        Assertions.assertEquals(expectMeta.getCreationTime(), data.get(0).getCreatedAt());
        Assertions.assertEquals(expectMeta.getLastModificationTime(), data.get(0).getUpdatedAt());
        Assertions.assertNull(data.get(0).getPublishAt());
        Assertions.assertEquals(expectMeta.getVersion(), data.get(0).getDraftVersion());
        Assertions.assertEquals(expectMeta.getAttributes().get(AippConst.ATTR_BASELINE_VERSION_KEY),
                data.get(0).getVersion());
        Assertions.assertEquals(expectMeta.getAttributes().get(AippConst.ATTR_META_STATUS_KEY),
                data.get(0).getStatus());
    }

    @Test
    @Disabled
    void testCreateAippWithInvalidNameThenFail() {
        Assertions.assertThrows(AippParamException.class,
                () -> aippFlowServiceImpl.create(AippDto.builder().build(), GenTestOperationContext()));

        Assertions.assertThrows(AippParamException.class,
                () -> aippFlowServiceImpl.create(AippDto.builder().name("").build(), GenTestOperationContext()));

        Assertions.assertThrows(AippParamException.class,
                () -> aippFlowServiceImpl.create(AippDto.builder().name("  ").build(), GenTestOperationContext()));
    }

    @Test
    @Disabled
    void testCreateAippThenOk() {
        AippDto aipp = AippDto.builder().name("testAippName").build();

        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowId(DUMMY_FLOW_CONFIG_ID);
        flowInfo.setVersion(DUMMY_FLOW_CONFIG_VERSION);

        when(flowsServiceMock.createFlows(any(String.class), any(OperationContext.class))).thenReturn(flowInfo);
        when(metaServiceMock.create(any(MetaDeclarationInfo.class), any(OperationContext.class))).thenAnswer(var -> {
            MetaDeclarationInfo info = var.getArgument(0);
            Assertions.assertEquals(info.getName().getValue(), aipp.getName());
            Assertions.assertEquals(info.getAttributes().getValue().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY),
                    flowInfo.getFlowId());
            Assertions.assertEquals(info.getVersion().getValue(), flowInfo.getVersion());
            Assertions.assertEquals(info.getAttributes().getValue().get(AippConst.ATTR_META_STATUS_KEY),
                    AippMetaStatusEnum.INACTIVE.getCode());
            List<MetaPropertyDeclarationInfo> props = info.getProperties().getValue();
            for (int i = 0; i < props.size(); i++) {
                Assertions.assertEquals(props.get(i).getName().getValue(), AippConst.STATIC_META_ITEMS.get(i).getKey());
            }

            Meta meta = new Meta();
            meta.setName(info.getName().getValue());
            meta.setId("testMetaId");
            meta.setAttributes(info.getAttributes().getValue());
            meta.setProperties(info.getProperties()
                    .getValue()
                    .stream()
                    .map(TaskPropertyConvertor.INSTANCE::fromMetaPropertyDeclarationInfo)
                    .collect(Collectors.toList()));
            return meta;
        });

        AippCreateDto rsp = aippFlowServiceImpl.create(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getAippId(), "testMetaId");
    }

    @Test
    @Disabled
    void testUpdateAippWithInvalidConditionThenFail() {
        Meta expectMeta = GenTestMeta();
        expectMeta.getAttributes().replace(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.ACTIVE.getCode());
        String aippId = expectMeta.getId();

        // test update active aipp
        RangedResultSet<Meta> mockResult = RangedResultSet.create(Collections.singletonList(expectMeta), 0L, 1, 1);
        when(metaServiceMock.list(argThat(metaFilter -> metaFilter.getCategories().size() == 1
                && metaFilter.getCategories().get(0).equals(JaneCategory.AIPP.name())
                && metaFilter.getMetaIds().size() == 1 && metaFilter.getMetaIds().get(0).equals("testId")
                && metaFilter.getVersions().size() == 1 && metaFilter.getVersions()
                .get(0)
                .equals(DUMMY_META_VERSION_OLD)), eq(true), eq(0L), eq(1), any(OperationContext.class))).thenReturn(
                mockResult);

        Assertions.assertThrows(AippForbiddenException.class, () -> {
            AippDto aippDto = new AippDto();
            aippDto.setId(aippId);
            aippDto.setVersion(DUMMY_META_VERSION_OLD);
            aippFlowServiceImpl.update(aippDto, GenTestOperationContext());
        });

        // test blank name
        expectMeta.getAttributes().replace(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        AippDto aipp = AippDto.builder().name(" ").build();
        Assertions.assertThrows(AippParamException.class, () -> {
            AippDto aippDto = new AippDto();
            aippDto.setId(aippId);
            aippDto.setVersion(DUMMY_META_VERSION_OLD);
            aippFlowServiceImpl.update(aippDto, GenTestOperationContext());
        });

        aipp.setName("");
        Assertions.assertThrows(AippParamException.class, () -> {
            AippDto aippDto = new AippDto();
            aippDto.setId(aippId);
            aippDto.setVersion(DUMMY_META_VERSION_OLD);
            aippFlowServiceImpl.update(aipp, GenTestOperationContext());
        });

        Assertions.assertThrows(AippParamException.class, () -> {
            AippDto aippDto = new AippDto();
            aippDto.setId(aippId);
            aippDto.setVersion(DUMMY_META_VERSION_OLD);
            aippFlowServiceImpl.update(new AippDto(), GenTestOperationContext());
        });
    }

    @Test
    @Disabled
    void testUpdateAippThenOk() {
        Meta expectMeta = GenTestMeta();
        String aippId = expectMeta.getId();
        AippDto aipp = AippDto.builder().name(expectMeta.getName()).description("testDescription").build();

        RangedResultSet<Meta> mockResult = RangedResultSet.create(Collections.singletonList(expectMeta), 0L, 1, 1);
        when(metaServiceMock.list(argThat(metaFilter -> metaFilter.getCategories().size() == 1
                        && metaFilter.getCategories().get(0).equals(JaneCategory.AIPP.name())
                        && metaFilter.getMetaIds().size() == 1 && metaFilter.getMetaIds().get(0).equals("testId")
                        && metaFilter.getVersions().isEmpty()),
                eq(true),
                eq(0L),
                eq(1),
                any(OperationContext.class))).thenReturn(mockResult);
        doAnswer(var -> {
            MetaDeclarationInfo info = var.getArgument(1);
            Assertions.assertEquals(expectMeta.getName(), info.getName().getValue());
            Assertions.assertEquals(aipp.getDescription(),
                    info.getAttributes().getValue().get(AippConst.ATTR_DESCRIPTION_KEY));
            return null;
        }).when(metaServiceMock).patch(any(), any(), any());

        aipp.setId(aippId);
        AippCreateDto rsp = aippFlowServiceImpl.update(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getAippId(), aippId);

        // flowData not null or empty
        Map<String, Object> flowData = new HashMap<>();
        flowData.put("testFlowKey", "testFlowData");
        aipp.setFlowViewData(flowData);
        doReturn(new FlowInfo()).when(flowsServiceMock)
                .updateFlows(eq((String) expectMeta.getAttributes().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY)),
                        eq((String) expectMeta.getAttributes().get(AippConst.ATTR_VERSION_KEY)),
                        any(),
                        any(OperationContext.class));
        rsp = this.aippFlowServiceImpl.update(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getAippId(), aippId);
    }

    FlowNodeFormInfo buildFlowNodeFormInfo() {
        FlowNodeFormInfo expectedFormInfo = new FlowNodeFormInfo();
        expectedFormInfo.setFormId("testFormId");
        expectedFormInfo.setVersion("testFormVersion");

        return expectedFormInfo;
    }

    private void publishFlowsMock(FlowNodeFormInfo expectedFormInfo) {
        doAnswer(var -> {
            String flowDataStr = var.getArgument(2);
            FlowInfo flowInfo = new FlowInfo();
            flowInfo.setConfigData(flowDataStr);
            flowInfo.setFlowId(var.getArgument(0));
            flowInfo.setVersion(var.getArgument(1));

            FlowNodeInfo nodeInfo = new FlowNodeInfo();
            nodeInfo.setType(FlowNodeType.START.getCode());
            nodeInfo.setFlowNodeForm(expectedFormInfo);
            nodeInfo.setProperties(this.buildFlowNodesProperties());
            flowInfo.setFlowNodes(Collections.singletonList(nodeInfo));
            return flowInfo;
        }).when(flowsServiceMock).publishFlows(any(), any(), any(), any(OperationContext.class));
    }

    private Map<String, Object> buildFlowNodesProperties() {
        return MapBuilder.<String, Object>get()
                .put("inputParams", Collections.singletonList(this.buildInputParams()))
                .build();
    }

    private Map<String, Object> buildInputParams() {
        Map<String, Object> inputValue = new HashMap<>();
        return MapBuilder.<String, Object>get()
                .put("name", "input")
                .put("value", Collections.singletonList(inputValue))
                .build();
    }

    private void publishBasicMock(Meta expectMeta) {
        RangedResultSet<Meta> mockResult = RangedResultSet.create(Collections.singletonList(expectMeta), 0L, 1, 1);
        when(metaServiceMock.list(argThat(metaFilter -> metaFilter.getCategories().size() == 1
                        && metaFilter.getCategories().get(0).equals(JaneCategory.AIPP.name())
                        && metaFilter.getMetaIds().size() == 1 && metaFilter.getMetaIds().get(0).equals("testId")
                        && metaFilter.getVersions().isEmpty()),
                eq(true),
                eq(0L),
                eq(1),
                any(OperationContext.class),
                any(MetaFilter.class))).thenReturn(mockResult);
        FlowNodeFormInfo expectedFormInfo = buildFlowNodeFormInfo();
        publishFlowsMock(expectedFormInfo);
    }

    void formMetaServiceQueryMock(List<FormMetaItem> formMetaItems) {
        doAnswer(var -> {
            List<FormMetaQueryParameter> parameters = var.getArgument(0);
            Assertions.assertEquals(parameters.size(), 1);
            FormMetaInfo metaInfo = new FormMetaInfo(parameters.get(0).getFormId(), parameters.get(0).getVersion());
            metaInfo.setFormMetaItems(formMetaItems);
            return Collections.singletonList(metaInfo);
        }).when(formMetaServiceMock).query(any());
    }

    AippDto GenAippDtoWithData(String name) {
        Map<String, Object> flowData = new HashMap<>();
        flowData.put("testFlowKey", "testFlowData");
        flowData.put("id", DUMMY_FLOW_CONFIG_ID);
        flowData.put("version", DUMMY_FLOW_CONFIG_VERSION);
        return AippDto.builder().name(name).flowViewData(flowData).build();
    }

    private void getFlowsMockRetry() {
        doAnswer(new Answer<Object>() {
            private int times = 0;

            public Object answer(InvocationOnMock invocation) {
                if (++times == 1) {
                    // 触发第一次
                    FlowInfo info = new FlowInfo();
                    info.setFlowId(DUMMY_FLOW_CONFIG_ID);
                    info.setVersion(DUMMY_FLOW_CONFIG_VERSION);
                    return info;
                }
                // 触发第二次
                throw new IllegalStateException();
            }
        }).when(flowsServiceMock).getFlows(anyString(), anyString(), any());
    }

    @Test
    @Disabled
    void shouldOkWhenPreviewAipp() {
        Meta expectMeta = GenTestMeta();
        expectMeta.getAttributes().put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.ACTIVE.getCode());

        getFlowsMockRetry();
        publishFlowsMock(buildFlowNodeFormInfo());

        FormMetaItem expectedFormMetaItem = new FormMetaItem("testKey", "testName", "TEXT", null, null);
        formMetaServiceQueryMock(Collections.singletonList(expectedFormMetaItem));

        when(metaServiceMock.create(any(), any())).thenAnswer(var -> {
            MetaDeclarationInfo info = var.getArgument(0);
            Assertions.assertEquals(info.getName().getValue(), expectMeta.getName());
            Assertions.assertEquals(expectMeta.getAttributes().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY),
                    info.getAttributes().getValue().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY));

            String previewVersion = info.getVersion().getValue();
            String expectedVersion = (String) expectMeta.getAttributes().get(AippConst.ATTR_VERSION_KEY);
            Assertions.assertNotEquals(expectedVersion.length(), previewVersion.length());
            Assertions.assertEquals(expectedVersion, previewVersion.substring(0, expectedVersion.length()));
            Assertions.assertEquals(AippMetaStatusEnum.ACTIVE.getCode(),
                    info.getAttributes().getValue().get(AippConst.ATTR_META_STATUS_KEY));

            List<MetaPropertyDeclarationInfo> props = info.getProperties().getValue();
            List<FormMetaItem> totalFormMetaItem = new ArrayList<>(AippConst.STATIC_META_ITEMS);
            totalFormMetaItem.add(expectedFormMetaItem);
            for (int i = 0; i < props.size(); i++) {
                Assertions.assertEquals(props.get(i).getName().getValue(), totalFormMetaItem.get(i).getKey());
            }
            return expectMeta;
        });

        AippDto aipp = GenAippDtoWithData(expectMeta.getName());
        aipp.setId(expectMeta.getId());
        AippCreateDto rsp =
                aippFlowServiceImpl.previewAipp((String) expectMeta.getAttributes().get(AippConst.ATTR_VERSION_KEY),
                        aipp,
                        GenTestOperationContext());
        Assertions.assertEquals(expectMeta.getId(), rsp.getAippId());
    }

    @Test
    @Disabled
    void shouldFailedWhenCreatePreviewAippFailed() {
        Meta expectMeta = GenTestMeta();
        expectMeta.getAttributes().put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.ACTIVE.getCode());

        FlowInfo info = new FlowInfo();
        info.setFlowId(DUMMY_FLOW_CONFIG_ID);
        info.setVersion(DUMMY_FLOW_CONFIG_VERSION);

        when(this.flowsServiceMock.getFlows(anyString(), anyString(), any())).thenReturn(info);

        AippDto aipp = GenAippDtoWithData(expectMeta.getName());
        aipp.setId(expectMeta.getId());
        Assertions.assertThrows(AippException.class,
                () -> this.aippFlowServiceImpl.previewAipp(ObjectUtils.cast(expectMeta.getAttributes()
                        .get(AippConst.ATTR_VERSION_KEY)), aipp, GenTestOperationContext()));
    }

    @Test
    @DisplayName("发布应用成功")
    void testPublishAppThenOk() {
        Meta expectMeta = GenTestMeta();
        publishBasicMock(expectMeta);
        this.setUpPublishMock();
        AippDto aipp = this.buildAppDto(expectMeta, AppCategory.APP.getType());
        Rsp<AippCreateDto> rsp = this.aippFlowServiceImpl.publish(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getCode(), AippErrCode.OK.getErrorCode());
    }

    @Test
    @DisplayName("发布工具流成功")
    void testPublishWaterFlowThenOk() {
        Meta expectMeta = GenTestMeta();
        publishBasicMock(expectMeta);
        this.setUpPublishMock();
        AippDto aipp = this.buildAppDto(expectMeta, AppCategory.WATER_FLOW.getType());
        Rsp<AippCreateDto> rsp = this.aippFlowServiceImpl.publish(aipp, GenTestOperationContext());
        Assertions.assertEquals(rsp.getCode(), AippErrCode.OK.getErrorCode());
    }

    private void setUpPublishMock() {
        doAnswer((Answer<Object>) invocation -> {
            MetaDeclarationInfo declaration = invocation.getArgument(1);
            Assertions.assertTrue(declaration.getAttributes().getDefined());
            return null;
        }).when(metaServiceMock).patch(any(), any(), any());

        when(this.appBuilderFormRepositoryMock.selectWithId(anyString())).thenReturn(null);
        when(this.brokerClientMock.getRouter(any(), anyString())).thenReturn(this.routerMock);
        when(this.routerMock.route(any())).thenReturn(this.invokerMock);
        when(this.invokerMock.invoke(any())).thenReturn("");
        doNothing().when(this.appBuilderAppMapperMock).updateAppWithStoreId(any(), any(), any());
    }

    private AippDto buildAppDto(Meta expectMeta, String type) {
        AippDto aipp = GenAippDtoWithData(expectMeta.getName());
        aipp.setId(expectMeta.getId());
        aipp.setVersion(DUMMY_META_VERSION_OLD);
        aipp.setType(type);
        aipp.setIcon(StringUtils.EMPTY);
        aipp.setDescription(StringUtils.EMPTY);
        aipp.setUniqueName(StringUtils.EMPTY);
        return aipp;
    }
}
