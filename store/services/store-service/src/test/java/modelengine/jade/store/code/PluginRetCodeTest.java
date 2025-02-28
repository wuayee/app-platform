/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.code;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 表示 {@link PluginRetCode} 的测试类。
 *
 * @author 张雪彬
 * @since 2024-08-16
 */
@DisplayName("测试插件部署错误码")
public class PluginRetCodeTest {
    @ParameterizedTest
    @CsvSource({
            "NO_FILE_UPLOADED_ERROR, 130901001",
            "UPLOADED_FILE_FORMAT_ERROR, 130901002",
            "NO_PLUGIN_FOUND_ERROR, 130901003",
            "FILE_MISSING_ERROR, 130901004",
            "PLUGIN_UNIQUE_CHECK_ERROR, 130901005",
            "JSON_PARSE_ERROR, 130901006",
            "PLUGIN_NOT_EXISTS, 130901007",
            "PLUGIN_COMPLETENESS_CHECK_ERROR, 130901008",
            "FIELD_ERROR_IN_SCHEMA, 130901009",
            "PLUGIN_VALIDATION_FIELD, 130901010"
    })
    @DisplayName("测试插件部署错误码")
    void testPluginDeployRetCode(PluginRetCode code, int expectCode) {
        Assertions.assertThat(code.getCode()).isEqualTo(expectCode);
    }
}