/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.PagedResultSet;
import modelengine.fit.jane.task.util.Pagination;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.util.sql.OrderBy;

import java.util.List;

/**
 * 为个人待办提供管理
 *
 * @author 罗书强
 * @since 2024-01-22
 */
public interface TaskAgendaService {
    /**
     * 根据模板查询任务列表
     *
     * @param filter 表示过滤条件的 {@link TaskInstance.Filter}。
     * @param pagination 表示分页查询的 {@link Pagination}。
     * @param templateId 表示模板id的 {@link String}。
     * @param taskEntityList 表示查询到的任务定义的 {@link List<String>}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param orderBys 表示排序条件
     * @return 表示查询到的实例信息的 {@link PagedResultSet<TaskInstance>}。
     */
    PagedResultSet<TaskInstance> listAllAgenda(TaskInstance.Filter filter, Pagination pagination, String templateId,
            OperationContext context, List<TaskEntity> taskEntityList, List<OrderBy> orderBys);

    /**
     * 检索任务id。
     *
     * @param filter 表示过滤条件的 {@link TaskInstance.Filter}。
     * @param pagination 表示分页查询的 {@link Pagination}。
     * @param templateId 表示模板id的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param orderBys 表示排序条件的{@link List}{@code <}{@link OrderBy}{@code >}
     * @return 表示查询到的任务定义的 {@link List<String>}。
     */
    List<String> listTaskIds(TaskInstance.Filter filter, Pagination pagination, String templateId,
            OperationContext context, List<OrderBy> orderBys);
}
