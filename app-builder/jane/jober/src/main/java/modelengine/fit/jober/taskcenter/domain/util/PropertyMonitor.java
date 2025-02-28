/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util;

import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.util.support.DefaultPropertyMonitor;

import java.util.Set;

/**
 * 为属性的变更提供监听器。
 *
 * @author 梁济时
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

