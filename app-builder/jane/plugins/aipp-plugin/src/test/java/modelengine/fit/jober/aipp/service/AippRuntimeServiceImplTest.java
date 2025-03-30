/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jade.waterflow.entity.FlowNodeInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.impl.AippRunTimeServiceImpl;
import modelengine.fit.jober.aipp.util.AppUtils;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.emitter.DefaultEmitter;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.globalization.LocaleService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 为{@link modelengine.fit.jober.aipp.service.impl.AippRunTimeServiceImpl} 提供测试
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
    private MetaService metaService;

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

    @Mock
    private LocaleService localeService;

    private MockedStatic<UserContextHolder> opContextHolderMock;

    @Mock
    private AppChatSessionService appChatSessionService;

    @Mock
    private Emitter<Object> emitter;

    @BeforeEach
    void setUp() {
        this.opContextHolderMock = mockStatic(UserContextHolder.class);
        opContextHolderMock.when(UserContextHolder::get).thenReturn(new UserContext("Jane", "127.0.0.1", "en"));
    }

    @AfterEach
    void teardown() {
        this.opContextHolderMock.close();
    }

    @Test
    @DisplayName("测试startFlowWithUserSelectMemory方法")
    void testStartFlowWithUserSelectMemory() {
        Map<String, Object> businessData = MapBuilder.<String, Object>get().build();
        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, businessData).build();
        Meta meta = new Meta();
        meta.setAttributes(MapBuilder.<String, Object>get()
                .put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_id")
                .put(AippConst.ATTR_META_STATUS_KEY, "active")
                .put(AippConst.ATTR_APP_ID_KEY, "123")
                .build());
        Mockito.when(metaService.retrieve(Mockito.eq("versionId"), Mockito.any())).thenReturn(meta);
        Mockito.when(metaInstanceService.getMetaVersionId(Mockito.eq("instanceId"))).thenReturn("versionId");

        Assertions.assertDoesNotThrow(() -> this.aippRunTimeService.startFlowWithUserSelectMemory("instanceId",
                initContext,
                new OperationContext(),
                true));
    }

    @Test
    @DisplayName("测试createAippInstance方法")
    @Disabled
    void testCreateAippInstance() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        context.setOperator("UT 123456");
        this.mockMetaQuery("UserSelect");
        Map<String, Object> initContext = this.genInitContext();
        Assertions.assertDoesNotThrow(() -> this.aippRunTimeService.createAippInstance("aipp_id",
                "version",
                initContext,
                context));
    }

    @Test
    @DisplayName("测试createLatestAippInstanceByAppId方法")
    void TestCreateLatestAippInstanceByAppId() {
        OperationContext context = new OperationContext();
        context.setTenantId("testTenantId");
        context.setOperator("UT 123456");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, "app1");
        attributes.put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_definition_id1");
        Meta meta = new Meta();
        meta.setAttributes(attributes);
        meta.setVersion("version1");
        meta.setVersionId("versionId1");
        meta.setId("id1");

        Instance metaInst = new Instance();
        metaInst.setId("instId");
        metaInst.setInfo(MapBuilder.<String, String>get().put("flow_trans_id", "flowTransId1").build());

        RangedResultSet<Meta> metaRangedResultSet = new RangedResultSet<>();
        metaRangedResultSet.setResults(Collections.singletonList(meta));
        metaRangedResultSet.setRange(new RangeResult(0, 1, 1));

        RangedResultSet<Instance> metaInstanceResultSet = new RangedResultSet<>();
        metaInstanceResultSet.setResults(Collections.singletonList(metaInst));

        Map<String, Object> businessData = MapBuilder.<String, Object>get().build();
        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, businessData).build();

        when(this.metaInstanceService.createMetaInstance(any(), any(), any())).thenReturn(metaInst);
        when(this.metaService.list(any(MetaFilter.class),
                anyBoolean(),
                anyLong(),
                anyInt(),
                any(OperationContext.class))).thenReturn(metaRangedResultSet);
        when(flowsService.getFlows(any(), Mockito.any())).thenReturn(mockFlowInfo("UserSelect"));
        when(this.metaInstanceService.list(anyList(), anyLong(), anyInt(), any())).thenReturn(
                metaInstanceResultSet);

        assertThat(this.aippRunTimeService.createLatestAippInstanceByAppId("app_id",
                true,
                initContext,
                context)).isEqualTo("flowTransId1");
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
        meta.setId("aipp_id");
        meta.setAttributes(attributes);
        meta.setVersionId("version1");
        RangedResultSet<Meta> metaRangedResultSet = new RangedResultSet<>();
        metaRangedResultSet.setResults(Collections.singletonList(meta));
        metaRangedResultSet.setRange(new RangeResult(0, 1, 1));

        AppUtils.setAppChatInfo("123", false);

        Instance metaInst = new Instance();
        metaInst.setId("instId");
        when(this.metaInstanceService.createMetaInstance(any(), any(), any())).thenReturn(metaInst);

        when(this.metaService.list(any(MetaFilter.class),
                anyBoolean(),
                anyLong(),
                anyInt(),
                any(OperationContext.class))).thenReturn(metaRangedResultSet);

        Map<String, Object> initContext = this.genInitContext();
        Assertions.assertDoesNotThrow(() -> this.aippRunTimeService.createInstanceByApp("aipp_id",
                "question",
                initContext,
                context,
                false));
    }

    @Test
    @DisplayName("测试startChat方法")
    @Disabled
    void testStartChatSuccess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获取要测试的私有方法
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("startChat",
                Map.class,
                Meta.class,
                OperationContext.class,
                Instance.class,
                ChatSession.class);
        method.setAccessible(true);  // 设置访问权限为 true
        Map<String, Object> businessData = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        Meta meta = new Meta();
        meta.setId("meta_id");
        meta.setAttributes(attributes);
        OperationContext context = new OperationContext();
        Instance metaInst = new Instance();
        metaInst.setId("instance_id");
        ChatSession<Object> chatSession = new ChatSession<>(new DefaultEmitter<>(), "123", true, Locale.ENGLISH);
        Mockito.when(flowsService.getFlows(any(), Mockito.any())).thenReturn(mockFlowInfo("UserSelect"));
        // 调用私有方法
        Assertions.assertDoesNotThrow(() -> method.invoke(aippRunTimeService,
                businessData,
                meta,
                context,
                metaInst,
                chatSession));
    }

    @Test
    @DisplayName("测试recordContext方法")
    void testRecordContextSuccess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获取要测试的私有方法
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("recordContext",
                OperationContext.class,
                Meta.class,
                Map.class,
                Instance.class);
        method.setAccessible(true);  // 设置访问权限为 true
        OperationContext operationContext = new OperationContext();
        operationContext.setOperator("user1");
        Meta meta = new Meta();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_APP_ID_KEY, "app1");
        meta.setAttributes(attributes);
        Instance metaInst = new Instance();
        metaInst.setId("instance_id");
        Map<String, Object> businessData = new HashMap<>();
        List<Map<String, String>> files = new ArrayList<>();
        files.add(MapBuilder.<String, String>get()
                .put("file_name", "1.docx")
                .put("file_url", "/path/to/1.docx")
                .put("file_type", "docx")
                .build());
        businessData.put(AippConst.BS_AIPP_FILE_DESC_KEY, files);
        method.invoke(aippRunTimeService, operationContext, meta, businessData, metaInst);
        assertThat(businessData.get(AippConst.BS_AIPP_FILES_DOWNLOAD_KEY)).isEqualTo(Collections.singletonList(
                "/path/to/1.docx"));
    }

    @Test
    @DisplayName("测试persistAippLog方法")
    void testPersistAippLogSuccess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获取要测试的私有方法
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("persistAippLog",
                Map.class,
                String.class,
                OperationContext.class);
        method.setAccessible(true);  // 设置访问权限为 true
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_AIPP_QUESTION_KEY, "question");
        List<Map<String, String>> files = new ArrayList<>();
        files.add(MapBuilder.<String, String>get()
                .put("file_name", "1.docx")
                .put("file_url", "/path/to/1.docx")
                .put("file_type", "docx")
                .build());
        businessData.put(AippConst.BS_AIPP_FILE_DESC_KEY, files);
        method.invoke(aippRunTimeService, businessData, "flow_definition_id1", null);
        assertThat(businessData.get(AippConst.BS_AIPP_QUESTION_KEY)).isEqualTo("question");
    }

    @Test
    @DisplayName("测试startChat方法")
    @Disabled
    void testStartChatFail() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获取要测试的私有方法
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("startChat",
                Map.class,
                Meta.class,
                OperationContext.class,
                Instance.class,
                String.class,
                ChatSession.class);
        method.setAccessible(true);  // 设置访问权限为 true
        String word = "aipp.service.impl.AippRunTimeServiceImpl";
        when(localeService.localize(anyString())).thenReturn("test");
        FlowInfo flowInfo = new FlowInfo();
        when(this.flowsService.getFlows(anyString(), any())).thenReturn(flowInfo);
        method.invoke(aippRunTimeService, null, mock(Meta.class), null, mock(Instance.class), null);
        verify(localeService).localize(Mockito.eq(word));
    }

    @Test
    @DisplayName("测试getMemorySwitch方法：当不存在memoryConfig配置项时，返回false")
    void testGetMemorySwitchWithNotExistMemoryConfig()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("getMemorySwitch", List.class, Map.class);
        method.setAccessible(true);
        assertThat(method.invoke(this.aippRunTimeService, null, null)).isEqualTo(false);
    }

    @Test
    @DisplayName("测试getMemorySwitch方法：当不存在memorySwitch配置项时，返回为false")
    void testGetMemorySwitchWithNotExistMemorySwitch()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("getMemorySwitch", List.class, Map.class);
        method.setAccessible(true);
        List<Map<String, Object>> configs = new ArrayList<>();
        Map<String, Object> config1 = new HashMap<>();
        config1.put("name1", "configValue1");
        configs.add(config1);
        assertThat(method.invoke(this.aippRunTimeService, configs, null)).isEqualTo(false);
    }

    @Test
    @DisplayName("测试getMemoryConfig方法，当流程getFlows抛异常时，会抛出Aipp异常")
    void testGetMemoryConfigWithGetFlowsError() throws NoSuchMethodException, IllegalAccessException {
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("getMemoryConfigs",
                Meta.class,
                String.class,
                OperationContext.class);
        method.setAccessible(true);
        Mockito.when(flowsService.getFlows(Mockito.anyString(), Mockito.any())).thenThrow(JobberException.class);
        try {
            method.invoke(this.aippRunTimeService, mock(Meta.class), "hello", new OperationContext());
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof AippException);
            assertThat((e.getCause()).getMessage()).isEqualTo("系统错误，获取应用编排信息失败，请联系管理员。");
        }
    }

    @Test
    @DisplayName("测试getMemoryConfig方法")
    void testGetMemoryConfigWithGetFlowsOK()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("getMemoryConfigs",
                Meta.class,
                String.class,
                OperationContext.class);
        method.setAccessible(true);
        FlowInfo flowInfo = this.mockFlowInfo("UserSelect");
        Mockito.when(flowsService.getFlows(Mockito.anyString(), Mockito.any())).thenReturn(flowInfo);
        Assertions.assertDoesNotThrow(() -> method.invoke(this.aippRunTimeService,
                mock(Meta.class),
                "hello",
                new OperationContext()));
    }

    @Test
    @DisplayName("测试startFlow方法，当流程startFlow抛异常时，会抛出Aipp异常")
    void testStartFlowsWithStartFlowError() throws NoSuchMethodException, IllegalAccessException {
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("startFlow",
                String.class,
                String.class,
                String.class,
                Map.class,
                OperationContext.class);
        method.setAccessible(true);
        Mockito.when(flowInstanceService.startFlow(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenThrow(JobberException.class);
        try {
            method.invoke(this.aippRunTimeService, "hello", "hello", "hello", new HashMap<>(), new OperationContext());
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof AippException);
            assertThat((e.getCause()).getMessage()).isEqualTo("会话响应出错，请重试。");
        }
    }

    @Test
    @DisplayName("测试sendReadyStatus方法")
    void testSendReadyStatus() throws NoSuchMethodException {
        Method method = AippRunTimeServiceImpl.class.getDeclaredMethod("sendReadyStatus", String.class, Map.class);
        method.setAccessible(true);
        String metaInstId = "123";
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_AT_CHAT_ID, "at_chat");
        businessData.put(AippConst.BS_CHAT_ID, "chat");
        AppChatRsp appChatRsp = AppChatRsp.builder()
                .instanceId(metaInstId)
                .status(FlowTraceStatus.READY.name())
                .atChatId(ObjectUtils.cast(businessData.get(AippConst.BS_AT_CHAT_ID)))
                .chatId(ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID)))
                .build();
        doNothing().when(this.appChatSSEService).send(eq(metaInstId), eq(appChatRsp));

        Assertions.assertDoesNotThrow(() -> method.invoke(this.aippRunTimeService, metaInstId, businessData));
    }

    private void mockMetaQuery(String type) {
        Meta meta = new Meta();
        meta.setId("meta_id");
        meta.setVersionId("meta_version_id");
        meta.setVersion("version");
        meta.setAttributes(MapBuilder.<String, Object>get()
                .put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_definition_id")
                .build());
        FlowInfo flowInfo = mockFlowInfo(type);
        Mockito.when(flowsService.getFlows(Mockito.eq("flow_definition_id"), Mockito.any())).thenReturn(flowInfo);
        Mockito.when(metaService.list(Mockito.any(), Mockito.eq(true), Mockito.eq(0L), Mockito.eq(1), Mockito.any()))
                .thenReturn(RangedResultSet.create(Collections.singletonList(meta), 0, 1, 1));
        Instance metaInst = new Instance();
        metaInst.setId("instId");
        Mockito.when(this.metaInstanceService.createMetaInstance(Mockito.eq("meta_version_id"),
                Mockito.any(),
                Mockito.any())).thenReturn(metaInst);
    }

    private FlowInfo mockFlowInfo(String type) {
        FlowNodeInfo flowNodeInfo = new FlowNodeInfo();
        flowNodeInfo.setType("start");
        flowNodeInfo.setProperties(MapBuilder.<String, Object>get()
                .put("inputParams", Arrays.asList(this.buildInput(), this.buildMemory(type)))
                .build());
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setFlowNodes(Collections.singletonList(flowNodeInfo));
        return flowInfo;
    }

    private Object buildInput() {
        return MapBuilder.<String, Object>get()
                .put("name", AippConst.BUSINESS_INPUT_KEY)
                .put("value",
                        Collections.singletonList(MapBuilder.<String, Object>get()
                                .put("name", AippConst.BS_AIPP_QUESTION_KEY)
                                .put("value", "question_1")
                                .build()))
                .build();
    }

    private Map<String, Object> buildMemory(String type) {
        return MapBuilder.<String, Object>get()
                .put("name", AippConst.MEMORY_CONFIG_KEY)
                .put("value",
                        Arrays.asList(MapBuilder.<String, Object>get()
                                        .put("name", AippConst.MEMORY_SWITCH_KEY)
                                        .put("value", true)
                                        .build(),
                                MapBuilder.<String, Object>get().put("name", "type").put("value", type).build()))
                .build();
    }

    private Map<String, Object> genInitContext() {
        Map<String, Object> businessData =
                MapBuilder.<String, Object>get().put(AippConst.BS_AIPP_QUESTION_KEY, "你好").build();
        return MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, businessData).build();
    }
}
