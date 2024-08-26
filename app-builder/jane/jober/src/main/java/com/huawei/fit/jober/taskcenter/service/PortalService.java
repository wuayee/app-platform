/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.domain.portal.TaskNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.model.RangedResultSet;

import java.util.List;
import java.util.Map;

/**
 * 为前端调用提供管理。
 *
 * @author 陈镕希
 * @since 2023-08-17
 */
public interface PortalService {
    /**
     * 创建任务定义。
     *
     * @param treeId 表示任务树唯一标识的 {@link String}。
     * @param declaration 表示任务声明的 {@link TaskDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务定义的 {@link TaskEntity}。
     */
    TaskEntity createTask(String treeId, TaskDeclaration declaration, OperationContext context);

    /**
     * 创建任务数据源。
     *
     * @param treeId 表示任务树唯一标识的 {@link String}。
     * @param nodeId 表示任务节点唯一标识的 {@link String}。
     * @param declaration 表示任务数据源声明的 {@link SourceDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务数据源的 {@link SourceEntity}。
     */
    SourceEntity createSource(String treeId, String nodeId, SourceDeclaration declaration, OperationContext context);

    /**
     * 查询任务数据源。
     *
     * @param treeId 表示任务树唯一标识的 {@link String}。
     * @param nodeId 表示任务节点唯一标识的 {@link String}。
     * @param offset 表示待查询的结果集在所有符合条件的结果集中的偏移量的 64 位整数。
     * @param limit 表示待查询的结果集中允许包含结果的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的任务数据源的
     * {@link RangedResultSet}{@code <}{@link SourceEntity}{@code >}。
     */
    RangedResultSet<SourceEntity> listSource(String treeId, String nodeId, long offset, int limit,
            OperationContext context);

    /**
     * getTree
     *
     * @param context context
     * @return List<TaskNode>
     */
    List<TaskNode> getTree(OperationContext context);

    /**
     * listTaskGroups
     *
     * @param owners owners
     * @param creators creators
     * @param tags tags
     * @param categories categories
     * @param taskIds taskIds
     * @param context context
     * @return List<TaskGroup>
     */
    List<TaskGroup> listTaskGroups(List<String> owners, List<String> creators, List<String> tags,
            List<String> categories, List<String> taskIds, OperationContext context);

    /**
     * count
     *
     * @param owners owners
     * @param creators creators
     * @param tags tags
     * @param taskIds taskIds
     * @param context context
     * @return List<TagCountEntity>
     */
    List<TagCountEntity> count(List<String> owners, List<String> creators, List<String> tags, List<String> taskIds,
            OperationContext context);

    /**
     * 创建任务定义。
     *
     * @param declaration 表示任务定义的声明的 {@link TaskDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务定义的 {@link TaskEntity}。
     */
    TaskEntity createTask(TaskDeclaration declaration, OperationContext context);

    /**
     * 修改任务定义。
     *
     * @param taskId 表示待修改的任务定义的唯一标识的 {@link String}。
     * @param declaration 表示任务定义的声明的 {@link TaskDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patchTask(String taskId, TaskDeclaration declaration, OperationContext context);

    /**
     * 删除任务定义。
     *
     * @param taskId 表示待删除的任务定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void deleteTask(String taskId, OperationContext context);

    /**
     * 检索任务定义。
     *
     * @param taskId 表示任务定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示任务定义的 {@link TaskEntity}。
     */
    TaskEntity retrieveTask(String taskId, OperationContext context);

    /**
     * 创建任务属性。
     *
     * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
     * @param declaration 表示任务属性的声明的 {@link TaskProperty.Declaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务属性的 {@link TaskProperty}。
     */
    TaskProperty createTaskProperty(String taskId, TaskProperty.Declaration declaration, OperationContext context);

