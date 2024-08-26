/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool;

import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.init.serialization.AippJacksonObjectSerializer;
import com.huawei.fit.jober.aipp.service.AppBuilderAppService;
import com.huawei.fit.jober.aipp.tool.impl.AppBuilderAppToolImpl;

import modelengine.fitframework.serialization.ObjectSerializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 为 {@link AppBuilderAppTool} 及其实现 {@link AppBuilderAppToolImpl} 提供单元测试
 *
 * @author 姚江
 * @since 2024-08-06
 */
@ExtendWith(MockitoExtension.class)
public class AppBuilderAppToolTest {
    private AppBuilderAppTool appBuilderAppTool;

    @Mock
    private AppBuilderAppService appService;

    private final String appEngineUrl = "localhost";

    @BeforeEach
    void before() {
        ObjectSerializer serializer =
                new AippJacksonObjectSerializer(AippJacksonObjectSerializer.DEFAULT_DATE_TIME_FORMAT);
        this.appBuilderAppTool = new AppBuilderAppToolImpl(appService, serializer, this.appEngineUrl);
    }

    @Test
    @DisplayName("创建app方法测试")
    void testCreateApp() {
        Mockito.when(appService.create(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(AppBuilderAppDto.builder().id("id").build());
        String s = Assertions.assertDoesNotThrow(() -> this.appBuilderAppTool.createApp("defaultErrorInfo", "me"));
        Assertions.assertTrue(s.contains(this.appEngineUrl));
        Assertions.assertTrue(s.endsWith("id"));
    }
}
