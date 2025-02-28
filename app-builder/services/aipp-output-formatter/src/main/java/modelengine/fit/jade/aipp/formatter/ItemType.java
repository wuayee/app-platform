/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用响应数据单元类型。
 *
 * @author 刘信宏
 * @since 2024-11-26
 */
public enum ItemType {
    /**
     * 纯文本类型。
     */
    TEXT("TEXT"),

    /**
     * 带有溯源信息的文本类型
     */
    TEXT_WITH_REFERENCE("TEXT_WITH_REFERENCE"),

    /**
     * 未知类型。
     */
    INVALID("INVALID");

    private static final Map<String, ItemType> ITEM_TYPE_MAP =
            Arrays.stream(ItemType.values()).collect(Collectors.toMap(ItemType::value, Function.identity()));

    private final String value;

    ItemType(String value) {
        this.value = value;
    }

    /**
     * 获取数据单元类型名称。
     *
     * @return 表示数据单元类型名称的 {@link String}。
     */
    public String value() {
        return this.value;
    }

    /**
     * 依据字符串解析 {@link ItemType} 对象。
     *
     * @param value 表示应用响应数据单元类型的 {@link String}。
     * @return 表示应用响应数据单元类型的 {@link ItemType}。
     */
    public static ItemType from(String value) {
        return ITEM_TYPE_MAP.getOrDefault(StringUtils.toUpperCase(value), INVALID);
    }
}
