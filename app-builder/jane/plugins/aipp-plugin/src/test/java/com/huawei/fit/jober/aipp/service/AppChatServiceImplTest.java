/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.enums.AppState;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.genericable.AppBuilderAppService;
import com.huawei.fit.jober.aipp.mapper.AippChatMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.service.impl.AppChatServiceImpl;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.model.Tuple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @BeforeEach
    void before() {
        this.appChatService = new AppChatServiceImpl(this.appFactory, this.aippChatMapper, this.aippRunTimeService,
                this.appService, this.aippLogService, this.appRepository);
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
                .appId(chatAppId).question("你好").chatId("hello")
                .context(CreateAppChatRequest.Context.builder().useMemory(true).atAppId(atChatAppId)
                        .userContext(context).build())
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

        Choir<Object> objectChoir =
                Assertions.assertDoesNotThrow(() -> this.appChatService.chat(hello, operationContext, false));

        Assertions.assertEquals(t2, objectChoir);
    }

    @Test
    @DisplayName("测试重新对话")
    void testRestartChat() {
        Mockito.when(this.aippLogService.getParentPath(Mockito.any())).thenReturn("/instanceId");
        Mockito.when(this.aippChatMapper.selectChatListByInstId(Mockito.eq("instanceId")))
                .thenReturn(this.mockChatList());
        Mockito.when(this.aippLogService.queryLogsByInstanceIdAndLogTypes(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(this.mockLog());
        Mockito.when(this.aippChatMapper.selectChatList(Mockito.any(), Mockito.anyString()))
                .thenReturn(this.mockChatList());
        Mockito.when(this.aippRunTimeService.createInstanceByApp(Mockito.any(), Mockito.any(), Mockito.anyMap(),
                Mockito.any(), Mockito.eq(true))).thenReturn(Tuple.duet("hello_inst", Choir.create((e) -> {})));
        Mockito.when(this.appFactory.create(Mockito.any()))
                .thenReturn(AppBuilderApp.builder().id("id1").version("10.0.0").state("ACTIVE").build());
        Mockito.when(this.appRepository.selectWithId(Mockito.any()))
                .thenReturn(AppBuilderApp.builder().id("id1").version("10.0.0").state("ACTIVE").build());
        Assertions.assertDoesNotThrow(() -> this.appChatService.restartChat("1",
                new HashMap<>(),
                new OperationContext()));
    }

    @Test
    @DisplayName("测试重新对话：没找到对应的对话")
    void testRestartChatFailedNoChat() {
        Mockito.when(this.aippLogService.getParentPath(Mockito.any())).thenReturn("/ /");
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> this.appChatService.restartChat("1", new HashMap<>(), new OperationContext()));
        Assertions.assertEquals(90002929, exception.getCode());
    }

    private List<QueryChatRsp> mockChatList() {
        Map<String, Object> attributesOrigin = new HashMap<>();
        attributesOrigin.put(AippConst.ATTR_CHAT_INST_ID_KEY, "instanceId");
        attributesOrigin.put(AippConst.ATTR_CHAT_STATE_KEY, AppState.INACTIVE.getName());
        QueryChatRsp chat1 = QueryChatRsp.builder().chatId("chatId1").chatName("1+1")
                .attributes(JsonUtils.toJsonString(attributesOrigin)).build();
        Map<String, Object> other = new HashMap<>();
        other.put(AippConst.ATTR_CHAT_INST_ID_KEY, "instIdOther");
        other.put(AippConst.ATTR_CHAT_STATE_KEY, AppState.PUBLISHED.getName());
        other.put(AippConst.ATTR_CHAT_ORIGIN_APP_KEY, "originAppId");
        other.put(AippConst.ATTR_CHAT_ORIGIN_APP_VERSION_KEY, "1.0.0");
        QueryChatRsp chat2 = QueryChatRsp.builder().chatId("chatId2").chatName("1+1")
                .attributes(JsonUtils.toJsonString(other)).build();
        return new ArrayList<>(Arrays.asList(chat1, chat2));
    }

    private List<AippInstLog> mockLog() {
        return new ArrayList<>(Collections.singletonList(AippInstLog.builder().logData("{\"msg\":\"hello\"}").build()));
    }
}
