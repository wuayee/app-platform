/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory;

import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 表示历史记录序列化方式的枚举类。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
public enum AippMemorySerializeAlg {
    /**
     * 表示包含问题与回答的序列化方式。
     */
    FULL,

    /**
     * 表示仅包含问题的序列化方式。
     */
    QUESTION_ONLY;

    private static final Map<String, AippMemorySerializeAlg> KEY_MAP = MapBuilder.<String, AippMemorySerializeAlg>get()
            .put("full", FULL)
            .put("question_only", QUESTION_ONLY)
            .build();

    /**
     * 根据给定的键获取 {@link AippMemorySerializeAlg}，如果键不存在，则返回默认的序列化方式 {@link AippMemorySerializeAlg#FULL}。
     *
     * @param key 表示输入键的 {@link String}。
     * @return 表示序列化方式的 {@link AippMemorySerializeAlg}。
     */
    public static AippMemorySerializeAlg from(String key) {
        return KEY_MAP.getOrDefault(key, FULL);
    }
}