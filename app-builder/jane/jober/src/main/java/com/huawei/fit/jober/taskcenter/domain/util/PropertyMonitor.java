/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultPropertyMonitor;

import java.util.Set;

/**
 * 为属性的变更提供监听器。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-26
 */
public interface PropertyMonitor {
    /**
     * of
     *
     * @param task task
     * @return PropertyMonitor
     */
    static PropertyMonitor of(TaskEntity task) {
        return new DefaultPropertyMonitor(task);
    }

    /**
     * hasTriggers
     *
     * @param sourceId sourceId
     * @return boolean
     */
    boolean hasTriggers(String sourceId);

    /**
     * getFitableIds
     *
     * @param sourceId sourceId
     * @param propertyId propertyId
     * @return Set<String>
     */
    Set<String> getFitableIds(String sourceId, String propertyId);
}

