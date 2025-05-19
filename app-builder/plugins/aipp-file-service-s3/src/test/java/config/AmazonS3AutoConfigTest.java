/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import modelengine.fit.jade.aipp.s3.file.config.AmazonS3AutoConfig;
import modelengine.fit.jade.aipp.s3.file.param.AmazonS3Param;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 测试 AmazonS3AutoConfig。
 *
 * @author 兰宇晨
 * @since 2024-12-26
 */
@FitTestWithJunit(includeClasses = {AmazonS3AutoConfig.class, AmazonS3Param.class})
public class AmazonS3AutoConfigTest {
    @Fit
    private AmazonS3AutoConfig amazonS3AutoConfig;

    private AmazonS3Param amazonS3Param;

    @Test
    @DisplayName("自动配置 AmazonS3客户端成功")
    void shouldReturnOKWhenCreateAmazonS3() {
        assertDoesNotThrow(() -> amazonS3AutoConfig.amazonS3());
    }
}
