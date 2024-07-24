/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers;

import static com.huawei.fit.waterflow.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.EVENT;
import static java.util.Locale.ROOT;

import com.huawei.fit.waterflow.common.exceptions.WaterflowException;
import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.common.Constant;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 流程JSONObject操作封装类
 * 封装所有JSONObject操作，不对外暴露
 *
 * @author y00679285
 * @since 1.0
 */
public class FlowGraphData {
    /**
     * 流程定义json中名称的key
     */
    public static final String NAME = "name";

    /**
     * 流程定义json中元数据ID的key
     */
    public static final String META_ID = "metaId";

    /**
     * 流程定义json中类型的key
     */
    public static final String TYPE = "type";

    /**
     * 流程定义json中节点的key
     */
    public static final String NODES = "nodes";

    /**
     * 流程定义json中自动任务的key
     */
    public static final String JOBER = "jober";

    /**
     * 流程定义json中自动任务中的fitables的key
     */
    public static final String FITABLES = "fitables";

    /**
     * 流程定义json中自动任务中的properties的key
     */
    public static final String PROPERTIES = "properties";

    /**
     * 流程定义json中自动任务过滤器的key
     */
    public static final String JOBER_FILTER = "joberFilter";

    /**
     * 流程定义json中人工任务的key
     */
    public static final String TASK = "task";

    /**
     * 流程定义json中人工任务的id标识
     */
    public static final String TASK_ID = "taskId";

    /**
     * 流程定义json中人工任务过滤器的key
     */
    public static final String TASK_FILTER = "taskFilter";

    /**
     * 流程定义json中回调函数的key
     */
    public static final String CALLBACK = "callback";

    /**
     * 流程定义json中回调函数的filteredKeys的key
     */
    public static final String FILTERED_KEYS = "filteredKeys";

    /**
     * 流程定义json中版本的key
     */
    public static final String VERSION = "version";

    /**
     * 流程定义json中流程的状态
     */
    public static final String STATUS = "status";

    /**
     * 流程定义json中描述信息的key
     */
    public static final String DESCRIPTION = "description";

    /**
     * 流程定义json中异常处理fitables的key
     */
    public static final String EXCEPTION_FITABLES = "exceptionFitables";

    /**
     * 流程定义json中触发模式的key
     */
    public static final String TRIGGER_MODE = "triggerMode";

    /**
     * 流程定义json中来源节点的key
     */
    public static final String FROM = "from";

    /**
     * 流程定义json中下一个节点的key
     */
    public static final String TO = "to";

    private final JSONObject definitions;

    private final JSONArray nodes;

    private final JSONArray events;

    /**
     * FlowGraphData
     *
     * @param flowGraph flowGraph
     */
    public FlowGraphData(String flowGraph) {
        definitions = JSONObject.parseObject(flowGraph);
        nodes = new JSONArray();
        events = new JSONArray();
        getAllNodes().stream()
                .filter(node -> !ObjectUtils.<JSONObject>cast(node)
                        .getString(TYPE)
                        .toUpperCase(ROOT)
                        .endsWith(EVENT.getCode()))
                .forEach(nodes::add);
        getAllNodes().stream()
                .filter(node -> ObjectUtils.<JSONObject>cast(node)
                        .getString(TYPE)
                        .toUpperCase(ROOT)
                        .endsWith(EVENT.getCode()))
                .forEach(events::add);
    }

    /**
     * 获取流程定义的名称
     *
     * @return 流程定义的名称
     */
    public String getFlowName() {
        return definitions.getString(NAME);
    }

    /**
     * 获取流程定义的元数据ID
     *
     * @return 流程定义的元数据ID
     */
    public String getFlowMetaId() {
        return definitions.getString(META_ID);
    }

    /**
     * 获取流程定义的版本
     *
     * @return 流程定义的版本
     */
    public String getFlowVersion() {
        return definitions.getString(VERSION);
    }

    /**
     * 获取流程定义的状态
     *
     * @return 流程状态
     */
    public String getFlowStatus() {
        return definitions.getString(STATUS);
    }

    /**
     * 获取流程定义的描述
     *
     * @return 流程定义的描述
     */
    public String getFlowDescription() {
        return definitions.getString(DESCRIPTION);
    }

    /**
     * 获取流程定义的异常处理fitables合集
     *
     * @return 流程定义的异常处理fitables合集
     */
    public Set<String> getFlowExceptionFitables() {
        return getStringSet(definitions.getJSONArray(EXCEPTION_FITABLES));
    }

    /**
     * 获取流程定义的节点数量
     *
     * @return 流程定义的节点数量
     */
    public int getNodes() {
        return nodes.size();
    }

    /**
     * 获取流程定义的节点名称
     *
     * @param index 节点索引
     * @return 流程定义的节点名称
     */
    public String getNodeName(int index) {
        return getNode(index).getString(NAME);
    }

