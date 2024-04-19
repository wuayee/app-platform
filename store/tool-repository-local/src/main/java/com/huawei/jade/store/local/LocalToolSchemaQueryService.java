/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.local;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.ToolSchemaQueryService;
import com.huawei.jade.store.inner.ToolRepository;

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
    public Map<String, Object> search(String toolName) {
        return this.repository.getTool(toolName)
                .map(Tool::metadata)
                .map(Tool.Metadata::schema)
                .orElseGet(Collections::emptyMap);
    }
}
