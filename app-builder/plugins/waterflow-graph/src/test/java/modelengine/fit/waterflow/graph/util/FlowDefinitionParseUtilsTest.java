/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.waterflow.graph.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 流程定义解析测试
 *
 * @author liuxinhong
 * @since 2024/01/16
 */
@ExtendWith(MockitoExtension.class)
public class FlowDefinitionParseUtilsTest {
    @Test
    void shouldOKWhenParsedGraphData() {
        String graph = "{\"id\":\"98e92246c776470084c41abafa9a7cc6\",\"title\":\"AippFlow\","
                + "\"pages\":[{\"shapes\":[{\"type\":\"aippStart\",\"id\":\"qt63ez\",\"text\":\"开始\"}],\"index\":0}],"
                + "\"exceptionFitables\":[\"modelengine.fit.jober.aipp.fitable.AippFlowExceptionHandler\"]}";
        JSONObject inputObj = JSON.parseObject(graph);
        String result = FlowDefinitionParseUtils.getParsedGraphData(inputObj, "1.0.0");
        Assertions.assertFalse(result.isEmpty());
        JSONObject obj = JSON.parseObject(result);
        Assertions.assertEquals(inputObj.get("id"), obj.get("metaId"));
        Assertions.assertTrue(obj.containsKey("exceptionFitables"));
    }

    @Test
    void shouldGetFlowMetaItemWhenParsedGraphDataGivenGraphDataWithFlowMeta() {
        String graph = "{\n" + "\t\"id\": \"98e92246c776470084c41abafa9a7cc6\",\n" + "\t\"title\": \"AippFlow\",\n"
                + "\t\"pages\": [\n" + "\t\t{\n" + "\t\t\t\"shapes\": [\n" + "\t\t\t\t{\n"
                + "\t\t\t\t\t\"type\": \"aippStart\",\n" + "\t\t\t\t\t\"id\": \"qt63ez\",\n"
                + "\t\t\t\t\t\"text\": \"开始\"\n" + "\t\t\t\t}\n" + "\t\t\t],\n" + "\t\t\t\"index\": 0\n" + "\t\t}\n"
                + "\t],\n" + "\t\"exceptionFitables\": [\n"
                + "\t\t\"modelengine.fit.jober.aipp.fitable.AippFlowExceptionHandler\"\n" + "\t],\n"
                + "\t\"flowMeta\": {\n" + "\t\t\"enableOutputScope\": true\n" + "\t}\n" + "}";
        JSONObject inputObj = JSON.parseObject(graph);
        String result = FlowDefinitionParseUtils.getParsedGraphData(inputObj, "1.0.0");
        Assertions.assertFalse(result.isEmpty());
        JSONObject obj = JSON.parseObject(result);
        Assertions.assertEquals(inputObj.get("id"), obj.get("metaId"));
        Assertions.assertTrue(obj.containsKey("exceptionFitables"));
        Assertions.assertTrue(obj.containsKey("enableOutputScope"));
        Assertions.assertEquals(true, obj.get("enableOutputScope"));
    }
}
