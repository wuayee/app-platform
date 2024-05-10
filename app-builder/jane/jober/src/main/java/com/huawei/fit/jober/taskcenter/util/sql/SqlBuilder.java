/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

/**
 * 为 SQL 语句提供构建器。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-04
 */
public interface SqlBuilder {
    /**
     * 追加一个标识符。
     *
     * @param identifier 表示标识符的 {@link String}。
     * @return 表示当前构建器的 {@link SqlBuilder}。
     */
    SqlBuilder appendIdentifier(String identifier);

    /**
     * 追加一个字符串。
     *
     * @param value 表示待追加的字符串的 {@link String}。
     * @return 表示当前构建器的 {@link SqlBuilder}。
     */
    SqlBuilder append(String value);

    /**
     * 追加一个字符。
     *
     * @param value 表示待追加的字符。
     * @return 表示当前构建器的 {@link SqlBuilder}。
     */
    SqlBuilder append(char value);

    /**
     * 回退指定数量的字符。
     *
     * @param count 表示待回退的字符数量的 32 位整数。
     * @return 表示当前构建器的 {@link SqlBuilder}。
     */
    SqlBuilder backspace(int count);

    /**
     * 追加指定数量的指定值。
     *
     * @param value 表示待追加的值的 {@link String}。
     * @param count 表示待追加的次数的 32 位整数。
     * @return 表示当前构建器的 {@link SqlBuilder}。
     */
    SqlBuilder appendRepeatedly(String value, int count);

    /**
     * 追加一个元素。
     *
     * @param element 表示待追加的元素的 {@link SqlElement}。
     * @return 表示当前构建器的 {@link SqlBuilder}。
     */
    default SqlBuilder append(SqlElement element) {
        element.appendTo(this);
        return this;
    }

    /**
     * 返回一个 SQL 构建器，用以自定义构建 SQL。
     *
     * @return 表示 SQL 构建器的 {@link SqlBuilder}。
     */
    static SqlBuilder custom() {
        return new DefaultSqlBuilder();
    }
}

