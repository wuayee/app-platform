/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 为sql提供缓存
 *
 * @author 陈镕希
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
