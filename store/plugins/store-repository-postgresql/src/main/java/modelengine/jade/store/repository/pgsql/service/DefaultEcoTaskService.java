/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.store.entity.query.QueryUtils;
import modelengine.jade.store.entity.query.TaskQuery;
import modelengine.jade.store.entity.transfer.TaskData;
import modelengine.jade.store.repository.pgsql.entity.TaskDo;
import modelengine.jade.store.repository.pgsql.mapper.TaskMapper;
import modelengine.jade.store.service.EcoTaskService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务的 Http 请求的服务层实现。
 *
 * @author 鲁为
 * @since 2024-06-06
 */
@Component
public class DefaultEcoTaskService implements EcoTaskService {
    private static final String FITABLE_ID = "store-repository-pgsql";

    private final ObjectSerializer serializer;
    private final TaskMapper taskMapper;

    /**
     * 通过持久层接口来初始化 {@link DefaultEcoTaskService} 的实例。
     *
     * @param serializer 表示序列化器实例的 {@link ObjectSerializer}。
     * @param taskMapper 表示持久层实例的 {@link TaskMapper}。
     */
    public DefaultEcoTaskService(@Fit(alias = "json") ObjectSerializer serializer, TaskMapper taskMapper) {
        this.serializer = notNull(serializer, "The json serializer cannot be null.");
        this.taskMapper = notNull(taskMapper, "The task mapper cannot be null.");
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public TaskData getTask(String taskId) {
        TaskDo taskDo = this.taskMapper.getTask(taskId);
        return TaskDo.convertToTaskData(taskDo, this.serializer);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<TaskData> getTasks(TaskQuery taskQuery) {
        if (taskQuery == null || QueryUtils.isPageInvalid(taskQuery.getOffset(), taskQuery.getLimit())) {
            return Collections.emptyList();
        }
        List<TaskDo> dos = this.taskMapper.getTasks(taskQuery);
        return dos.stream()
                .map(taskDo -> TaskDo.convertToTaskData(taskDo, this.serializer))
                .collect(Collectors.toList());
    }
}