    /**
     * 获取流程定义的节点元数据ID
     *
     * @param index 节点索引
     * @return 流程定义的节点元数据ID
     */
    public String getNodeMetaId(int index) {
        return getNode(index).getString(META_ID);
    }

    /**
     * 获取流程定义的节点类型
     *
     * @param index 节点索引
     * @return 流程定义的节点类型
     */
    public String getNodeType(int index) {
        return getNode(index).getString(TYPE).toUpperCase(ROOT);
    }

    /**
     * 获取流程定义的节点触发方式
     *
     * @param index 节点索引
     * @return 流程定义的节点触发方式
     */
    public String getNodeTriggerMode(int index) {
        return getNode(index).getString(TRIGGER_MODE).toUpperCase(ROOT);
    }

    /**
     * 获取流程定义的节点自动任务
     *
     * @param index 节点索引
     * @return 流程定义的节点任务
     */
    public JSONObject getNodeJober(int index) {
        return getNode(index).getJSONObject(JOBER);
    }

    /**
     * 获取流程定义的节点任务名称
     *
     * @param nodeIndex 节点索引
     * @return 流程定义的节点任务名称
     */
    public String getNodeJoberName(int nodeIndex) {
        return getNodeJober(nodeIndex).getString(NAME);
    }

    /**
     * 获取流程定义的节点自动任务类型
     *
     * @param index 节点索引
     * @return 流程定义的节点任务
     */
    public String getNodeJoberType(int index) {
        Validation.notNull(getNodeJober(index).getString(TYPE),
                () -> new WaterflowException(INPUT_PARAM_IS_INVALID, "Node jober type"));
        return getNodeJober(index).getString(TYPE).toUpperCase(ROOT);
    }

    /**
     * 获取流程定义的节点自动任务fitables
     *
     * @param index 节点索引
     * @return 流程定义的节点任务
     */
    public Set<String> getNodeJoberFitables(int index) {
        return getStringSet(getNodeJober(index).getJSONArray(FITABLES));
    }

