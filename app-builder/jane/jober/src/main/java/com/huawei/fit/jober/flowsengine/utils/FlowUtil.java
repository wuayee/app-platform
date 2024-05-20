/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.utils;

import static com.huawei.fit.jober.common.Constant.BUSINESS_DATA_IGNORED_KEYS;
import static com.huawei.fit.jober.common.Constant.BUSINESS_DATA_INTERNAL_KEY;
import static com.huawei.fit.jober.common.Constant.BUSINESS_DATA_KEY;
import static com.huawei.fit.jober.common.Constant.INTERNAL_OUTPUT_SCOPE_KEY;
import static com.huawei.fit.jober.common.Constant.PASS_DATA;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.flowsengine.biz.service.FlowContextsService;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 流程定义中变量处理工具类
 *
 * @author g00564732
 * @since 2023/12/01
 */
public final class FlowUtil {
    private static final Logger log = Logger.get(FlowContextsService.class);

    private static final Pattern LEGACY_RULE_KEY_PATTERN = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");

    private static final Pattern LEGACY_RULE_KEY_PATTERN_WITH_QUOTES = Pattern.compile("'\\{\\{([^\\}]+)\\}\\}'");

    /**
     * 获取流程引擎变量原始值
     * 输入{{var}}，返回var
     *
     * @param variable 流程引擎变量
     * @return 变量原始值列表
     */
    public static List<String> originalVariable(String variable) {
        if (StringUtils.isEmpty(variable)) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        Matcher matcher = LEGACY_RULE_KEY_PATTERN.matcher(variable);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    /**
     * 替换流程引擎变量的值
     * 输入{{var}}，返回var的真实值，从valueMap中获取
     *
     * @param variable 流程引擎变量
     * @param valueMap 流程引擎变量的值
     * @return 替换后的值
     */
    public static Object replace(String variable, Map<String, Object> valueMap) {
        log.info("[FlowUtil] replace variables for variable: [{}], value map: [{}]", variable, valueMap);
        if (StringUtils.isEmpty(variable)) {
            return variable;
        }
        String formattedVariable = variable;
        Matcher matcher = LEGACY_RULE_KEY_PATTERN.matcher(formattedVariable);
        while (matcher.find()) {
            String formatVariable = matcher.group(0);
            String originalVariable = matcher.group(1);
            String value = ObjectUtils.cast(valueMap.getOrDefault(originalVariable, ""));
            formattedVariable = formattedVariable.replace(formatVariable, value);
        }
        return formattedVariable;
    }

    /**
     * 格式化条件规则： 输入{{var}},返回 businessData.var; 输入'{{var}}' == '1', 返回businessData.var == "1"
     *
     * @param conditionRule 条件规则
     * @return 格式化后的条件规则
     */
    public static String formatConditionRule(String conditionRule) {
        log.info("[FlowUtil] format condition rule: [{}]", conditionRule);
        if (StringUtils.isEmpty(conditionRule)) {
            return conditionRule;
        }
        String formattedConditionRule = conditionRule;
        Matcher matcherWithQuotes = LEGACY_RULE_KEY_PATTERN_WITH_QUOTES.matcher(formattedConditionRule);
        while (matcherWithQuotes.find()) {
            formattedConditionRule = replaceFormattedVariable(formattedConditionRule, matcherWithQuotes);
        }
        Matcher matcher = LEGACY_RULE_KEY_PATTERN.matcher(formattedConditionRule);
        while (matcher.find()) {
            formattedConditionRule = replaceFormattedVariable(formattedConditionRule, matcher);
        }
        formattedConditionRule = formattedConditionRule.replace("'", "\"");
        return formattedConditionRule;
    }

    private static String replaceFormattedVariable(String conditionRule, Matcher matcher) {
        String formatVariable = matcher.group(0);
        String originalVariable = matcher.group(1);
        if (!originalVariable.startsWith(BUSINESS_DATA_KEY) || !originalVariable.startsWith(PASS_DATA)) {
            originalVariable = BUSINESS_DATA_KEY + "." + originalVariable;
        }
        return conditionRule.replace(formatVariable, originalVariable);
    }

    /**
     * 合并两个map
     * 通key值如果不是map，则直接覆盖，如果是map则递归合并
     *
     * @param input 原始map
     * @param target 需要被合并的map
     * @return 合并后的map
     */
    public static Map<String, Object> mergeMaps(Map<String, Object> input, Map<String, Object> target) {
        log.info("[FlowUtil] mergeMaps for input: [{}], target: [{}]", input, target);
        Map<String, Object> mergedMap = new HashMap<>(input);
        target.forEach((targetKey, targetValue) -> {
            if (!mergedMap.containsKey(targetKey)) {
                mergedMap.put(targetKey, targetValue);
                return;
            }
            Object mergingValue = mergedMap.get(targetKey);
            if (!(mergingValue instanceof Map) || !(targetValue instanceof Map)) {
                mergedMap.put(targetKey, targetValue);
                return;
            }
            mergedMap.put(targetKey, mergeMaps(ObjectUtils.cast(mergingValue), ObjectUtils.cast(targetValue)));
        });
        return mergedMap;
    }

    /**
     * 将结果放到数据对应的节点下
     *
     * @param businessData 任务数据
     * @param nodeMetaId 节点id
     */
    public static void cacheResultToNode(Map<String, Object> businessData, String nodeMetaId) {
        Map<String, Object> internalInfo = cast(getOrCreateMapChild(businessData, BUSINESS_DATA_INTERNAL_KEY));
        Map<String, Object> outputScope = cast(getOrCreateMapChild(internalInfo, INTERNAL_OUTPUT_SCOPE_KEY));
        outputScope.put(nodeMetaId, extractBusinessDataOutput(businessData));
    }

    private static Map<String, Object> extractBusinessDataOutput(Map<String, Object> businessData) {
        Map<String, Object> result = new HashMap<>();
        businessData.entrySet()
                .stream()
                .filter(item -> !BUSINESS_DATA_IGNORED_KEYS.contains(item.getKey()))
                .forEach(item -> result.put(item.getKey(), item.getValue()));
        return result;
    }

    private static Object getOrCreateMapChild(Map<String, Object> target, String key) {
        Object result = target.get(key);
        if (Objects.isNull(result)) {
            result = new HashMap<>();
            target.put(key, result);
        }
        return result;
    }
}
