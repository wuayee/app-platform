/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.exception;

import modelengine.fitframework.exception.FitException;

import com.huawei.jade.store.tool.parser.code.PluginDeployRetCode;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link PluginDeployException} 测试类。
 *
 * @since 2024-08-16
 */
public class PluginDeployExceptionTest {
    /**
     * 测试异常中错误信息占位符保存到 {@link FitException} 中的properties。
     */
    @DisplayName("测试构造PluginDeployException")
    @Test
    void shouldSuccessConstructPluginDeployException() {
        PluginDeployException actual = new PluginDeployException(PluginDeployRetCode.FILE_MISSING_ERROR, "file.json");
        Map<String, String> expectedProperties = new HashMap<>();
        expectedProperties.put("0", "file.json");
        Assertions.assertThat(actual.getProperties()).isEqualTo(expectedProperties);
        Assertions.assertThat(actual.getCode()).isEqualTo(PluginDeployRetCode.FILE_MISSING_ERROR.getCode());
    }
}
