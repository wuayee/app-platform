/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import com.huawei.fit.jane.task.util.Entities;

import lombok.Data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * 表示任务数据源。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
@Data
public class SourceEntity {
    private String id;

    // TODO 待删除，数据源的名称即为其所属任务类型的名称，需要归至任务类型统一获取。
    private String name;

    private String app;

    private SourceType type;

    private List<TriggerEntity> triggers;

    private List<InstanceEvent> events;

    public static SourceEntity lookup(Collection<TaskType> types, String sourceId) {
        Queue<TaskType> queue = new LinkedList<>(types);
        while (!queue.isEmpty()) {
            TaskType current = queue.poll();
            Optional<SourceEntity> optional = current.sources().stream()
                    .filter(source -> Entities.match(source.getId(), sourceId))
                    .findAny();
            if (optional.isPresent()) {
                return optional.get();
            }
            queue.addAll(current.children());
        }
        return null;
    }
}
