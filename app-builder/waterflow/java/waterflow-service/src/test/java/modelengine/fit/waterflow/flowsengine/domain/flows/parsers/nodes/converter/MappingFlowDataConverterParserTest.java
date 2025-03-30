/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.FlowsDataBaseTest;
import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

/**
 * MappingFlowDataConverterParser对应测试
 *
 * @author yangxiangyu
 * @since 2024/8/19
 */
@ExtendWith(MethodNameLoggerExtension.class)
class MappingFlowDataConverterParserTest extends FlowsDataBaseTest {
    private static final String PARSER_FILE_PATH = "flows/parsers/";

    private MappingFlowDataConverterParser mappingFlowDataConverterParser;

    @Test
    @DisplayName("测试FlowDataConverter解析成功")
    public void testMappingFlowDataConverterParserSuccess() {
        String json = getJsonData(getFilePath("flow_with_flow_data_converter.json"));
        Map<String, Object> converterConfig = JSONObject.parseObject(json);
        mappingFlowDataConverterParser = new MappingFlowDataConverterParser();

        FlowDataConverter converter = mappingFlowDataConverterParser.parse(converterConfig);

        Assertions.assertEquals("output", converter.getOutputName());
    }

    @Override
    protected String getFilePathPrefix() {
        return PARSER_FILE_PATH;
    }
}