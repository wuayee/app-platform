/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter.impl;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.InstanceConverter;
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
