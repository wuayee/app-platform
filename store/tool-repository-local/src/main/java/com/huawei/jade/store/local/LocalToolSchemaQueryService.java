/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.local;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.jade.store.Item;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.repository.ToolRepository;
import com.huawei.jade.store.service.ToolSchemaQueryService;

import java.util.Collections;
import java.util.Map;

/**
 * 表示 {@link ToolSchemaQueryService} 的本地实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
@Component
public class LocalToolSchemaQueryService implements ToolSchemaQueryService {
    private final ToolRepository repository;

    public LocalToolSchemaQueryService(ToolRepository repository) {
        this.repository = repository;
    }

    @Override
    @Fitable(id = "local")
    public Map<String, Object> search(String group, String toolName) {
        // 过滤
        return this.repository.getTool(group, toolName)
                .map(Item::itemInfo)
                .map(ItemInfo::schema)
                .orElseGet(Collections::emptyMap);
    }
}
