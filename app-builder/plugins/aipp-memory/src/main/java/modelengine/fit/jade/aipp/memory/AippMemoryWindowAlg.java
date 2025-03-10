/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory;

import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 表示历史记录的滑动窗口方式设置的枚举类。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
public enum AippMemoryWindowAlg {
    /**
     * 表示最大缓存数为窗长的滑动窗口方式。
     */
    BUFFER_WINDOW,

    /**
     * 表示最大分词数为窗长的滑动窗口方式。
     */
    TOKEN_WINDOW;

    private static final Map<String, AippMemoryWindowAlg> KEY_MAP = MapBuilder.<String, AippMemoryWindowAlg>get()
            .put("buffer_window", BUFFER_WINDOW)
            .put("token_window", TOKEN_WINDOW)
            .build();

    /**
     * 根据给定的键获取 {@link AippMemoryWindowAlg}，
     * 如果键不存在，则返回默认的序列化方式 {@link AippMemoryWindowAlg#BUFFER_WINDOW}。
     *
     * @param key 表示输入键的 {@link String}。
     * @return 表示序列化方式的 {@link AippMemoryWindowAlg}。
     */
    public static AippMemoryWindowAlg from(String key) {
        return KEY_MAP.getOrDefault(key, BUFFER_WINDOW);
    }
}