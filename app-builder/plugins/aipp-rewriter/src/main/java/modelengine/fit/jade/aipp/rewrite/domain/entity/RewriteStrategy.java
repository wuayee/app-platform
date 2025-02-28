/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain.entity;

import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 表示重写策略的枚举。
 *
 * @author 易文渊
 * @since 2024-09-20
 */
public enum RewriteStrategy {
    /**
     * 内置方法。
     */
    BUILTIN,

    /**
     * 自定义方法。
     */
    CUSTOM;

    private static final Map<String, RewriteStrategy> KEY_MAP =
            MapBuilder.<String, RewriteStrategy>get().put("builtin", BUILTIN).put("custom", CUSTOM).build();

    /**
     * 根据给定的键获取 {@link RewriteStrategy}，如果键不存在，则返回默认的序列化方式 {@link RewriteStrategy#BUILTIN}。
     *
     * @param key 表示输入键的 {@link String}。
     * @return 表示序列化方式的 {@link RewriteStrategy}。
     */
    public static RewriteStrategy from(String key) {
        return KEY_MAP.getOrDefault(key, BUILTIN);
    }
}