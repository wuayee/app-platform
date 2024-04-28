/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.utils;

import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 流程定义中变量处理工具类
 *
 * @author g00564732
 * @since 1.0
 */
public final class FlowUtil {
    private static final Logger LOG = Logger.get(FlowUtil.class);

    private static final Pattern PATTERN = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");

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
        Matcher matcher = PATTERN.matcher(variable);
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
        LOG.info("[FlowUtil] replace variables for variable: [{}], value map: [{}]", variable, valueMap);
        if (StringUtils.isEmpty(variable)) {
            return variable;
        }
        String replaced = variable;
        Matcher matcher = PATTERN.matcher(replaced);
        while (matcher.find()) {
            String formatVariable = matcher.group(0);
            String originalVariable = matcher.group(1);
            String value = ObjectUtils.cast(valueMap.getOrDefault(originalVariable, ""));
            replaced = replaced.replace(formatVariable, value);
        }
        return replaced;
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
        LOG.info("[FlowUtil] mergeMaps for input: [{}], target: [{}]", input, target);
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
            mergedMap.put(targetKey, mergeMaps((Map<String, Object>) mergingValue, (Map<String, Object>) targetValue));
        });
        return mergedMap;
    }
}
