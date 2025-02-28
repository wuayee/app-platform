/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TriggerEntity;
import modelengine.fit.jober.taskcenter.domain.util.PropertyMonitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 {@link PropertyMonitor} 提供默认实现。
 *
 * @author 陈镕希
 * @since 2023-10-30
 */
public class DefaultPropertyMonitor implements PropertyMonitor {
    private final TaskEntity task;

    private final Map<String, Map<String, Set<String>>> fitables;

    /**
     * 构造函数
     *
     * @param task 任务
     */
    public DefaultPropertyMonitor(TaskEntity task) {
        this.task = task;
        this.fitables = new HashMap<>();
        this.fillSources(this.task.getSources());
    }

    private void fillSources(List<SourceEntity> sources) {
        if (sources == null) {
            return;
        }
        for (SourceEntity source : sources) {
            this.fillTriggers(source.getId(), source.getTriggers());
        }
    }

    private void fillTriggers(String sourceId, List<TriggerEntity> triggers) {
        if (triggers == null) {
            return;
        }
        this.fitables.put(sourceId, triggers.stream()
                .collect(Collectors.groupingBy(TriggerEntity::getPropertyId,
                        Collectors.mapping(TriggerEntity::getFitableId, Collectors.toSet()))));
    }

    @Override
    public boolean hasTriggers(String sourceId) {
        return this.fitables.containsKey(sourceId);
    }

    @Override
    public Set<String> getFitableIds(String sourceId, String propertyId) {
        Map<String, Set<String>> propertied = nullIf(this.fitables.get(sourceId), Collections.emptyMap());
        return nullIf(propertied.get(propertyId), Collections.emptySet());
    }
}
