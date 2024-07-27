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

    private String name;

    private String app;

    private SourceType type;

    private List<TriggerEntity> triggers;

    private List<InstanceEvent> events;

    /**
     * 查找任务数据源
     *
     * @param types 表示任务类型的集合的{@link Collection}{@code <}{@link TaskType}{@code >}
     * @param sourceId 表示数据源id的{@link String}
     * @return 任务数据源
     */
    public static SourceEntity lookup(Collection<TaskType> types, String sourceId) {
        Queue<TaskType> queue = new LinkedList<>(types);
        SourceEntity result = null;
        while (!queue.isEmpty()) {
            TaskType current = queue.poll();
            Optional<SourceEntity> optional = current.sources().stream()
                    .filter(source -> Entities.match(source.getId(), sourceId))
                    .findAny();
            if (optional.isPresent()) {
                result = optional.get();
                break;
            }
            queue.addAll(current.children());
        }
        return result;
    }
}
