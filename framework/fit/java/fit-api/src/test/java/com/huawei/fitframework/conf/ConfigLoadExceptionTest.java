/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.conf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link ConfigLoadException} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-16
 */
@DisplayName("测试 ConfigLoadException 类")
class ConfigLoadExceptionTest {
    @Test
    @DisplayName("提供 ConfigLoadException 类，返回异常信息")
    void givenConfigLoadExceptionShouldReturnExceptionMessage() {
        ConfigLoadException exception = new ConfigLoadException("123");
        assertThat(exception.getMessage()).isEqualTo("123");
    }

    @Test
    @DisplayName("提供 ConfigLoadException 类，返回引发异常的原因的异常")
    void givenConfigLoadExceptionShouldThrowException() {
        NullPointerException nullPointerException = new NullPointerException();
        ConfigLoadException exception = new ConfigLoadException("123", nullPointerException);
        assertThat(exception.getCause()).isEqualTo(nullPointerException);
    }
}