/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.genericable.AppBuilderAppService;
import com.huawei.fit.jober.aipp.mapper.AippChatMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.service.impl.AppChatServiceImpl;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.model.Tuple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
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
    private AppBuilderAppRepository appRepository;

    @BeforeEach
    void before() {
        this.appChatService = new AppChatServiceImpl(this.appFactory,
                this.aippChatMapper,
                this.aippRunTimeService,
                this.appService,
                this.appRepository);
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
}
