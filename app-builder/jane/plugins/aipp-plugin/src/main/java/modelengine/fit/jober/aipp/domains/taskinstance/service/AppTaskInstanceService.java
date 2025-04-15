/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.taskinstance.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.common.RangedResultSet;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 应用任务实例服务.
 *
 * @author 张越
 * @since 2024-12-31
 */
public interface AppTaskInstanceService {
    /**
     * 获取单个任务实例.
     *
     * @param taskId 任务唯一标识.
     * @param taskInstanceId 任务实例唯一标识.
     * @param context 操作人上下文信息.
     * @return {@link Optional}{@code <}{@link AppTaskInstance}{@code >} 对象.
     */
    Optional<AppTaskInstance> getInstance(String taskId, String taskInstanceId, OperationContext context);

    /**
     * 获取任务实例列表.
     *
     * @param taskId 任务唯一标识.
     * @param query 查询参数.
     * @param offset 偏移量.
     * @param limit 单次查询条数.
     * @param context 操作人上下文信息.
     * @return {@link RangedResultSet}{@code <}{@link AppTaskInstance}{@code >} 任务实例列表.
     */
    RangedResultSet<AppTaskInstance> getInstances(String taskId, AppTaskInstance query, long offset, int limit,
            OperationContext context);

    /**
     * 通过任务实例id获取任务实例列表.
     *
     * @param taskId 任务唯一标识.
     * @param limit 单次查询条数.
     * @param context 操作人上下文信息.
     * @return {@link List}{@code <}{@link AppTaskInstance}{@code >} 任务实例列表.
     */
    List<AppTaskInstance> getInstancesByTaskId(String taskId, int limit, OperationContext context);

    /**
     * 通过任务实例id获取任务实例列表流.
     *
     * @param taskId 任务唯一标识.
     * @param limit 单次查询条数.
     * @param context 操作人上下文信息.
     * @return {@link Stream}{@code <}{@link AppTaskInstance}{@code >} 任务实例列表流.
     */
    Stream<AppTaskInstance> getInstanceStreamByTaskId(String taskId, int limit, OperationContext context);

    /**
     * 修改单个任务实例.
     *
     * @param instance 待修改任务实例参数.
     * @param context 操作人上下文信息.
     */
    void update(AppTaskInstance instance, OperationContext context);

    /**
     * 创建任务实例.
     *
     * @param instance 任务实例创建参数.
     * @param context 操作人上下文信息.
     * @return {@link AppTaskInstance} 对象.
     */
    AppTaskInstance createInstance(AppTaskInstance instance, OperationContext context);

    /**
     * 删除单个任务实例.
     *
     * @param taskId 任务唯一标识.
     * @param taskInstanceId 任务实例唯一标识.
     * @param context 操作人上下文信息.
     */
    void delete(String taskId, String taskInstanceId, OperationContext context);

    /**
     * 通过任务实例id获取任务id.
     *
     * @param taskInstanceId 任务实例唯一标识.
     * @return {@link String} 任务id.
     */
    String getTaskId(String taskInstanceId);

    /**
     * 通过实例id获取实例对象.
     *
     * @param taskInstanceId 实例id.
     * @param context 操作人上下文.
     * @return {@link AppTaskInstance} 的 {@link Optional} 对象.
     */
    Optional<AppTaskInstance> getInstanceById(String taskInstanceId, OperationContext context);
}
