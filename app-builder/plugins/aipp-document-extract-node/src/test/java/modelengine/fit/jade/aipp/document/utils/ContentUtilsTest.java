/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link ContentUtils} 的测试类。
 *
 * @author 兰宇晨
 * @since 2025-01-20
 */
public class ContentUtilsTest {
    @Test
    @DisplayName("测试读取文件名成功")
    void shouldOkWhenGetFileName() {
        String fileUrl = "http://mock.com/mock.bucket/mock.txt";
        assertThat(ContentUtils.getFileName(fileUrl)).isEqualTo("mock.txt");
    }

    @Test
    @DisplayName("测试空文件链接解析文件名失败")
    void shouldNotOkWhenGetFileNameFromNull() {
        String fileUrl = "";
        assertThatThrownBy(() -> ContentUtils.getFileName(fileUrl)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("测试无文件名链接解析文件名失败")
    void shouldNotOkWhenExtractFileNameWithWrongUrl() {
        String fileUrl = "http://mock.com/mock.bucket/";
        assertThatThrownBy(() -> ContentUtils.getFileName(fileUrl)).isInstanceOf(IllegalArgumentException.class);
    }
}
