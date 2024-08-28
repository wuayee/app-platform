/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.IoUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * sql模块缓存
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
class SqlModuleCache {
    private final String name;

    private final Map<String, String> sqls;

    SqlModuleCache(String name) {
        this.name = name;
        this.sqls = new HashMap<>();
    }

    String script(String key) {
        notNull(key, "The key of SQL script to obtain cannot be null.");
        return this.sqls.computeIfAbsent(key, this::loadScript);
    }

    private String loadScript(String key) {
        String resourceKey = String.join("/", "sql", this.name, key + ".sql");
        try {
            return IoUtils.content(SqlModuleCache.class.getClassLoader(), resourceKey);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load SQL script: " + resourceKey, ex);
        }
    }
}
