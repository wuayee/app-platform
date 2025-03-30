/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static modelengine.fit.jober.aipp.enums.AppTypeEnum.APP;

import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jade.waterflow.entity.FlowNodeInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.genericable.AppBuilderAppService;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.impl.AppChatServiceImpl;
import modelengine.fit.jober.aipp.util.CacheUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 为 {@link AppChatService} 提供测试
 *
 * @author 姚江
 * @since 2024-08-01
 */
@ExtendWith(MockitoExtension.class)
public class AppChatServiceImplTest {
    private AppChatService appChatService;

    @Mock
    private AppBuilderAppFactory appFactory;

    @Mock
    private AippChatMapper aippChatMapper;

    @Mock
    private AippRunTimeService aippRunTimeService;

    @Mock
    private AppBuilderAppService appService;

    @Mock
    private AippLogService aippLogService;

    @Mock
    private AppBuilderAppRepository appRepository;

    @Mock
    private MetaService metaService;

    @Mock
    private FlowsService flowsService;

    @BeforeEach
    void before() {
        this.appChatService = new AppChatServiceImpl(this.appFactory,
                this.aippChatMapper,
                this.aippRunTimeService,
                this.appService,
                this.aippLogService,
                this.appRepository,
                this.metaService,
                this.flowsService,
                null,
                null);
        CacheUtils.clear();
    }

    @Test
    @DisplayName("测试对话方法")
    void testChat() {
        String chatAppId = "chat";
        String atChatAppId = "atChat";
        Map<String, Object> context = new HashMap<>();
        context.put("user_1", true);
        context.put("user_2", atChatAppId);
        CreateAppChatRequest hello = CreateAppChatRequest.builder()
                .appId(chatAppId)
                .question("你好")
                .chatId("hello")
                .context(CreateAppChatRequest.Context.builder()
                        .useMemory(true)
                        .atAppId(atChatAppId)
                        .userContext(context)
                        .build())
                .build();
        OperationContext operationContext = new OperationContext();

        Choir<Object> t2 = Choir.create((e) -> {});
        Mockito.when(this.aippRunTimeService.createInstanceByApp(Mockito.eq(atChatAppId),
                Mockito.eq("你好"),
                Mockito.anyMap(),
                Mockito.any(),
                Mockito.eq(false))).thenReturn(Tuple.duet("hello_inst", t2));
        Mockito.when(this.appFactory.create(Mockito.any()))
                .thenReturn(AppBuilderApp.builder().id("id1").version("10.0.0").state("ACTIVE").build());
        Mockito.when(this.appRepository.selectWithId(Mockito.any()))
                .thenReturn(AppBuilderApp.builder().id("id1").version("10.0.0").state("ACTIVE").build());
        Mockito.when(this.metaService.list(Mockito.any(MetaFilter.class),
                        Mockito.eq(false),
                        Mockito.eq(0L),
                        Mockito.eq(10),
                        Mockito.any(OperationContext.class)))
                .thenReturn(new RangedResultSet<>(Collections.singletonList(mockMeta()), new RangeResult(0, 10, 1)));
        Mockito.when(this.metaService.list(Mockito.any(MetaFilter.class),
                        Mockito.eq(true),
                        Mockito.eq(0L),
                        Mockito.eq(1),
                        Mockito.any(OperationContext.class)))
                .thenReturn(new RangedResultSet<>(Collections.singletonList(mockMeta()), new RangeResult(0, 10, 1)));
        Mockito.when(this.flowsService.getFlows(Mockito.any(), Mockito.any()))
                .thenReturn(mockFlowInfo(new ArrayList<>(), false));
        Choir<Object> objectChoir =
                Assertions.assertDoesNotThrow(() -> this.appChatService.chat(hello, operationContext, false));
        Assertions.assertEquals(t2, objectChoir);
    }

