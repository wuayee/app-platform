/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util.sql;

/**
 * 表示排序配置。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-21
 */
public interface OrderBy {
    /**
     * 表示升序。
     */
    String ASCENDING = "ASC";
    /**
     * 表示降序。
     */
    String DESCENDING = "DESC";

    /**
     * 获取排序的属性。
     *
     * @return 表示排序的属性的 {@link String}。
     */
    String property();

    /**
     * 获取排序的方式，{@link #ASCENDING 升序}，或 {@link #DESCENDING 降序}。
     *
     * @return 表示排序的方式的 {@link String}。
     */
    String order();

    /**
     * 从字符串中解析排序配置。
     *
     * @param value 表示包含排序配置的字符串的 {@link String}。
     * @return 表示从字符串中解析到的排序配置的 {@link OrderBy}。
     */
    static OrderBy parse(String value) {
        return DefaultOrderBy.parse(value);
    }

    /**
     * 创建一个排序配置。
     *
     * @param property 表示待排序依据的属性的名称的 {@link String}。
     * @return 表示新创建的排序配置的 {@link OrderBy}。
     */
    static OrderBy of(String property) {
        return of(property, null);
    }

    /**
     * 创建一个排序配置。
     *
     * @param property 表示待排序依据的属性的名称的 {@link String}。
     * @param order 表示待排序的顺序的 {@link String}。
     * @return 表示新创建的排序配置的 {@link OrderBy}。
     */
    static OrderBy of(String property, String order) {
        return new DefaultOrderBy(property, order);
    }
}

