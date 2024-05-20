/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.builder.store;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.util.MapUtils;
import com.huawei.jade.store.Tool;

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
    private final Tool.Info toolInfo;

    public DefaultValueFilterToolInfo(Tool.Info toolInfo) {
        notNull(toolInfo, "the tool info cannot be null");
        this.toolInfo = toolInfo;
    }

    @Override
    public Map<String, Object> schema() {
        // 过滤掉摘要信息中拥有默认值的参数。
        Map<String, Object> schema = new HashMap<>(this.toolInfo.schema());
        Map<String, Object> parametersSchema = cast(schema.get("parameters"));
        if (MapUtils.isEmpty(parametersSchema)) {
            return this.toolInfo.schema();
        }
        Map<String, Object> properties = cast(parametersSchema.get("properties"));
        if (MapUtils.isEmpty(properties)) {
            return this.toolInfo.schema();
        }
        List<String> defaultKeyList = new ArrayList<>();
        Iterator<Map.Entry<String, Object>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            Map<String, Object> property = cast(entry.getValue());
            // 删除有默认值的参数
            if (property.get("default") != null) {
                iterator.remove();
                defaultKeyList.add(entry.getKey());
            }
        }
        // 过滤 required 中拥有默认值的参数。
        List<String> requiredList = cast(parametersSchema.get("required"));
        requiredList.removeIf(defaultKeyList::contains);
        parametersSchema.put("required", requiredList);
        // 过滤 order 字段
        parametersSchema.remove("order");
        return schema;
    }

    @Override
    public Map<String, Object> runnables() {
        return this.toolInfo.runnables();
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
