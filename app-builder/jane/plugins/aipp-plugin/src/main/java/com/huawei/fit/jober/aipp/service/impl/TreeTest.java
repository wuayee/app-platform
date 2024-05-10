/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.common.JsonUtils;

import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.service.FlowData;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Todo
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-25
 */
public class TreeTest {
    // public static void main(String[] args) throws IOException {
    //     String jsonString = "{\"title\":\"root\",\"key\":\"1\",\"children\":[{\"title\":\"产品线\",\"key\":\"11\",\"parent\":\"1\",\"children\":[{\"title\":\"数存\",\"key\":\"111\",\"parent\":\"11\",\"children\":[{\"title\":\"数字化工具\",\"key\":\"1111\",\"parent\":\"111\"}]}]},{\"title\":\"BG\",\"key\":\"12\",\"parent\":\"1\",\"children\":[{\"title\":\"运营商\",\"key\":\"121\",\"parent\":\"12\",\"children\":[{\"title\":\"数字化工具1\",\"key\":\"1211\",\"parent\":\"121\"}]}]},{\"title\":\"区域\",\"key\":\"13\",\"parent\":\"1\",\"children\":[{\"title\":\"中国区\",\"key\":\"131\",\"parent\":\"13\",\"children\":[{\"title\":\"数字化工具2\",\"key\":\"1311\",\"parent\":\"131\"}]}]}]}";
    //
    //     ObjectMapper mapper = new ObjectMapper();
    //     JsonNode rootNode = mapper.readTree(jsonString);
    //     removeInnerChildren(rootNode);
    //     String res = mapper.writeValueAsString(rootNode);
    //
    //
    //     System.out.println(res);
    // }
    //
    // private static void removeInnerChildren(JsonNode node) {
    //     if (node == null || node.isNull()) {
    //         return;
    //     }
    //     if (node.has("children")) {
    //         JsonNode childrenNode = node.get("children");
    //         for (int i = 0 ; i< childrenNode.size(); i++) {
    //             JsonNode greatChildNode = childrenNode.get(i).get("children");
    //             if (greatChildNode == null) {
    //                 ((ObjectNode) node).remove("children");
    //
    //             }
    //             removeInnerChildren(childrenNode.get(i));
    //         }
    //     }
    // }

    // public static void main(String[] args) throws IOException {
    //     List<Map<String, Object>> res = new ArrayList<>();
    //     // String jsonString = "{\"test\": 1,\"form_args\":{\"abstract\":\"以下是一些可能的面试问题：\",\"qaList\":[{\"question\":\"介绍一下你的实习经历。\",\"answer\":\"有在嘉立顿公司实习六个月。\"},{\"question\":\"实习过程中有什么收获呢？\",\"answer\":\"通关超过十个游戏。\"}]}}";
    //     // String jsonString = "{\"msg\":null,\"form_id\":\"123\",\"form_args\":null,\"form_version\":\"1.0.0\"}";
    //     String jsonString = "{\"msg\":null,\"form_id\":\"123\",\"form_args\":\"{\\\"interviewResult\\\":[{\\\"question\\\":\\\"介绍一下你的实习经历。\\\",\\\"answer\\\":\\\"有在嘉立顿公司实习六个月。\\\"},{\\\"question\\\":\\\"实习过程中有什么收获呢？\\\",\\\"answer\\\":\\\"通关超过十个游戏。\\\"}]}\",\"form_version\":\"1.0.0\"}";
    //     // String jsonString = "{\"msg\":null,\"form_id\":\"123\",\"form_args\":\"{\\\"interviewResult1\\\":123}\",\"form_version\":\"1.0.0\"}";
    //
    //
    //     Map<String, String> data = JsonUtils.parseObject(jsonString, Map.class);
    //     String form_args = data.get("form_args");
    //     if (StringUtils.isEmpty(form_args)) {
    //         return;
    //     }
    //     Map<String, Object> formArgs = JsonUtils.parseObject(form_args, Map.class);
    //     if (!formArgs.containsKey("interviewResult")) {
    //         return;
    //     }
    //     if (formArgs.get("interviewResult") instanceof List) {
    //         res.addAll((List<Map<String, Object>>) formArgs.get("interviewResult"));
    //     }
    //
    //     System.out.println(res);
    //
    //     //
    //     // // 创建 ObjectMapper 实例
    //     // Map<String, String> data = JsonUtils.parseObject(jsonString, Map.class);
    //     //
    //     // String form_args = data.get("form_args");
    //     // if (StringUtils.isEmpty(form_args)) {
    //     //     return;
    //     // }
    //     // System.out.println(form_args);
    //     // Map<String, Object> test = JsonUtils.parseObject(form_args, Map.class);
    //     // if (!test.containsKey("interviewResult")) {
    //     //     return;
    //     // }
    //     // // List<Map<String, Object>> l = (List<Map<String, Object>>) test.get("interviewResult");
    //     // if (test.get("interviewResult") instanceof List) {
    //     //     System.out.println("111");
    //     // }
    //     // // List re = JsonUtils.parseArray(test.get("interviewResult"), List[].class);
    //     // // Map<String, List<Map<String, Object>>> interviewResultJson = JsonUtils.parseObject(form_args, Map.class);
    //     // // List<Map<String, Object>> qaList = interviewResultJson.get("interviewResult");
    //     // // res.addAll(qaList);
    //     // System.out.println("123");
    //
    // }

