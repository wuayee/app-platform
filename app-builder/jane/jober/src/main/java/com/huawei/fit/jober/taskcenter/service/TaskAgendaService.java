/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;

import java.util.List;

/**
 * 为个人待办提供管理
 *
 * @author 罗书强 lwx1291633
 * @since 2024-01-22
 */
public interface TaskAgendaService {
    /**
     * @param filter 表示过滤条件的 {@link TaskInstance.Filter}。
     * @param pagination 表示分页查询的 {@link Pagination}。
     * @param templateId 表示模板id的 {@link String}。
     * @param taskEntityList 表示查询到的任务定义的 {@link List<String>}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的实例信息的 {@link PagedResultSet<TaskInstance>}。
     */
    PagedResultSet<TaskInstance> ListAllAgenda(TaskInstance.Filter filter, Pagination pagination,
            String templateId, OperationContext context, List<TaskEntity> taskEntityList, List<OrderBy> orderBys);

    /**
     * 检索任务id。
     *
     * @param filter 表示过滤条件的 {@link TaskInstance.Filter}。
     * @param pagination 表示分页查询的 {@link Pagination}。
     * @param templateId 表示模板id的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的任务定义的 {@link List<String>}。
     */
    List<String> listTaskIds(TaskInstance.Filter filter, Pagination pagination, String templateId,
            OperationContext context, List<OrderBy> orderBys);
}
