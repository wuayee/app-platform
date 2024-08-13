/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultPrimaryValue;

import java.util.Map;

/**
 * 表示主键的值。
 *
 * @author 梁济时
 * @since 2023-10-28
 */
public interface PrimaryValue {
    /**
     * values
     *
     * @return Map<String, Object>
     */
    Map<String, Object> values();

    /**
     * 获取一个值，该值指示当前主键值是否为空。
     *
     * @return 若为空，则为 {@code true}，否则为 {@code false}。
     */
    boolean isEmpty();

    /**
     * match
     *
     * @param object object
     * @return boolean
     */
    boolean match(TaskInstanceRow object);

    /**
     * 获取表示空主键值的实例。
     *
     * @return 表示空主键值的实例的 {@link PrimaryValue}。
     */
    static PrimaryValue empty() {
        return DefaultPrimaryValue.EMPTY;
    }
}
