/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.globalization;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.globalization.StringResource;
import modelengine.fitframework.plugin.Plugin;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.globalization.impl.LocaleServiceImpl;

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
import java.util.MissingResourceException;

/**
 * {@link LocaleServiceImpl} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-19
 */
@ExtendWith(MockitoExtension.class)
public class LocaleServiceImplTest {
    private static final String DEFAULT_ERROR_CODE = String.valueOf(CommonRetCode.INTERNAL_ERROR.getCode());

    @Fit
    private LocaleService localeService;
    @Fit
    private Plugin plugin;
    @Fit
    private StringResource stringResource;
    private MockedStatic<UserContextHolder> opContextHolderMock;

    @BeforeEach
    void setUp() {
        this.plugin = mock(Plugin.class);
        this.stringResource = mock(StringResource.class);
        this.opContextHolderMock = mockStatic(UserContextHolder.class);
        this.opContextHolderMock.when(UserContextHolder::get)
                .thenReturn(new UserContext("Jane", "127.0.0.1", "en"));
        this.localeService = new LocaleServiceImpl(plugin);
    }

    @AfterEach
    void teardown() {
        this.opContextHolderMock.close();
    }

    @Test
    @DisplayName("获取locale message结果应该正确")
    void shouldCorrectWhenGetLocaleMessage() {
        String code = "TEST_CODE";
        String localMsg = "locale message";
        Mockito.when(this.plugin.sr()).thenReturn(this.stringResource);
        Mockito.when(this.stringResource.getMessage(Locale.ENGLISH, code))
                .thenReturn(localMsg);
        String localeMessage = this.localeService.localize(code);
        Assertions.assertEquals(localMsg, localeMessage);
    }

    @Test
    @DisplayName("获取locale message结果抛异常应该为空")
    void shouldCorrectWhenGetLocaleMessageNoResources() {
        String code = "TEST_CODE";
        String localMsg = "locale message";
        Mockito.when(this.plugin.sr()).thenReturn(this.stringResource);
        Mockito.doThrow(new MissingResourceException("", "", ""))
                .when(stringResource)
                .getMessage(Locale.ENGLISH, code);
        String localeMessage = this.localeService.localize(code);
        Assertions.assertEquals("", localeMessage);
    }

    @Test
    @DisplayName("传入系统语言，获取locale message结果应该正确")
    void shouldCorrectWhenInputLocale() {
        String code = "TEST_CODE";
        String localMsg = "locale message";
        Mockito.when(this.plugin.sr()).thenReturn(this.stringResource);
        Mockito.when(this.stringResource.getMessage(Locale.ENGLISH, code)).thenReturn(localMsg);
        String localeMessage = this.localeService.localize(Locale.ENGLISH, code);
        Assertions.assertEquals(localMsg, localeMessage);
    }

    @Test
    @DisplayName("传入系统语言，获取locale message结果抛异常应该为空")
    void shouldCorrectWhenInputLocaleNoResources() {
        String code = "TEST_CODE";
        String localMsg = "locale message";
        Mockito.when(this.plugin.sr()).thenReturn(this.stringResource);
        Mockito.doThrow(new MissingResourceException("", "", ""))
                .when(stringResource)
                .getMessage(Locale.ENGLISH, code);
        String localeMessage = this.localeService.localize(Locale.ENGLISH, code);
        Assertions.assertEquals("", localeMessage);
    }

    @Test
    @DisplayName("获取默认信息结果应该正确")
    void shouldCorrectWhenLocalizeOrDefault() {
        String testCode = "testCode";
        String defaultMsg = "default message";
        Mockito.when(this.plugin.sr()).thenReturn(this.stringResource);
        Mockito.when(this.stringResource.getMessage(Locale.ENGLISH, DEFAULT_ERROR_CODE)).thenReturn(defaultMsg);
        Mockito.doThrow(new MissingResourceException("", "", ""))
                .when(stringResource)
                .getMessage(Locale.ENGLISH, testCode);
        String localeMessage = this.localeService.localizeOrDefault(testCode, DEFAULT_ERROR_CODE);
        Assertions.assertEquals(defaultMsg, localeMessage);
    }

    @Test
    @DisplayName("传入系统消息，获取默认信息结果应该正确")
    void shouldCorrectWhenLocalizeOrDefaultInputLocale() {
        String testCode = "testCode";
        String defaultMsg = "default message";
        Mockito.when(this.plugin.sr()).thenReturn(this.stringResource);
        Mockito.when(this.stringResource.getMessage(Locale.ENGLISH, DEFAULT_ERROR_CODE)).thenReturn(defaultMsg);
        Mockito.doThrow(new MissingResourceException("", "", ""))
                .when(stringResource)
                .getMessage(Locale.ENGLISH, testCode);
        String localeMessage = this.localeService.localizeOrDefault(Locale.ENGLISH, testCode, DEFAULT_ERROR_CODE);
        Assertions.assertEquals(defaultMsg, localeMessage);
    }
}
