/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.controller;

import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.jober.entity.FlowInfo;
import com.huawei.fit.waterflow.biz.common.entity.CleanTaskPageResult;
import com.huawei.fitframework.model.RangeResult;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为视图提供工具方法。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
public final class Views {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Views() {
    }

    /**
     * viewOf
     *
     * @param cleanTaskPageResult 分页清洗结果
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOf(CleanTaskPageResult cleanTaskPageResult) {
        Map<String, Object> view = new LinkedHashMap<>(2);
        view.put("totalPage", cleanTaskPageResult.getTotalNum());
        view.put("result", cleanTaskPageResult.getResult());
        return view;
    }

    /**
     * viewOfFlowInfo
     *
     * @param flowInfo flowInfo
     * @return Map<String, Object>
     */
    public static Map<String, Object> viewOfFlowInfo(FlowInfo flowInfo) {
        if (flowInfo == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>();
        com.alibaba.fastjson.JSONObject parsedData = com.alibaba.fastjson.JSONObject.parseObject(
                flowInfo.getConfigData());
        String name = parsedData.getString("title");
        put(view, "flowId", flowInfo.getFlowId());
        put(view, "version", flowInfo.getVersion());
        put(view, "graphData", flowInfo.getConfigData());
        put(view, "name", name);
        return view;
    }

    /**
     * 根据flowGraphDefinition获取一个视图
     *
     * @param flowGraphDefinition flowGraphDefinition
     * @return 视图
     */
    public static Map<String, Object> viewOfFlowGraphDefinition(FlowGraphDefinition flowGraphDefinition) {
        if (flowGraphDefinition == null) {
            return new LinkedHashMap<>();
        }
        return processFlowGraph(flowGraphDefinition, "isDeleted");
    }

    /**
     * 获取一个视图，表示分页查询的结果集。
     *
     * @param results 表示分页查询结果集的 {@link RangedResultSet}。
     * @param key 表示结果集的键的 {@link String}。
     * @param mapper 表示用以映射结果元素的方法的 {@link Function}。
     * @param <T> 结果集中元素的类型。
     * @return 表示结果集的视图的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static <T> Map<String, Object> viewOf(RangedResultSet<T> results, String key,
            Function<T, Map<String, Object>> mapper) {
        if (results == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(2);
        view.put(key, results.getResults().stream().map(mapper).collect(Collectors.toList()));
        view.put("pagination", viewOf(results.getRange()));
        return view;
    }

    private static void put(Map<String, Object> view, String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            view.put(key, value);
        }
    }

    private static Map<String, Object> processFlowGraph(FlowGraphDefinition flowGraphDefinition, String... except) {
        if (flowGraphDefinition == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(12);
        JSONObject from = convertToJson(flowGraphDefinition);
        from.keySet()
                .stream()
                .filter(key -> !flowGraphFilter(key, except))
                .forEach(key -> view.put(key, from.get(key)));
        return view;
    }

    private static Map<String, Object> viewOf(RangeResult result) {
        if (result == null) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> view = new LinkedHashMap<>(3);
        view.put("offset", result.getOffset());
        view.put("limit", result.getLimit());
        view.put("total", result.getTotal());
        return view;
    }

    private static JSONObject convertToJson(FlowGraphDefinition flowGraphDefinition) {
        return (JSONObject) JSONObject.toJSON(flowGraphDefinition);
    }

    private static boolean flowGraphFilter(String key, String[] except) {
        if (except == null || except.length == 0) {
            return false;
        }
        Optional<String> first = Arrays.stream(except).filter(key::equals).findFirst();
        return first.isPresent();
    }
}
