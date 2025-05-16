/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.utils;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.common.Constant;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 流程定义中变量处理工具类
 *
 * @author 高诗意
 * @since 2023/12/01
 */
public final class FlowUtil {
    private static final Logger log = Logger.get(FlowUtil.class);

    private static final Pattern LEGACY_RULE_NOT_PATTERN = Pattern.compile("!\\{\\{[^!{}]*\\}\\}");


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
     * 判断条件规则是否为非模式
     * 输入"!{{var}}"，返回true
     *
     * @param conditionRule 条件规则
     * @return 是否为非模式
     */
    public static boolean isPatternOfNot(String conditionRule) {
        return LEGACY_RULE_NOT_PATTERN.matcher(conditionRule).find();
    }

    /**
     * 格式化条件规则： 输入{{var}},返回 businessData.get("var"); 输入'{{var}}' == '1', 返回businessData.get("var") == "1"
     *
     * @param conditionRule 条件规则
     * @return 格式化后的条件规则
     */
    public static String formatConditionRule(String conditionRule) {
        if (StringUtils.isEmpty(conditionRule)) {
            return conditionRule;
        }
        String formattedConditionRule = conditionRule;
        for (Pattern pattern : Arrays.asList(LEGACY_RULE_KEY_PATTERN_WITH_QUOTES, LEGACY_RULE_KEY_PATTERN)) {
            formattedConditionRule = tryConvertOldVar(formattedConditionRule, pattern);
        }
        return formattedConditionRule.replace("'", "\"");
    }

    /**
     * 输入{{var}},返回 businessData.get("var"); 输入'{{var}}' == '1', 返回businessData.get("var") == "1"
     *
     * @param condition 输入的条件
     * @param pattern 匹配规则
     * @return 替换后的条件
     */
    private static String tryConvertOldVar(String condition, Pattern pattern) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = pattern.matcher(condition);
        while (matcher.find()) {
            String variable = matcher.group(1);  // 提取 {{ }} 中的内容
            String[] parts = variable.split("\\.");
            // 构造 businessData.get("var").get("a") 形式的字符串
            StringBuilder newVar = new StringBuilder();
            if (!Constant.BUSINESS_DATA_KEY.equals(parts[0])) {
                newVar.append(Constant.BUSINESS_DATA_KEY);
            }
            Arrays.stream(parts).forEach(path -> newVar.append(".get(\"").append(path).append("\")"));
            matcher.appendReplacement(result, newVar.toString());
        }
        matcher.appendTail(result);
        return result.toString();
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
        Map<String, Object> internalInfo = cast(getOrCreateMapChild(businessData, Constant.BUSINESS_DATA_INTERNAL_KEY));
        Map<String, Object> outputScope = cast(getOrCreateMapChild(internalInfo, Constant.INTERNAL_OUTPUT_SCOPE_KEY));
        outputScope.put(nodeMetaId, extractBusinessDataOutput(businessData));
    }

    /**
     * 将结果放到数据对应的节点下
     *
     * @param result 待添加的结果
     * @param businessData 任务数据
     * @param nodeMetaId 节点id
     */
    public static void cacheResultToNode(Map<String, Object> result, Map<String, Object> businessData,
            String nodeMetaId) {
        Map<String, Object> internalInfo = cast(getOrCreateMapChild(businessData, Constant.BUSINESS_DATA_INTERNAL_KEY));
        Map<String, Object> outputScope = cast(getOrCreateMapChild(internalInfo, Constant.INTERNAL_OUTPUT_SCOPE_KEY));
        outputScope.put(nodeMetaId, result);
    }

    private static Map<String, Object> extractBusinessDataOutput(Map<String, Object> businessData) {
        Map<String, Object> result = new HashMap<>();
        businessData.entrySet()
                .stream()
                .filter(item -> !Constant.BUSINESS_DATA_IGNORED_KEYS.contains(item.getKey()))
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