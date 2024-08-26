/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.service.impl.AppChatSseServiceImpl;

import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.flowable.emitter.DefaultEmitter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * {@link AppChatSseService} 的测试类
 *
 * @author 姚江
 * @since 2024-08-01
 */
@ExtendWith(MockitoExtension.class)
public class AppChatSseServiceImplTest {
    private AppChatSseService appChatSseService;

    @Mock
    private Emitter<Object> emitter;

    @Mock
    private AippLogService logService;

    @Mock
    private CountDownLatch latch;

    @BeforeEach
    void before() {
        this.appChatSseService = new AppChatSseServiceImpl(logService);
    }

    @Test
    @DisplayName("测试添加")
    void testAddEmitter() {
        Emitter<Object> e = new DefaultEmitter<>();
        Assertions.assertDoesNotThrow(() -> this.appChatSseService.addEmitter("hello", e, new CountDownLatch(0)));
        Optional<Emitter<Object>> hello = this.appChatSseService.getEmitter("hello");
        Assertions.assertTrue(hello.isPresent());
        Assertions.assertEquals(e, hello.get());
    }

    @Test
    @DisplayName("测试获取")
    void testGetEmitter() {
        Emitter<Object> e = new DefaultEmitter<>();
        this.appChatSseService.addEmitter("hello", e, new CountDownLatch(0));
        Optional<Emitter<Object>> hello =
                Assertions.assertDoesNotThrow(() -> this.appChatSseService.getEmitter("hello"));
        Assertions.assertTrue(hello.isPresent());
        Assertions.assertEquals(e, hello.get());
    }

    @Test
    @DisplayName("测试发送最后的消息")
    void testSendLastData() {
        this.appChatSseService.addEmitter("hello", emitter, latch);
        Assertions.assertDoesNotThrow(() -> appChatSseService.send("hello", "hello"));
        Assertions.assertDoesNotThrow(() -> appChatSseService.sendLastData("hello", "hello"));
        Mockito.verify(emitter, Mockito.times(2)).emit("hello");
        Mockito.verify(emitter, Mockito.times(1)).complete();
        Mockito.verify(latch, Mockito.times(1)).countDown();
    }

    @Test
    @DisplayName("测试发送最后的消息到祖先")
    void testSendLastAncestor() {
        this.appChatSseService.addEmitter("hello", emitter, latch);
        Mockito.when(logService.getParentPath("hello world")).thenReturn("world/hello");
        Assertions.assertDoesNotThrow(() -> appChatSseService.sendToAncestorLastData("hello world", "hello"));
        Mockito.verify(emitter, Mockito.times(1)).emit("hello");
        Mockito.verify(emitter, Mockito.times(1)).complete();
    }
}