    public static void main(String[] args) throws IOException {
        // String json = "{\"triggerMode\":\"auto\",\"jober\":{\"type\":\"general_jober\",\"name\":\"\",\"fitables\":[\"com.huawei.fit.jober.aipp.fitable.LLMComponent\"],\"converter\":{\"type\":\"mapping_converter\",\"entity\":{\"inputParams\":[{\"id\":\"ffe17733-08be-4297-b429-b07fa4cb0696\",\"name\":\"model\",\"type\":\"String\",\"from\":\"Input\",\"value\":\"Qwen-72B\"},{\"id\":\"3cffb1f4-20c3-487a-aa12-0c8ccaeba786\",\"name\":\"temperature\",\"type\":\"Number\",\"from\":\"Input\",\"value\":\"0.3\"},{\"id\":\"6c7489e9-7410-4e47-bd0f-ed6b1575460b\",\"name\":\"prompt\",\"type\":\"Object\",\"from\":\"Expand\",\"value\":[{\"id\":\"63602287-b10e-418e-8ead-0f576da094bc\",\"name\":\"template\",\"type\":\"String\",\"from\":\"Input\",\"value\":\"用户的问题是{{query}}，相关的资料是{{knowledge}}\"},{\"id\":\"d0f61c0b-cbdd-4e76-a43e-45dd74123ffa\",\"name\":\"variables\",\"type\":\"Object\",\"from\":\"Expand\",\"value\":[{\"id\":\"b68cba72-7de3-43e9-a5a0-992e144c1ff6\",\"name\":\"knowledge\",\"type\":\"String\",\"from\":\"Reference\",\"value\":[\"output\",\"retrievalOutput\"],\"referenceNode\":\"sciinj\",\"referenceId\":\"783f2714-d81a-44e2-8d32-2fefba3dcf1c\",\"referenceKey\":\"retrievalOutput\"},{\"id\":\"ea05cd7c-6801-4127-b88d-0c9bd7e76dda\",\"name\":\"query\",\"type\":\"String\",\"from\":\"Reference\",\"value\":[\"Question\"],\"referenceKey\":\"Question\",\"referenceNode\":\"b0dl77\",\"referenceId\":\"input_26555fd5-ec34-4a91-bd77-469c16f65dbc\"}]}]},{\"id\":\"90d51691-7858-46da-a3d2-b528f6b89059\",\"name\":\"tools\",\"type\":\"Array\",\"from\":\"Input\",\"value\":[]},{\"id\":\"cdcd3ec9-a4dc-4b22-9cd4-dcd40bf60917\",\"name\":\"workflows\",\"type\":\"Array\",\"from\":\"Input\",\"value\":[]},{\"id\":\"90d69169-7585-46ab-a3d2-b528f6b89333\",\"name\":\"systemPrompt\",\"type\":\"String\",\"from\":\"Input\",\"value\":\"\"}],\"outputParams\":[{\"id\":\"fface40b-e0d6-4d4d-a0e8-35c77af3df7e\",\"name\":\"output\",\"type\":\"Object\",\"from\":\"Expand\",\"value\":[{\"id\":\"51020cba-2d8e-4962-b39e-8f542856a8d7\",\"name\":\"llmOutput\",\"type\":\"string\",\"from\":\"Input\",\"description\":\"\",\"value\":\"\"}]}]}},\"isAsync\":\"true\"}}";
        String json="{\"jober\":{\"fitables\":[\"com.huawei.fit.jober.aipp.fitable.NaiveRAGComponent\"],\"converter\":{\"type\":\"mapping_converter\",\"entity\":{\"outputParams\":[{\"name\":\"output\",\"from\":\"Expand\",\"id\":\"output_b1f2b3c2-5076-4efc-8ba4-68d9b179960d\",\"type\":\"Object\",\"value\":[{\"name\":\"retrievalOutput\",\"from\":\"Input\",\"id\":\"783f2714-d81a-44e2-8d32-2fefba3dcf1c\",\"type\":\"String\",\"value\":\"String\"}]}],\"inputParams\":[{\"name\":\"input\",\"from\":\"Expand\",\"id\":\"input_f9c0b32c-8ecf-4b29-8dd1-71992a2cd124\",\"type\":\"Object\",\"value\":[{\"referenceNode\":\"b0dl77\",\"name\":\"query\",\"from\":\"Reference\",\"id\":\"query_b509ba64-90d9-4c9e-9d23-64a4e7fe7167\",\"type\":\"String\",\"value\":[\"Question\"],\"referenceId\":\"input_26555fd5-ec34-4a91-bd77-469c16f65dbc\",\"referenceKey\":\"Question\"}]},{\"name\":\"knowledge\",\"from\":\"Expand\",\"id\":\"knowledge_0b51bdaf-e486-4a70-ab4b-594efdad3a94\",\"type\":\"Array\",\"value\":[]},{\"name\":\"maximum\",\"from\":\"Input\",\"id\":\"maximumRecalls_2aa47ff9-f41e-48b0-bb63-f6e453562e68\",\"type\":\"Integer\",\"value\":3}]}},\"name\":\"\",\"type\":\"general_jober\"},\"triggerMode\":\"auto\"}";
        Map<String, String> config = new HashMap<>();
        config.put("temperature", "0.5");
        config.put("model", "\"1111111111111\"");
        // config.put("tools", "[\"tool1\", \"tool2\"]");
        config.put("knowledge",
                "[{\"id\":92,\"name\":\"testsess\", \"description\":\"111\"},{\"id\":91,\"name\":\"testseg\", \"description\":\"22\"}]");
        // String[] res = JsonUtils.parseObject("[\"tool1\", \"tool2\"]", String[].class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode xx =  mapper.readTree(json);
            JsonNode params = xx.findPath("inputParams");
            for (int i= 0; i<params.size(); i++) {
                JsonNode child = params.get(i);
                processParam(child, config);


            }
            Object tt = JSON.parse(xx.toString());
            JSONObject kk = new JSONObject();
            kk.put("meta", tt);
            System.out.println(JSONObject.toJSONString(kk));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void processParam(JsonNode node, Map<String, String> params) {
        List<String> singleLayerParams = new ArrayList<>(Arrays.asList("model", "temperature", "systemPrompt"));
        List<String> doubleLayerParams = new ArrayList<>(Arrays.asList("tools", "workflows"));
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (StringUtils.equals(node.get("name").asText(), param.getKey())) {
                if (singleLayerParams.contains(param.getKey())) {
                    // JsonNode valueNode = new TextNode("123");
                    if (param.getKey() == "model") {
                        ((ObjectNode) node).put("value", JsonUtils.parseObject(param.getValue(), String.class));

                    } else {
                        ((ObjectNode) node).put("value", JsonUtils.parseObject(param.getValue(), Float.class));

                    }
                    // System.out.println(node.toString());
                    continue;
                }
                if (doubleLayerParams.contains(param.getKey())) {
                    ArrayNode valueArrayNode = convert2ArrayNode(param.getValue());
                    ((ObjectNode) node).set("value", valueArrayNode);
                    continue;
                }
                if (StringUtils.equals("knowledge", param.getKey())) {
                    JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
                    ArrayNode valueArrayNode = nodeFactory.arrayNode();
                    List<Map<String, Object>> res = (List<Map<String,Object>>) JsonUtils.parseObject(param.getValue(), List.class);
                    res.forEach(r ->{
                        ArrayNode valueArrayNode1 = nodeFactory.arrayNode();
                        for (Map.Entry<String, Object> rr : r.entrySet()) {
                            if (rr.getKey() == "id") {
                                valueArrayNode1.add(convert2Id(rr.getKey(), ((Integer) rr.getValue()).longValue()));

                            } else {
                                valueArrayNode1.add(convert1ValueObject(rr.getKey(), String.valueOf(rr.getValue())));
                            }
                            // valueArrayNode1.add(convert1ValueObject(rr.getKey(), rr.getValue()));
                        }
                        Map<String, Object> a = new HashMap<>();
                        a.put("id", UUID.randomUUID().toString());
                        a.put("type", "Object");
                        a.put("from", "Expand");
                        a.put("value", valueArrayNode1);
                        ObjectNode mapNode = nodeFactory.objectNode();
                        for (Map.Entry<String, Object> entry : a.entrySet()) {
                            if (entry.getKey() == "value") {
                                mapNode.put(entry.getKey(), (JsonNode) entry.getValue());
                            } else {
                                mapNode.put(entry.getKey(), (String) entry.getValue());
                            }
                        }
                        valueArrayNode.add(mapNode);
                    });
                    ((ObjectNode) node).set("value", valueArrayNode);
                }
            }
        }
    }