    /**
     * 获取流程定义节点手动任务属性列表
     *
     * @param index 节点索引
     * @return 手动任务类型
     */
    public Map<String, String> getNodeJoberProperties(int index) {
        Map<String, String> properties = new HashMap<>();
        getNodeJober(index).entrySet()
                .stream()
                .filter(entry -> !NAME.equals(entry.getKey()))
                .filter(entry -> !TYPE.equals(entry.getKey()))
                .filter(entry -> !FITABLES.equals(entry.getKey()))
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue().toString()));
        return properties;
    }

    /**
     * 获取流程定义的节点自动任务过滤器
     *
     * @param index 节点索引
     * @param filterKey 过滤器对象的KEY
     * @return 流程定义的节点任务
     */
    public JSONObject getNodeFilter(int index, String filterKey) {
        return getNode(index).getJSONObject(filterKey);
    }

    /**
     * 获取流程定义的节点自动任务过滤器类型
     *
     * @param index 节点索引
     * @param filterKey 过滤器对象的KEY
     * @return 流程定义的节点任务
     */
    public String getNodeFilterType(int index, String filterKey) {
        return getNodeFilter(index, filterKey).getString(TYPE).toUpperCase(ROOT);
    }

    /**
     * 获取流程定义的节点自动任务过滤器属性
     *
     * @param index 节点索引
     * @param filterKey 过滤器对象的KEY
     * @return 自动任务过滤器属性
     */
    public Map<String, String> getNodeFilterProperties(int index, String filterKey) {
        Map<String, String> properties = new HashMap<>();
        getNodeFilter(index, filterKey).entrySet()
                .stream()
                .filter(entry -> !TYPE.equals(entry.getKey()))
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue().toString()));
        return properties;
    }

    /**
     * 获取流程定义的节点人工任务
     *
     * @param index 节点索引
     * @return 流程定义的节点任务
     */
    public JSONObject getNodeTask(int index) {
        return getNode(index).getJSONObject(TASK);
    }

    /**
     * 获取流程定义节点人工任务的ID
     *
     * @param index 节点索引
     * @return 手动任务的ID
     */
    public String getNodeTaskId(int index) {
        return getNodeTask(index).getString(TASK_ID);
    }

    /**
     * 获取流程定义节点人工任务类型
     *
     * @param index 节点索引
     * @return 手动任务类型
     */
    public String getNodeTaskType(int index) {
        return getNodeTask(index).getString(TYPE).toUpperCase(ROOT);
    }

    /**
     * 获取流程定义节点手动任务属性列表
     *
     * @param index 节点索引
     * @return 手动任务类型
     */
    public Map<String, String> getNodeTaskProperties(int index) {
        Map<String, String> properties = new HashMap<>();
        getNodeTask(index).entrySet()
                .stream()
                .filter(entry -> !TASK_ID.equals(entry.getKey()))
                .filter(entry -> !TYPE.equals(entry.getKey()))
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue().toString()));
        return properties;
    }

    /**
     * 获取流程定义的节点回调函数
     *
     * @param index 节点索引
     * @return 流程定义的回调函数
     */
    public JSONObject getNodeCallback(int index) {
        return getNode(index).getJSONObject(CALLBACK);
    }

    /**
     * 获取流程定义的节点回调函数名称
     *
     * @param nodeIndex 节点索引
     * @return 流程定义的节点回调函数名称
     */
    public String getNodeCallbackName(int nodeIndex) {
        return getNodeCallback(nodeIndex).getString(NAME);
    }

    /**
     * 获取流程定义节点回调函数类型
     *
     * @param index 节点索引
     * @return 回调函数类型
     */
    public String getNodeCallbackType(int index) {
        return getNodeCallback(index).getString(TYPE).toUpperCase(ROOT);
    }

    /**
     * 获取流程定义的节点回调函数filteredKeys
     *
     * @param index 节点索引
     * @return 流程定义的回调函数filteredKeys
     */
    public Set<String> getNodeCallbackFilteredKeys(int index) {
        return getStringSet(getNodeCallback(index).getJSONArray(FILTERED_KEYS));
    }

    /**
     * 获取流程定义的节点回调函数fitables
     *
     * @param index 节点索引
     * @return 流程定义的回调函数fitables
     */
    public Set<String> getNodeCallbackFitables(int index) {
        return getStringSet(getNodeCallback(index).getJSONArray(FITABLES));
    }

    /**
     * 获取流程定义节点回调函数属性列表
     *
     * @param index 节点索引
     * @return 回调函数属性
     */
    public Map<String, String> getNodeCallbackProperties(int index) {
        Map<String, String> properties = new HashMap<>();
        getNodeCallback(index).entrySet()
                .stream()
                .filter(entry -> !NAME.equals(entry.getKey()))
                .filter(entry -> !TYPE.equals(entry.getKey()))
                .filter(entry -> !FITABLES.equals(entry.getKey()))
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue().toString()));
        return properties;
    }

    /**
     * 获取流程定义的事件数量
     *
     * @return 流程定义的事件数量
     */
    public int getEvents() {
        return events.size();
    }

    /**
     * 获取流程定义的事件元数据ID
     *
     * @param eventIndex 事件索引
     * @return 流程定义的事件元数据ID
     */
    public String getEventMetaId(int eventIndex) {
        return getEvent(eventIndex).getString(META_ID);
    }

    /**
     * 获取流程定义的事件名称
     *
     * @param eventIndex 事件索引
     * @return 流程定义事件名称
     */
    public String getEventName(int eventIndex) {
        return getEvent(eventIndex).getString(NAME);
    }

    /**
     * 获取流程定义的事件来源节点
     *
     * @param eventIndex 事件索引
     * @return 流程定义的事件来源节点
     */
    public String getEventFromNode(int eventIndex) {
        return getEvent(eventIndex).getString(FROM);
    }

    /**
     * 获取流程定义的事件下一节点
     *
     * @param eventIndex 事件索引
     * @return 流程定义的事件下一节点
     */
    public String getEventToNode(int eventIndex) {
        return getEvent(eventIndex).getString(TO);
    }

    /**
     * 获取流程定义的事件条件规则
     *
     * @param eventIndex 事件索引
     * @return 流程定义的事件条件规则
     */
    public String getEventConditionRule(int eventIndex) {
        return getEvent(eventIndex).getString(Constant.CONDITION_RULE_PROPERTY_KEY);
    }

    /**
     * 获取节点属性信息
     *
     * @param index 序号
     * @return 属性信息
     */
    public Map<String, String> getNodeProperties(int index) {
        Map<String, String> properties = new HashMap<>();
        getNode(index).entrySet()
                .stream()
                .filter(entry -> !NAME.equals(entry.getKey()))
                .filter(entry -> !TYPE.equals(entry.getKey()))
                .filter(entry -> !META_ID.equals(entry.getKey()))
                .filter(entry -> !TRIGGER_MODE.equals(entry.getKey()))
                .filter(entry -> !TASK.equals(entry.getKey()))
                .filter(entry -> !TASK_FILTER.equals(entry.getKey()))
                .filter(entry -> !JOBER.equals(entry.getKey()))
                .filter(entry -> !JOBER_FILTER.equals(entry.getKey()))
                .filter(entry -> !CALLBACK.equals(entry.getKey()))
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue().toString()));
        return properties;
    }

    private JSONArray getAllNodes() {
        return Optional.ofNullable(definitions.getJSONArray(NODES))
                .orElseThrow(() -> new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flow nodes"));
    }

    private JSONObject getNode(int index) {
        return this.nodes.getJSONObject(index);
    }

    private JSONObject getEvent(int index) {
        return this.events.getJSONObject(index);
    }

    private Set<String> getStringSet(JSONArray jsonArray) {
        Set<String> variables = new HashSet<>();
        Optional.ofNullable(jsonArray)
                .ifPresent(variablesJSON -> variablesJSON.forEach(variable -> variables.add(variable.toString())));
        return variables;
    }
}
