/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.globalization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

/**
 * 表示 {@link ResourceBundleStringResource} 的单元测试。
 *
 * @author 梁济时
 * @since 2022-11-21
 */
@DisplayName("测试 ResourceBundleStringResource 类型")
class ResourceBundleStringResourceTest {
    @Test
    @DisplayName("使用指定的语言环境中定义的资源格式化")
    void should_return_formatted_message() {
        ResourceBundleStringResource bundle =
                new ResourceBundleStringResource(this.getClass().getClassLoader(), "i18n/message", "UTF-8");
        String messageEn = bundle.getMessage(new Locale("en"), "HELLO", "world");
        assertEquals("Hello, world!", messageEn);
        String messageZh = bundle.getMessage(new Locale("zh"), "HELLO", "world");
        assertEquals("你好，world！", messageZh);
    }
}
