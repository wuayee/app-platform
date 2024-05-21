/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers;

import static com.huawei.fit.jober.common.Constant.CONDITION_RULE_PROPERTY_KEY;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeType.EVENT;
import static java.util.Locale.ROOT;

import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.util.ConvertConditionToRuleUtils;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 流程JSONObject操作封装类
 * 封装所有JSONObject操作，不对外暴露
 *
 * @author y00679285
 * @since 2023/8/26
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
     * 自动任务中的converter配置
     */
    public static final String CONVERTER = "converter";

    /**
     * properties
     */
    public static final String PROPERTIES = "properties";

    /**
     * fitablesConfig
     */
    public static final String FITABLES_CONFIG = "fitablesConfig";

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

    private static final Set<String> flowInternalKeys = new HashSet<>(
            Arrays.asList(NAME, STATUS, META_ID, VERSION, DESCRIPTION, NODES));

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
                .sorted((event1, event2) -> {
                    String fromConnector1 = Optional.ofNullable(((JSONObject) event1).get("fromConnector"))
                            .orElse("-1")
                            .toString();
                    String fromConnector2 = Optional.ofNullable(((JSONObject) event2).get("fromConnector"))
                            .orElse("-1")
                            .toString();
                    return Integer.compare(extractNumberFromFromConnector(fromConnector1),
                            extractNumberFromFromConnector(fromConnector2));
                })
                .forEach(events::add);
        extraConditionRules2Events(nodes.stream()
                .filter(node -> FlowNodeType.CONDITION == FlowNodeType.getNodeType(ObjectUtils.<JSONObject>cast(node).getString(TYPE).toUpperCase(ROOT)))
                .collect(Collectors.toList()), events);
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
     * @return String
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
     * 获取流程的属性信息
     *
     * @return 属性信息
     */
    public Map<String, Object> getFlowProperties() {
        Map<String, Object> properties = new HashMap<>();
        definitions.entrySet()
                .stream()
                .filter(item -> !flowInternalKeys.contains(item.getKey()))
                .forEach(item -> properties.put(item.getKey(), item.getValue()));
        return properties;
    }

    /** 流程定义中流程节点json数据获取方式 */
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
                () -> new JobberException(INPUT_PARAM_IS_INVALID, "Node jober type"));
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
                .filter(e -> !e.getKey().equals(NAME))
                .filter(e -> !e.getKey().equals(TYPE))
                .filter(e -> !e.getKey().equals(FITABLES))
                .filter(e -> !e.getKey().equals(CONVERTER))
                .forEach(e -> properties.put(e.getKey(), e.getValue().toString()));
        return properties;
    }

    /**
     * 获取Jober的fitable配置
     *
     * @param nodeIndex 节点索引
     * @return fitable配置参数集合
     */
    public Map<String, Object> getNodeJoberFitableConfig(int nodeIndex) {
        List<Object> fitablesConfig = Optional.ofNullable(getNodeJober(nodeIndex).getJSONArray(FITABLES_CONFIG))
                .flatMap(array -> Optional.ofNullable(array.toJavaList(Object.class)))
                .orElse(new ArrayList<>());
        return fitablesConfig.stream()
                .map(ObjectUtils::<JSONObject>cast)
                .collect(Collectors.toMap(config -> config.getString("id"), config -> config));
    }

    /**
     * 获取jober上的converter配置
     *
     * @param index 节点索引
     * @return converter
     */
    public Map<String, Object> getNodeJoberConverter(int index) {
        return getNodeJober(index).getJSONObject(CONVERTER);
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
    public Optional<String> getNodeFilterType(int index, String filterKey) {
        return Optional.ofNullable(getNodeFilter(index, filterKey))
                .flatMap(filterJson -> Optional.ofNullable(filterJson.getString(TYPE)))
                .map(filterType -> filterType.toUpperCase(ROOT));
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
                .filter(entity -> !Objects.equals(entity.getKey(), TYPE))
                .forEach(entity -> properties.put(entity.getKey(), entity.getValue().toString()));
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
    public Optional<String> getNodeTaskType(int index) {
        return Optional.ofNullable(getNodeTask(index))
                .flatMap(taskJson -> Optional.ofNullable(taskJson.getString(TYPE)))
                .map(taskType -> taskType.toUpperCase(ROOT));
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
                .filter(entity -> !Objects.equals(entity.getKey(), TASK_ID))
                .filter(entity -> !Objects.equals(entity.getKey(), TYPE))
                .forEach(entity -> properties.put(entity.getKey(), entity.getValue().toString()));
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
    public Optional<String> getNodeCallbackType(int index) {
        return Optional.ofNullable(getNodeCallback(index))
                .flatMap(callbackJson -> Optional.ofNullable(callbackJson.getString(TYPE)))
                .map(callbackType -> callbackType.toUpperCase(ROOT));
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
                .filter(entity -> !Objects.equals(entity.getKey(), NAME))
                .filter(entity -> !Objects.equals(entity.getKey(), TYPE))
                .filter(entity -> !Objects.equals(entity.getKey(), FITABLES))
                .forEach(entity -> properties.put(entity.getKey(), entity.getValue().toString()));
        return properties;
    }

    /**
     * 获取callback上的converter配置
     *
     * @param index 节点索引
     * @return converter
     */
    public Map<String, Object> getNodeCallbackConverter(int index) {
        return getNodeCallback(index).getJSONObject(CONVERTER);
    }

    /** 流程定义中流程事件json数据获取方式 */
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
        return getEvent(eventIndex).getString(CONDITION_RULE_PROPERTY_KEY);
    }

    /**
     * getNodeProperties
     *
     * @param index index
     * @return Map<String, String>
     */
    public Map<String, Object> getNodeProperties(int index) {
        Map<String, Object> properties = new HashMap<>();
        getNode(index).entrySet()
                .stream()
                .filter(entity -> !Objects.equals(entity.getKey(), NAME))
                .filter(entity -> !Objects.equals(entity.getKey(), TYPE))
                .filter(entity -> !Objects.equals(entity.getKey(), META_ID))
                .filter(entity -> !Objects.equals(entity.getKey(), TRIGGER_MODE))
                .filter(entity -> !Objects.equals(entity.getKey(), TASK))
                .filter(entity -> !Objects.equals(entity.getKey(), TASK_FILTER))
                .filter(entity -> !Objects.equals(entity.getKey(), JOBER))
                .filter(entity -> !Objects.equals(entity.getKey(), JOBER_FILTER))
                .filter(entity -> !Objects.equals(entity.getKey(), CALLBACK))
                .forEach(entity -> properties.put(entity.getKey(), entity.getValue()));
        return properties;
    }

    private JSONArray getAllNodes() {
        return Optional.ofNullable(definitions.getJSONArray(NODES))
                .orElseThrow(() -> new JobberParamException(INPUT_PARAM_IS_INVALID, "flow nodes"));
    }

    private JSONObject getNode(int index) {
        return this.nodes.getJSONObject(index);
    }

    private JSONObject getEvent(int index) {
        return this.events.getJSONObject(index);
    }

    private Set<String> getStringSet(JSONArray jsonArray) {
        Set<String> variables = new LinkedHashSet<>();
        Optional.ofNullable(jsonArray)
                .ifPresent(variablesJSON -> variablesJSON.forEach(variable -> variables.add(variable.toString())));
        return variables;
    }

    private void extraConditionRules2Events(List<Object> conditions, JSONArray events) {
        conditions.forEach(condition -> {
            String conditionMetaId = ((JSONObject) condition).get("metaId").toString();
            List<Object> relatedEventList = events.stream()
                    .filter(event -> conditionMetaId.equals(((JSONObject) event).get("from").toString()))
                    .collect(Collectors.toList());
            if (((JSONObject) condition).getJSONObject("conditionParams") == null) {
                return;
            }
            JSONArray branches = ((JSONObject) condition).getJSONObject("conditionParams")
                    .getJSONArray("branches");
            for (int i = 0; i < branches.size(); i++) {
                ((JSONObject)relatedEventList.get(i)).put("conditionRule", ConvertConditionToRuleUtils.convert(branches.get(i).toString()));
            }
            // Todo 临时逻辑为了联调，后续待确认此做法
            ((JSONObject)relatedEventList.get(branches.size())).put("conditionRule", "true");
        });
    }

    /**
     * 辅助方法，使用正则表达式提取FromConnector字符串中的数字，未找到匹配数字返回-1。
     *
     * @param str 需要提取数字的字符串的 {@link String}。
     * @return 字符串中提取出的数字。
     */
    private int extractNumberFromFromConnector(String str) {
        Pattern pattern = Pattern.compile("dynamic-(\\d+)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1; // 如果未找到匹配的数字，则返回 -1
    }
}
