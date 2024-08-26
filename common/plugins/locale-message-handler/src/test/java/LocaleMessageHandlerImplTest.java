/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

import modelengine.fitframework.globalization.StringResource;
import modelengine.fitframework.plugin.Plugin;

import com.huawei.jade.common.localemessage.LocaleMessageHandlerImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link LocaleMessageHandlerImpl} 测试类。
 *
 * @since 2024-08-16
 */
@ExtendWith(MockitoExtension.class)
public class LocaleMessageHandlerImplTest {
    private final String defaultSystemErrorMessageKey = "500";

    @Mock
    private Plugin plugin;

    @Mock
    private StringResource stringResource;

    @InjectMocks
    private LocaleMessageHandlerImpl localeMessageHandler;

    @Test
    @DisplayName("测试获取locale message")
    void shouldSuccessHandleLocaleMessage() {
        Mockito.when(plugin.sr()).thenReturn(stringResource);
        Mockito.when(stringResource.getMessage(Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any()))
            .thenReturn("locale message");
        String localeMessage = localeMessageHandler.getLocaleMessage("1", "default message");
        Assertions.assertEquals("locale message", localeMessage);
    }

    @Test
    @DisplayName("测试获取默认信息")
    void shouldSuccessHandleLocaleMessageWhenNoResource() {
        Mockito.when(plugin.sr()).thenReturn(stringResource);
        Mockito.when(stringResource.getMessage(Mockito.any(), Mockito.eq(defaultSystemErrorMessageKey)))
            .thenReturn("system default message");
        Mockito.doThrow(new NullPointerException())
            .when(stringResource)
            .getMessage(Mockito.any(), Mockito.eq("1"), Mockito.any(), Mockito.any());
        String localeMessage = localeMessageHandler.getLocaleMessage("1", "default message");
        Assertions.assertEquals("system default message", localeMessage);
    }
}
