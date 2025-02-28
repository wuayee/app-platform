/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util;

import java.util.Map;

/**
 * 为 Map 提供序列化程序。
 *
 * @author 梁济时
 * @since 2023-11-01
 */
public interface MapSerializer {
    /**
     * 将映射序列化成为一个字符串。
     *
     * @param value 表示待序列化的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示从映射序列化得到的字符串的 {@link String}。
     */
    String serialize(Map<String, Object> value);

    /**
     * 将字符串反序列化成为一个映射。
     *
     * @param value 表示待反序列化的字符串的 {@link String}。
     * @return 表示从字符串反序列化得到的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> deserialize(String value);
}
