/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultCategoryAcceptor;

import java.util.List;
import java.util.Map;

/**
 * 为类目提供配置器。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-25
 */
public interface CategoryAcceptor {
    /**
     * 根据任务数据获取类目。
     *
     * @param info 表示任务数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示类目的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> obtain(Map<String, Object> info);

    /**
     * of
     *
     * @param task task
     * @return CategoryAcceptor
     */
    static CategoryAcceptor of(TaskEntity task) {
        return new DefaultCategoryAcceptor(task.getProperties());
    }
}