    @Test
    @DisplayName("测试重新对话")
    void testRestartChat() {
        Mockito.when(this.aippLogService.getParentPath(Mockito.any())).thenReturn("/instanceId");
        List<String> chatIds = Arrays.asList("chatId1", "chatId2");
        Mockito.when(this.aippChatMapper.selectChatIdByInstanceId(Mockito.eq("instanceId"))).thenReturn(chatIds);
        Mockito.when(this.aippChatMapper.selectChatListByChatIds(chatIds)).thenReturn(this.mockChatList());
        Mockito.when(this.aippLogService.queryLogsByInstanceIdAndLogTypes(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(this.mockLog());
        Mockito.when(this.aippChatMapper.selectChatList(Mockito.any(), Mockito.anyString(), Mockito.any()))
                .thenReturn(this.mockChatList());
        Mockito.when(this.aippRunTimeService.createInstanceByApp(Mockito.any(),
                Mockito.any(),
                Mockito.anyMap(),
                Mockito.any(),
                Mockito.eq(true))).thenReturn(Tuple.duet("hello_inst", Choir.create((e) -> {})));
        Mockito.when(this.appFactory.create(Mockito.any()))
                .thenReturn(AppBuilderApp.builder().id("id1").version("10.0.0").state("ACTIVE").build());
        Mockito.when(this.appRepository.selectWithId(Mockito.any()))
                .thenReturn(AppBuilderApp.builder().id("id1").version("10.0.0").state("ACTIVE").build());
        Mockito.when(this.metaService.list(Mockito.any(MetaFilter.class),
                        Mockito.eq(false),
                        Mockito.eq(0L),
                        Mockito.eq(10),
                        Mockito.any(OperationContext.class)))
                .thenReturn(new RangedResultSet<>(Collections.singletonList(mockMeta()), new RangeResult(0, 10, 1)));
        Mockito.lenient()
                .when(this.flowsService.getFlows(Mockito.any(), Mockito.any()))
                .thenReturn(mockFlowInfo(new ArrayList<>(), false));
        Assertions.assertDoesNotThrow(() -> this.appChatService.restartChat("1",
                new HashMap<>(),
                new OperationContext()));
    }

    @Test
    @DisplayName("测试重新对话：没找到对应的对话")
    void testRestartChatFailedNoChat() {
        Mockito.when(this.aippLogService.getParentPath(Mockito.any())).thenReturn("/instanceId");
        List<String> chatIds = Arrays.asList("chatId1", "chatId2");
        Mockito.when(this.aippChatMapper.selectChatIdByInstanceId(Mockito.eq("instanceId"))).thenReturn(chatIds);
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> this.appChatService.restartChat("1", new HashMap<>(), new OperationContext()));
        Assertions.assertEquals(90002939, exception.getCode());
    }

    @Test
    @DisplayName("应用对话时，没有传入合法Question")
    @Disabled
    void testChatWithInvalidQuestion() {
        String chatAppId = "chat";
        String atChatAppId = "atChat";
        Map<String, Object> context = new HashMap<>();
        context.put("user_1", true);
        context.put("user_2", atChatAppId);
        OperationContext operationContext = new OperationContext();
        Mockito.when(this.appFactory.create(Mockito.any()))
                .thenReturn(AppBuilderApp.builder()
                        .id("id1")
                        .version("10.0.0")
                        .state("ACTIVE")
                        .type(APP.code())
                        .build());
        Mockito.when(this.appRepository.selectWithId(Mockito.any()))
                .thenReturn(AppBuilderApp.builder()
                        .id("id1")
                        .version("10.0.0")
                        .state("ACTIVE")
                        .type(APP.code())
                        .build());

        CreateAppChatRequest hello = CreateAppChatRequest.builder()
                .appId(chatAppId)
                .chatId("hello")
                .context(CreateAppChatRequest.Context.builder()
                        .useMemory(true)
                        .atAppId(atChatAppId)
                        .userContext(context)
                        .build())
                .build();

        // question的长度在1-20000之间，在应用的场景下为必填
        testInvalidQuestion(hello, "", operationContext);
        testInvalidQuestion(hello, null, operationContext);
        String testInput = java.util.stream.Stream.generate(() -> "A").limit(20001).collect(Collectors.joining());
        testInvalidQuestion(hello, testInput, operationContext);
    }

    private void testInvalidQuestion(CreateAppChatRequest hello, String question, OperationContext operationContext) {
        hello.setQuestion(question);
        AippParamException exception = Assertions.assertThrows(AippParamException.class,
                () -> this.appChatService.chat(hello, operationContext, false));
        Assertions.assertEquals(exception.getMessage(), "非法参数: Question。");
    }

    @Nested
    @DisplayName("测试自定义参数校验")
    class TestAddUserContext {
        private AppChatService chatService;

        @Mock
        private MetaService metaService1;

        @Mock
        private FlowsService flowsService1;

        @BeforeEach
        void setup() {
            this.chatService =
                    new AppChatServiceImpl(null, null, null, null, null, null, metaService1, flowsService1, null, null);
            Mockito.when(metaService1.list(Mockito.any(MetaFilter.class),
                    Mockito.anyBoolean(),
                    Mockito.eq(0L),
                    Mockito.anyInt(),
                    Mockito.any(OperationContext.class))).thenAnswer((invocation) -> {
                boolean argument = invocation.getArgument(1);
                if (argument) {
                    return new RangedResultSet<>(Collections.singletonList(mockMeta()), new RangeResult(0, 1, 1));
                } else {
                    return new RangedResultSet<>(Collections.singletonList(mockMeta()), new RangeResult(0, 10, 1));
                }
            });
            CacheUtils.clear();
        }

        @Test
        @DisplayName("自定义参数为非必填，userContext中该参数的值为null，校验不报错")
        public void testNotRequiredParamWithNull() throws NoSuchMethodException {
            Map<String, Object> input = new HashMap<>();
            input.put("isRequired", false);
            input.put("name", "input1");
            input.put("type", "String");
            setInputParams(new ArrayList<>(Collections.singletonList(input)), false);
            Method method = AppChatServiceImpl.class.getDeclaredMethod("addUserContext",
                    CreateAppChatRequest.class,
                    Map.class,
                    boolean.class,
                    OperationContext.class,
                    String.class);
            method.setAccessible(true);
            CreateAppChatRequest request = new CreateAppChatRequest();
            getCreateAppChatRequest(request, "input1", null);
            request.setAppId("APPID");
            Assertions.assertDoesNotThrow(() -> method.invoke(this.chatService,
                    request,
                    new HashMap<>(),
                    false,
                    new OperationContext(),
                    "workflow"));
        }

        @Test
        @DisplayName("参数中有必填字段，userContext为null，校验报错")
        public void testRequiredParamWithoutUserContext() throws NoSuchMethodException {
            Map<String, Object> input = new HashMap<>();
            input.put("isRequired", true);
            input.put("name", "input1");
            input.put("type", "String");
            setInputParams(new ArrayList<>(Collections.singletonList(input)), true);
            Method method = AppChatServiceImpl.class.getDeclaredMethod("addUserContext",
                    CreateAppChatRequest.class,
                    Map.class,
                    boolean.class,
                    OperationContext.class,
                    String.class);
            method.setAccessible(true);
            CreateAppChatRequest request = new CreateAppChatRequest();
            CreateAppChatRequest.Context context = new CreateAppChatRequest.Context();
            Map<String, Object> userContext = new HashMap<>();
            context.setUserContext(userContext);
            request.setContext(context);
            request.setAppId("APPID");
            failureSituation(request, "user context", "app");
        }

        @Test
        @DisplayName("应用自定义参数中没有必填字段，userContext为null，校验不报错")
        public void testAppNonRequiredParamWithoutUserContext() throws NoSuchMethodException {
            Map<String, Object> input = new HashMap<>();
            input.put("isRequired", false);
            input.put("name", "input1");
            input.put("type", "String");
            setInputParams(new ArrayList<>(Collections.singletonList(input)), true);
            Method method = AppChatServiceImpl.class.getDeclaredMethod("addUserContext",
                    CreateAppChatRequest.class,
                    Map.class,
                    boolean.class,
                    OperationContext.class,
                    String.class);
            method.setAccessible(true);
            CreateAppChatRequest request = new CreateAppChatRequest();
            CreateAppChatRequest.Context context = new CreateAppChatRequest.Context();
            Map<String, Object> userContext = new HashMap<>();
            context.setUserContext(userContext);
            request.setContext(context);
            request.setAppId("APPID");
            Assertions.assertDoesNotThrow(() -> method.invoke(this.chatService,
                    request,
                    new HashMap<>(),
                    false,
                    new OperationContext(),
                    "app"));
        }

        @Test
        @DisplayName("工作流自定义参数中没有必填字段，userContext为null，校验不报错")
        public void testWorkflowNonRequiredParamWithoutUserContext() throws NoSuchMethodException {
            Map<String, Object> input = new HashMap<>();
            input.put("isRequired", false);
            input.put("name", "input1");
            input.put("type", "String");
            setInputParams(new ArrayList<>(Collections.singletonList(input)), false);
            Method method = AppChatServiceImpl.class.getDeclaredMethod("addUserContext",
                    CreateAppChatRequest.class,
                    Map.class,
                    boolean.class,
                    OperationContext.class,
                    String.class);
            method.setAccessible(true);
            CreateAppChatRequest request = new CreateAppChatRequest();
            CreateAppChatRequest.Context context = new CreateAppChatRequest.Context();
            Map<String, Object> userContext = new HashMap<>();
            context.setUserContext(userContext);
            request.setContext(context);
            request.setAppId("APPID");
            Assertions.assertDoesNotThrow(() -> method.invoke(this.chatService,
                    request,
                    new HashMap<>(),
                    false,
                    new OperationContext(),
                    "workflow"));
        }

        @Test
        @DisplayName("参数中有必填字段，userContext中没有传该字段，校验报错")
        public void testRequiredParamNotExistInUserContext() throws NoSuchMethodException {
            Map<String, Object> input = new HashMap<>();
            input.put("isRequired", true);
            input.put("name", "input1");
            input.put("type", "String");
            setInputParams(new ArrayList<>(Collections.singletonList(input)), false);
            Method method = AppChatServiceImpl.class.getDeclaredMethod("addUserContext",
                    CreateAppChatRequest.class,
                    Map.class,
                    boolean.class,
                    OperationContext.class,
                    String.class);
            method.setAccessible(true);
            CreateAppChatRequest request = new CreateAppChatRequest();
            getCreateAppChatRequest(request, "not_input1", "1234");
            request.setAppId("APPID");
            failureSituation(request, "input1", "workflow");
        }

        @Test
        @DisplayName("组合场景，校验通过")
        public void testValidCombinationSituation() throws NoSuchMethodException {
            List<String> names = Arrays.asList("not_required_input",
                    "string_input",
                    "string_input_2",
                    "integer_input",
                    "integer_input_2",
                    "boolean_input",
                    "boolean_input_2",
                    "number_input",
                    "number_input_2",
                    "number_input_3");
            List<String> types = Arrays.asList("String",
                    "String",
                    "String",
                    "Integer",
                    "Integer",
                    "Boolean",
                    "Boolean",
                    "Number",
                    "Number",
                    "Number");
            List<Boolean> requiredStates =
                    Arrays.asList(false, true, false, true, false, true, false, true, false, true);
            setInputParams(createInputList(names, types, requiredStates), true);
            Method method = AppChatServiceImpl.class.getDeclaredMethod("addUserContext",
                    CreateAppChatRequest.class,
                    Map.class,
                    boolean.class,
                    OperationContext.class,
                    String.class);
            method.setAccessible(true);
            String testInput = java.util.stream.Stream.generate(() -> "A").limit(500).collect(Collectors.joining());
            CreateAppChatRequest request = new CreateAppChatRequest();
            getCreateAppChatRequest(request, "string_input", "你");
            getCreateAppChatRequest(request, "string_input", testInput);
            request.getContext().getUserContext().put("integer_input", -999999999);
            request.getContext().getUserContext().put("integer_input_2", 999999999);
            request.getContext().getUserContext().put("boolean_input", true);
            request.getContext().getUserContext().put("boolean_input_2", false);
            request.getContext().getUserContext().put("number_input", -999999999.99);
            request.getContext().getUserContext().put("number_input_2", 999999999.99);
            request.getContext().getUserContext().put("number_input_3", 1.1);
            request.setAppId("APPID");
            Assertions.assertDoesNotThrow(() -> method.invoke(this.chatService,
                    request,
                    new HashMap<>(),
                    false,
                    new OperationContext(),
                    "app"));
        }

        @Test
        @DisplayName("字符串类型自定义参数输入不合法，校验失败")
        public void testStringInputWithInvalidInput() throws NoSuchMethodException {
            // 字符串长度为1~500
            String testInput = java.util.stream.Stream.generate(() -> "A").limit(501).collect(Collectors.joining());
            testInvalidInputParam("String", testInput);
            testInvalidInputParam("String", "");
            testInvalidInputParam("String", 123);
            testInvalidInputParam("String", true);
        }

        @Test
        @DisplayName("布尔类型自定义参数输入不合法，校验失败")
        public void testBooleanInputWithInvalidInput() throws NoSuchMethodException {
            testInvalidInputParam("Boolean", "true");
            testInvalidInputParam("Boolean", 123);
        }

        @Test
        @DisplayName("整型自定义参数输入不合法，校验失败")
        public void testIntegerInputWithInvalidInput() throws NoSuchMethodException {
            // 范围为-999999999~999999999
            testInvalidInputParam("Integer", "123");
            testInvalidInputParam("Integer", 1000000000);
            testInvalidInputParam("Integer", -1000000000);
            testInvalidInputParam("Integer", -999999999.1);
            testInvalidInputParam("Integer", false);
        }

        @Test
        @DisplayName("数字类型自定义参数输入不合法，校验失败")
        public void testNumberInputWithInvalidInput() throws NoSuchMethodException {
            // 范围为-999999999.99~999999999.99，两位小数
            testInvalidInputParam("Number", -1000000000);
            testInvalidInputParam("Number", 1000000000);
            testInvalidInputParam("Number", "hi");
            testInvalidInputParam("Number", 1.999);
            testInvalidInputParam("Number", true);
        }

        private void testInvalidInputParam(String paramType, Object inputValue) throws NoSuchMethodException {
            CacheUtils.clear();
            Map<String, Object> input = new HashMap<>();
            String paramName = paramType + "_input";
            input.put("isRequired", true);
            input.put("name", paramName);
            input.put("type", paramType);
            setInputParams(new ArrayList<>(Collections.singletonList(input)), true);
            CreateAppChatRequest request = new CreateAppChatRequest();
            request.setAppId("APPID");
            getCreateAppChatRequest(request, paramName, inputValue);
            failureSituation(request, paramName, "app");
        }

        private void failureSituation(CreateAppChatRequest request, String invalidInput, String appType)
                throws NoSuchMethodException {
            Method method = AppChatServiceImpl.class.getDeclaredMethod("addUserContext",
                    CreateAppChatRequest.class,
                    Map.class,
                    boolean.class,
                    OperationContext.class,
                    String.class);
            method.setAccessible(true);
            Exception exception = Assertions.assertThrows(Exception.class, () -> {
                try {
                    method.invoke(this.chatService, request, new HashMap<>(), false, new OperationContext(), appType);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            });
            Assertions.assertInstanceOf(AippParamException.class, exception);
            Assertions.assertEquals(StringUtils.format("非法参数: {0}。", invalidInput), exception.getMessage());
        }

        private void getCreateAppChatRequest(CreateAppChatRequest request, String paramName, Object value) {
            CreateAppChatRequest.Context context = new CreateAppChatRequest.Context();
            Map<String, Object> userContext = new HashMap<>();
            userContext.put(paramName, value);
            context.setUserContext(userContext);
            request.setContext(context);
        }

        private void setInputParams(List<Map<String, Object>> inputs, boolean isApp) {
            Mockito.lenient()
                    .when(this.flowsService1.getFlows(Mockito.any(), Mockito.any()))
                    .thenReturn(mockFlowInfo(inputs, isApp));
        }

        private List<Map<String, Object>> createInputList(List<String> names, List<String> types,
                List<Boolean> requiredStates) {
            List<Map<String, Object>> inputList = new ArrayList<>();
            for (int i = 0; i < names.size(); i++) {
                Map<String, Object> inputMap = new HashMap<>();
                inputMap.put("isRequired", requiredStates.get(i));
                inputMap.put("name", names.get(i));
                inputMap.put("type", types.get(i));
                inputList.add(inputMap);
            }
            return inputList;
        }
    }

    private List<QueryChatRsp> mockChatList() {
        Map<String, Object> attributesOrigin = new HashMap<>();
        attributesOrigin.put(AippConst.ATTR_CHAT_INST_ID_KEY, "instanceId");
        attributesOrigin.put(AippConst.ATTR_CHAT_STATE_KEY, AppState.INACTIVE.getName());
        QueryChatRsp chat1 = QueryChatRsp.builder()
                .chatId("chatId1")
                .chatName("1+1")
                .attributes(JsonUtils.toJsonString(attributesOrigin))
                .build();
        Map<String, Object> other = new HashMap<>();
        other.put(AippConst.ATTR_CHAT_INST_ID_KEY, "instIdOther");
        other.put(AippConst.ATTR_CHAT_STATE_KEY, AppState.PUBLISHED.getName());
        other.put(AippConst.ATTR_CHAT_ORIGIN_APP_KEY, "originAppId");
        other.put(AippConst.ATTR_CHAT_ORIGIN_APP_VERSION_KEY, "1.0.0");
        QueryChatRsp chat2 = QueryChatRsp.builder()
                .chatId("chatId2")
                .chatName("1+1")
                .attributes(JsonUtils.toJsonString(other))
                .build();
        return new ArrayList<>(Arrays.asList(chat1, chat2));
    }

    private List<AippInstLog> mockLog() {
        return new ArrayList<>(Collections.singletonList(AippInstLog.builder().logData("{\"msg\":\"hello\"}").build()));
    }

    private Meta mockMeta() {
        Meta meta = new Meta();
        meta.setId("metaId");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_FLOW_DEF_ID_KEY, "flow_definition_id");
        meta.setAttributes(attributes);
        return meta;
    }

    private FlowInfo mockFlowInfo(List<Map<String, Object>> inputs, boolean isApp) {
        FlowInfo flowInfo = new FlowInfo();
        FlowNodeInfo startNode = new FlowNodeInfo();
        startNode.setType("start");
        List<Map<String, Object>> inputParams = new ArrayList<>();
        Map<String, Object> inputConfig = new HashMap<>();
        inputConfig.put("name", "input");
        if (isApp) {
            Map<String, Object> appDefaultInput = new HashMap<>();
            appDefaultInput.put("isRequired", true);
            appDefaultInput.put("name", "Question");
            inputs.add(appDefaultInput);
        }
        inputConfig.put("value", inputs);
        inputParams.add(inputConfig);
        Map<String, Object> startNodeConfig = new HashMap<>();
        startNodeConfig.put("inputParams", inputParams);
        startNode.setProperties(startNodeConfig);
        flowInfo.setFlowNodes(Collections.singletonList(startNode));
        return flowInfo;
    }
}
