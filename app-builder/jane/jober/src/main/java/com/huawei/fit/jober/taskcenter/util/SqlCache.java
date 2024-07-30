/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import static com.huawei.fitframework.inspection.Validation.notNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 为sql提供缓存
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-30
 */
class SqlCache {
    private final Map<String, SqlModuleCache> modules;

    SqlCache() {
        this.modules = new HashMap<>();
    }

    SqlModuleCache module(String name) {
        notNull(name, "The module of SQL script to obtain cannot be null.");
        return this.modules.computeIfAbsent(name, SqlModuleCache::new);
    }
}
