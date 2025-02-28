/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.domain.entity;

import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 表示支持的编程语言类型枚举。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
public enum ProgrammingLanguage {
    /**
     * python 语言。
     */
    @Property(name = "python") PYTHON;

    private static final Map<String, ProgrammingLanguage> KEY_MAP =
            MapBuilder.<String, ProgrammingLanguage>get().put("python", PYTHON).build();

    /**
     * 根据给定的键获取 {@link ProgrammingLanguage}，如果键不存在，则返回 {@code null}。
     *
     * @param key 表示输入键的 {@link String}。
     * @return 表示序列化方式的 {@link ProgrammingLanguage}。
     */
    public static ProgrammingLanguage from(String key) {
        return KEY_MAP.getOrDefault(key, null);
    }
}