    // public static ArrayNode convert1(List<Map<String, String>> res) {
    //     JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
    //     List<Map<String, String>> re = res.stream().map(r -> convert1ValueObject(r)).collect(Collectors.toList());
    //     ArrayNode valueArrayNode = nodeFactory.arrayNode();
    //     for (Map<String, String> rr : re) {
    //         ObjectNode mapNode = nodeFactory.objectNode();
    //         for (Map.Entry<String, String> entry : rr.entrySet()) {
    //             mapNode.put(entry.getKey(), entry.getValue());
    //         }
    //         valueArrayNode.add(mapNode);
    //     }
    //     return valueArrayNode;
    // }


    public static ArrayNode convert2ArrayNode(String value) {
        String[] res = JsonUtils.parseObject(value, String[].class);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        List<Map<String, String>> re = Arrays.stream(res).map(r -> convert2ValueObject(r)).collect(Collectors.toList());

        ArrayNode valueArrayNode = nodeFactory.arrayNode();
        for (Map<String, String> rr : re) {
            ObjectNode mapNode = nodeFactory.objectNode();
            for (Map.Entry<String, String> entry : rr.entrySet()) {
                mapNode.put(entry.getKey(), entry.getValue());
            }
            valueArrayNode.add(mapNode);
        }
        return valueArrayNode;
    }


    public static Map<String, String> convert2ValueObject(String value) {
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        return map;
    }

    public static ObjectNode convert1ValueObject(String key, String value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapNode.put(entry.getKey(), entry.getValue());
        }
        return mapNode;
    }


    public static ObjectNode convert2Id(String key, Long value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey() == "value") {
                mapNode.put(entry.getKey(), (Long) entry.getValue());
            } else {
                mapNode.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return mapNode;
    }
}
