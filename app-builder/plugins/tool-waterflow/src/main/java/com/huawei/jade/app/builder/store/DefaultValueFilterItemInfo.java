/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.builder.store;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.util.MapUtils;
import com.huawei.jade.store.ItemInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 提供一个返回去除默认参数摘要信息的元素信息结构。
 *
 * @author 王攀博
 * @since 2024-04-22
 */
public class DefaultValueFilterItemInfo implements ItemInfo {
    private final ItemInfo itemInfo;

    public DefaultValueFilterItemInfo(ItemInfo itemInfo) {
        this.itemInfo = itemInfo;
    }

    @Override
    public Map<String, Object> schema() {
        // 过滤掉摘要信息中拥有默认值的参数。
        Map<String, Object> schema = new HashMap<>(this.itemInfo.schema());
        Map<String, Object> parametersSchema = cast(schema.get("parameters"));
        if (MapUtils.isEmpty(parametersSchema)) {
            return this.itemInfo.schema();
        }
        Map<String, Object> properties = cast(parametersSchema.get("properties"));
        if (MapUtils.isEmpty(properties)) {
            return this.itemInfo.schema();
        }
        Iterator<Map.Entry<String, Object>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            Map<String, Object> property = cast(entry.getValue());
            // 删除有默认值的参数
            if (property.get("default") != null) {
                iterator.remove();
            }
        }
        return schema;
    }

    @Override
    public String category() {
        return this.itemInfo.category();
    }

    @Override
    public String group() {
        return this.itemInfo.group();
    }

    @Override
    public String name() {
        return this.itemInfo.name();
    }

    @Override
    public String uniqueName() {
        return this.itemInfo.uniqueName();
    }

    @Override
    public String description() {
        return this.itemInfo.description();
    }

    @Override
    public Set<String> tags() {
        return this.itemInfo.tags();
    }
}
