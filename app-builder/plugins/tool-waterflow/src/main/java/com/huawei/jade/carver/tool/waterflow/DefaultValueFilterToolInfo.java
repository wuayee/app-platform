/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.waterflow;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.util.MapUtils;
import com.huawei.jade.carver.tool.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 提供一个返回去除默认参数摘要信息的元素信息结构。
 *
 * @author 王攀博
 * @since 2024-04-22
 */
public class DefaultValueFilterToolInfo implements Tool.Info {
    private static final String PARAMETERS_KEY = "parameters";
    private static final String PARAMETERS_PROPERTIES_KEY = "properties";
    private static final String PARAMETERS_REQUIRED_KEY = "required";
    private static final String PARAMETERS_ORDER_KEY = "order";
    private static final String DEFAULT_PARAMETER_KEY = "default";

    private final Tool.Info toolInfo;

    public DefaultValueFilterToolInfo(Tool.Info toolInfo) {
        notNull(toolInfo, "the tool info cannot be null");
        this.toolInfo = toolInfo;
    }

    @Override
    public Map<String, Object> schema() {
        // 过滤掉摘要信息中拥有默认值的参数。
        Map<String, Object> schema = new HashMap<>(this.toolInfo.schema());
        return getFilterSchema(schema);
    }

    static Map<String, Object> getFilterSchema(Map<String, Object> schema) {
        Map<String, Object> parametersSchema = cast(schema.get(PARAMETERS_KEY));
        if (MapUtils.isEmpty(parametersSchema)) {
            return schema;
        }
        Map<String, Object> properties = cast(parametersSchema.get(PARAMETERS_PROPERTIES_KEY));
        if (MapUtils.isEmpty(properties)) {
            return schema;
        }
        filterDefaultParams(properties, parametersSchema);
        parametersSchema.remove(PARAMETERS_ORDER_KEY);
        filterDynamicParams(properties);
        return schema;
    }

    private static void filterDefaultParams(Map<String, Object> properties, Map<String, Object> parametersSchema) {
        List<String> defaultKeyList = new ArrayList<>();
        Iterator<Map.Entry<String, Object>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            Map<String, Object> property = cast(entry.getValue());
            // 删除有默认值的参数
            if (property.get(DEFAULT_PARAMETER_KEY) != null) {
                iterator.remove();
                defaultKeyList.add(entry.getKey());
            }
        }
        // 过滤 required 中拥有默认值的参数。
        List<String> requiredList = cast(parametersSchema.get(PARAMETERS_REQUIRED_KEY));
        requiredList.removeIf(defaultKeyList::contains);
        parametersSchema.put(PARAMETERS_REQUIRED_KEY, requiredList);
    }

    private static void filterDynamicParams(Map<String, Object> properties) {
        if (!properties.containsKey(WaterFlowToolConst.INPUT_PARAMS_KEY)) {
            return;
        }
        Map<String, Object> inputParams = cast(properties.get(WaterFlowToolConst.INPUT_PARAMS_KEY));
        Map<String, Object> inputParamsProperties = cast(inputParams.get(PARAMETERS_PROPERTIES_KEY));
        inputParamsProperties.remove(WaterFlowToolConst.TRACE_ID);
        inputParamsProperties.remove(WaterFlowToolConst.CALLBACK_ID);
    }

    @Override
    public Map<String, Object> runnables() {
        return this.toolInfo.runnables();
    }

    @Override
    public String icon() {
        return this.toolInfo.icon();
    }

    @Override
    public String creator() {
        return this.toolInfo.creator();
    }

    @Override
    public String modifier() {
        return this.toolInfo.modifier();
    }

    @Override
    public String name() {
        return this.toolInfo.name();
    }

    @Override
    public String uniqueName() {
        return this.toolInfo.uniqueName();
    }

    @Override
    public String description() {
        return this.toolInfo.description();
    }

    @Override
    public String source() {
        return this.toolInfo.source();
    }

    @Override
    public Set<String> tags() {
        return this.toolInfo.tags();
    }
}
