/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.handler;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.FlowAttributes;
import modelengine.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * {@link FlowAttributes} 对应测试类
 *
 * @author 张越
 * @since 2024/08/05
 */
public class FlowAttributesTest {
    @Test
    @DisplayName("基本测试")
    public void basic() {
        // prepare.
        JSONObject data = new JSONObject();
        data.put("title", "title1");
        data.put("id", "id1");
        data.put("exceptionFitables", "exceptionFitables1111111");

        JSONObject flowMeta = new JSONObject();
        flowMeta.put("testFlowMeta", "test111111");
        data.put("flowMeta", flowMeta);

        JSONObject shape = new JSONObject();
        shape.put("id", "shapeId1");
        shape.put("type", "start");
        shape.put("text", "开始节点");

        JSONObject shapeFlowMeta = new JSONObject();
        shapeFlowMeta.put("triggerMode", "auto");
        shapeFlowMeta.put("task", "task1");
        shapeFlowMeta.put("taskFilter", "taskFilter1");
        shapeFlowMeta.put("jober", "jober1");
        shapeFlowMeta.put("joberFilter", "joberFilter1");
        shapeFlowMeta.put("callback", "callback1");
        shape.put("flowMeta", shapeFlowMeta);

        JSONArray shapes = new JSONArray();
        shapes.add(shape);

        JSONObject page = new JSONObject();
        page.put("shapes", shapes);

        JSONArray pages = new JSONArray();
        pages.add(page);

        data.put("pages", pages);

        // when.
        Map<String, Object> result = new FlowAttributes(data, "0.1.1").getData();

        // then.
        Assertions.assertEquals("title1", result.get("name"));
        Assertions.assertEquals("id1", result.get("metaId"));
        Assertions.assertEquals("0.1.1", result.get("version"));
        Assertions.assertEquals("active", result.get("status"));
        Assertions.assertNotNull(result.get("testFlowMeta"));
        Assertions.assertEquals("test111111", result.get("testFlowMeta"));

        List<Map<String, Object>> nodes = ObjectUtils.cast(result.get("nodes"));
        Assertions.assertEquals(1, nodes.size());
    }

    @Test
    @DisplayName("当节点runnable为false时，jadeFlow中不会有shape节点")
    public void whenNodeIsNotRunnableThenJadeFlowNodeSizeIs0() {
        // prepare.
        JSONObject data = new JSONObject();

        JSONObject shape = new JSONObject();
        shape.put("id", "shapeId1");
        shape.put("type", "start");
        shape.put("text", "开始节点");
        shape.put("runnable", false);

        JSONArray shapes = new JSONArray();
        shapes.add(shape);

        JSONObject page = new JSONObject();
        page.put("shapes", shapes);

        JSONArray pages = new JSONArray();
        pages.add(page);

        data.put("pages", pages);

        // when.
        Map<String, Object> result = new FlowAttributes(data, "0.1.1").getData();

        // then.
        List<Map<String, Object>> nodes = ObjectUtils.cast(result.get("nodes"));
        Assertions.assertEquals(0, nodes.size());
    }

    @Test
    @DisplayName("当节点runnable为null时，jadeFlow中会有shape节点")
    public void whenNodeRunnableIsNullThenJadeFlowNodeSizeIs1() {
        // prepare.
        JSONObject data = new JSONObject();

        JSONObject shape = new JSONObject();
        shape.put("id", "shapeId1");
        shape.put("type", "start");
        shape.put("text", "开始节点");
        shape.put("runnable", null);

        JSONArray shapes = new JSONArray();
        shapes.add(shape);

        JSONObject page = new JSONObject();
        page.put("shapes", shapes);

        JSONArray pages = new JSONArray();
        pages.add(page);

        data.put("pages", pages);

        // when.
        Map<String, Object> result = new FlowAttributes(data, "0.1.1").getData();

        // then.
        List<Map<String, Object>> nodes = ObjectUtils.cast(result.get("nodes"));
        Assertions.assertEquals(1, nodes.size());
    }
}
