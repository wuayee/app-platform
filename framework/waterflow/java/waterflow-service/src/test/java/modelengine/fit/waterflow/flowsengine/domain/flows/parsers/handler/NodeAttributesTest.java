/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.handler;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.types.NodeAttributes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * {@link NodeAttributes} 对应测试类
 *
 * @author 张越
 * @since 2024/08/05
 */
public class NodeAttributesTest {
    @Test
    @DisplayName("基本测试")
    public void basic() {
        // prepare.
        JSONObject data = new JSONObject();
        data.put("id", "id1");
        data.put("type", "start");
        data.put("text", "开始节点");

        JSONObject flowMeta = new JSONObject();
        flowMeta.put("triggerMode", "auto");
        flowMeta.put("task", "task1");
        flowMeta.put("taskFilter", "taskFilter1");
        flowMeta.put("jober", "jober1");
        flowMeta.put("joberFilter", "joberFilter1");
        flowMeta.put("callback", "callback1");
        data.put("flowMeta", flowMeta);

        // when.
        Map<String, Object> result = new NodeAttributes(new AttributesData(data)).getData();

        // then.
        Assertions.assertEquals("id1", result.get("metaId"));
        Assertions.assertEquals("start", result.get("type"));
        Assertions.assertEquals("开始节点", result.get("name"));
        Assertions.assertEquals("auto", result.get("triggerMode"));
        Assertions.assertEquals("task1", result.get("task"));
        Assertions.assertNull(result.get("taskFilter"));
        Assertions.assertEquals("jober1", result.get("jober"));
        Assertions.assertNull(result.get("joberFilter"));
        Assertions.assertEquals("callback1", result.get("callback"));
    }

    @Test
    @DisplayName("当节点类型是state时，可解析出jobFilter和taskFilter")
    public void whenNodeTypeIsStateThenShouldHaveTaskFilterAndJoberFilter() {
        // prepare.
        JSONObject data = new JSONObject();
        data.put("id", "id1");
        data.put("type", "state");
        data.put("text", "状态节点");

        JSONObject flowMeta = new JSONObject();
        flowMeta.put("triggerMode", "auto");
        flowMeta.put("task", "task1");
        flowMeta.put("taskFilter", "taskFilter1");
        flowMeta.put("jober", "jober1");
        flowMeta.put("joberFilter", "joberFilter1");
        flowMeta.put("callback", "callback1");
        data.put("flowMeta", flowMeta);

        // when.
        Map<String, Object> result = new NodeAttributes(new AttributesData(data)).getData();

        // then.
        Assertions.assertEquals("id1", result.get("metaId"));
        Assertions.assertEquals("state", result.get("type"));
        Assertions.assertEquals("状态节点", result.get("name"));
        Assertions.assertEquals("joberFilter1", result.get("joberFilter"));
        Assertions.assertEquals("taskFilter1", result.get("taskFilter"));
    }

    @Test
    @DisplayName("当flowMeta中包含attributes中已有字段时，不会覆盖shape中值")
    public void whenFlowMetaHasKeySameWithAttributesThenShouldNotBeReplaced() {
        // prepare.
        JSONObject data = new JSONObject();
        data.put("id", "id1");
        data.put("type", "state");
        data.put("text", "状态节点");

        JSONObject flowMeta = new JSONObject();
        flowMeta.put("triggerMode", "auto");
        flowMeta.put("task", "task1");
        flowMeta.put("taskFilter", "taskFilter1");
        flowMeta.put("jober", "jober1");
        flowMeta.put("joberFilter", "joberFilter1");
        flowMeta.put("callback", "callback1");
        flowMeta.put("type", "typeInFlowMeta");
        flowMeta.put("name", "nameInFlowMeta");
        flowMeta.put("id", "idInFlowMeta");
        data.put("flowMeta", flowMeta);

        // when.
        Map<String, Object> result = new NodeAttributes(new AttributesData(data)).getData();

        // then.
        Assertions.assertEquals("id1", result.get("metaId"));
        Assertions.assertEquals("state", result.get("type"));
        Assertions.assertEquals("状态节点", result.get("name"));
        Assertions.assertNull(result.get("id"));
    }

    @Test
    @DisplayName("当text不是String类型时，从textInnerHtml中解析名称")
    public void whenTextIsStringThenNameShouldExtractFromTextInnerHtml() {
        // prepare.
        JSONObject data = new JSONObject();
        data.put("id", "id1");
        data.put("type", "state");
        data.put("text", new Object());
        data.put("textInnerHtml", "<p>开始节点</p>");

        // when.
        Map<String, Object> result = new NodeAttributes(new AttributesData(data)).getData();

        // then.
        Assertions.assertEquals("开始节点", result.get("name"));
    }
}
