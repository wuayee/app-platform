/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.store.entity.query.TaskQuery;
import com.huawei.jade.store.entity.transfer.TaskData;
import com.huawei.jade.store.repository.pgsql.entity.TaskDo;
import com.huawei.jade.store.repository.pgsql.mapper.TaskMapper;
import com.huawei.jade.store.service.TaskService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 任务的 Http 请求的服务层实现。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-06
 */
@Component
public class DefaultTaskService implements TaskService {
    private final ObjectSerializer serializer;
    private final TaskMapper taskMapper;

    /**
     * 通过持久层接口来初始化 {@link DefaultTaskService} 的实例。
     *
     * @param serializer 表示序列化器实例的 {@link ObjectSerializer}。
     * @param taskMapper 表示持久层实例的 {@link TaskMapper}。
     */
    public DefaultTaskService(@Fit(alias = "json") ObjectSerializer serializer, TaskMapper taskMapper) {
        this.serializer = serializer;
        this.taskMapper = taskMapper;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public TaskData getTask(String taskId) {
        TaskDo taskDo = this.taskMapper.getTask(taskId);
        TaskData taskData = new TaskData();
        taskData.setTaskId(taskDo.getTaskId());
        taskData.setSchema(json2obj(taskDo.getSchema(), serializer));
        taskData.setContext(json2obj(taskDo.getContext(), serializer));
        taskData.setToolUniqueName(taskDo.getToolUniqueName());
        return taskData;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public List<TaskData> getTasks(TaskQuery taskQuery) {
        if (taskQuery == null) {
            return Collections.emptyList();
        }
        if ((taskQuery.getLimit() != null
                && taskQuery.getLimit() < 0)) {
            return Collections.emptyList();
        }
        List<TaskDo> dos = this.taskMapper.getTasks(taskQuery);
        ArrayList<TaskData> taskDataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dos)) {
            for (TaskDo taskDo : dos) {
                TaskData taskData = new TaskData();
                taskData.setTaskId(taskDo.getTaskId());
                taskData.setSchema(json2obj(taskDo.getSchema(), serializer));
                taskData.setContext(json2obj(taskDo.getContext(), serializer));
                taskData.setToolUniqueName(taskDo.getToolUniqueName());
                taskDataList.add(taskData);
            }
        }
        return taskDataList;
    }

    /**
     * 反序列化。
     *
     * @param schema 表示待序列化的字符串 {@link String}。
     * @param serializer 表示序列化对象的 {@link ObjectSerializer}。
     * @return 序列化的结果的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public static Map<String, Object> json2obj(String schema, ObjectSerializer serializer) {
        Map<String, Object> res = null;
        if (schema != null) {
            res = serializer.deserialize(schema,
                    TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class}));
        }
        return res;
    }
}
