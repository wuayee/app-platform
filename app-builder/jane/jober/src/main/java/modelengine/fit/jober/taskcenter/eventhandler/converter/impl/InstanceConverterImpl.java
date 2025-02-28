/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter.impl;

import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jober.entity.instance.Instance;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.domain.TaskType;
import modelengine.fit.jober.taskcenter.eventhandler.converter.InstanceConverter;
import modelengine.fitframework.annotation.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link InstanceConverter}的默认实现。
 *
 * @author 陈镕希
 * @since 2023-09-08
 */
@Component
public class InstanceConverterImpl implements InstanceConverter {
    @Override
    public Instance convert(TaskEntity task, TaskInstance instance) {
        Instance result = new Instance();
        result.setId(instance.id());
        result.setTypeId(Optional.ofNullable(instance.type()).map(TaskType::id).orElse(""));
        result.setSourceId(Optional.ofNullable(instance.source()).map(SourceEntity::getId).orElse(""));
        result.setInfo(convertInfo(task, instance.info()));
        result.setTags(instance.tags());
        result.setCategories(instance.categories());
        return result;
    }

    private Map<String, String> convertInfo(TaskEntity task, Map<String, Object> info) {
        Map<String, String> results = new HashMap<>(info.size());
        for (Map.Entry<String, Object> entry : info.entrySet()) {
            TaskProperty property = task.getPropertyByName(entry.getKey());
            if (property == null) {
                continue;
            }
            String value = property.dataType().toString(entry.getValue());
            results.put(property.name(), value);
        }
        return results;
    }
}