    /**
     * 修改任务属性。
     *
     * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
     * @param propertyId 表示待修改的任务属性的唯一标识的 {@link String}。
     * @param declaration 表示任务属性的声明的 {@link TaskProperty.Declaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patchTaskProperty(String taskId, String propertyId, TaskProperty.Declaration declaration,
            OperationContext context);

    /**
     * 批量为任务定义的属性打补丁。
     *
     * @param taskId 表示待修补的属性所属任务定义的唯一标识的 {@link String}。
     * @param declarations 表示待修补的内容以属性唯一标识作为键的映射的 {@link Map}{@code <}{@link String}{@code ,
     * }{@link TaskProperty.Declaration}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patchProperties(String taskId, Map<String, TaskProperty.Declaration> declarations, OperationContext context);

    /**
     * 删除任务属性。
     *
     * @param taskId 表示任务属性所属任务定义的唯一标识的 {@link String}。
     * @param propertyId 表示待删除的任务属性的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void deleteTaskProperty(String taskId, String propertyId, OperationContext context);

    /**
     * 创建任务类型。
     *
     * @param taskId 表示任务类型所属任务定义的唯一标识的 {@link String}。
     * @param declaration 表示任务类型的声明的 {@link TaskType.Declaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务类型的 {@link TaskType}。
     */
    TaskType createTaskType(String taskId, TaskType.Declaration declaration, OperationContext context);

    /**
     * 修改任务类型。
     *
     * @param taskId 表示任务类型所属任务定义的唯一标识的 {@link String}。
     * @param typeId 表示待修改的任务类型的唯一标识的 {@link String}。
     * @param declaration 表示任务类型的声明的 {@link TaskType.Declaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patchTaskType(String taskId, String typeId, TaskType.Declaration declaration, OperationContext context);

    /**
     * 删除任务类型。
     *
     * @param taskId 表示任务类型所属任务定义的唯一标识的 {@link String}。
     * @param typeId 表示待删除的任务类型的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void deleteTaskType(String taskId, String typeId, OperationContext context);

    /**
     * 创建任务数据源。
     *
     * @param taskId 表示任务数据源所属的任务定义的唯一标识的 {@link String}。
     * @param typeId 表示任务数据源所属的任务类型的唯一标识的 {@link String}。
     * @param declaration 表示任务数据源的声明的 {@link SourceDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务数据源的 {@link SourceEntity}。
     */
    SourceEntity createTaskSource(String taskId, String typeId, SourceDeclaration declaration,
            OperationContext context);

    /**
     * 修改任务数据源。
     *
     * @param taskId 表示任务数据源所属的任务定义的唯一标识的 {@link String}。
     * @param typeId 表示任务数据源所属的任务类型的唯一标识的 {@link String}。
     * @param sourceId 表示待修改的任务数据源的唯一标识的 {@link String}。
     * @param declaration 表示任务数据源的声明的 {@link SourceDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patchTaskSource(String taskId, String typeId, String sourceId, SourceDeclaration declaration,
            OperationContext context);

    /**
     * 删除任务数据源。
     *
     * @param taskId 表示任务数据源所属的任务定义的唯一标识的 {@link String}。
     * @param typeId 表示任务数据源所属的任务类型的唯一标识的 {@link String}。
     * @param sourceId 表示待删除的任务数据源的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void deleteTaskSource(String taskId, String typeId, String sourceId, OperationContext context);

    /**
     * 列出指定任务类型下所有的任务数据源。
     *
     * @param taskId 表示任务数据源所属的任务定义的唯一标识的 {@link String}。
     * @param typeId 表示任务数据源所属的任务类型的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示包含任务数据源的列表的 {@link List}{@code <}{@link SourceEntity}{@code >}。
     */
    List<SourceEntity> listTaskSources(String taskId, String typeId, OperationContext context);

    /**
     * 为任务详情提供有数据的任务树的信息。
     *
     * @author 梁济时
     * @since 2023-08-30
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class TaskGroup {
        private String treeId;

        private String treeName;

        private String taskId;

        private Integer numberOfTasks;
    }

    /**
     * Pagination
     *
     * @since 2023-09-15
     */
    @Data
    class Pagination {
        private long offset;

        private int limit;

        private long total;
    }

    /**
     * TagCountEntity
     *
     * @since 2023-09-15
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class TagCountEntity {
        private String status;

        private Long value;
    }
}
