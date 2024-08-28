/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.ui.globalization;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.globalization.StringResource;
import modelengine.fitframework.plugin.Plugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

/**
 * {@link LocaleUiWord} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-19
 */
@ExtendWith(MockitoExtension.class)
public class LocaleUiWordTest {
    private LocaleUiWord localeUiWord;
    private Plugin plugin;
    private StringResource stringResource;
    private MockedStatic<UserContextHolder> opContextHolderMock;

    @BeforeEach
    void setUp() {
        this.plugin = mock(Plugin.class);
        this.stringResource = mock(StringResource.class);
        this.opContextHolderMock = mockStatic(UserContextHolder.class);
        this.opContextHolderMock.when(UserContextHolder::get)
                .thenReturn(new UserContext("Jane", "127.0.0.1", "en"));
        this.localeUiWord = new LocaleUiWord(plugin);
    }

    @AfterEach
    void teardown() {
        this.opContextHolderMock.close();
    }

    @Test
    @DisplayName("获取locale message结果应该正确")
    void shouldCorrectWhenGetLocaleMessage() {
        Mockito.when(this.plugin.sr()).thenReturn(this.stringResource);
        Mockito.when(this.stringResource.getMessage(Mockito.any(), Mockito.anyString()))
                .thenReturn("locale message");
        String localeMessage = this.localeUiWord.getLocaleMessage("default message");
        Assertions.assertEquals("locale message", localeMessage);
    }

    @Test
    @DisplayName("获取默认信息结果应该正确")
    void shouldCorrectWhenHandleLocaleMessageNoResource() {
        Mockito.when(this.plugin.sr()).thenReturn(this.stringResource);
        Mockito.doThrow(new FitException(""))
                .when(this.stringResource)
                .getMessage(Mockito.any(), Mockito.anyString());
        String localeMessage = this.localeUiWord.getLocaleMessage("default message");
        Assertions.assertEquals("", localeMessage);
    }

    @Test
    @DisplayName("获取 locale 结果应该正确")
    void shouldCorrectWhenGetLocale() {
        Assertions.assertEquals(Locale.ENGLISH, this.localeUiWord.getLocale());
    }
}
