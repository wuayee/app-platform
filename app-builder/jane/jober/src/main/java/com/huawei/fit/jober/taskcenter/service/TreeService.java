/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.TreeDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TreeEntity;
import com.huawei.fit.jober.taskcenter.filter.TreeFilter;
import com.huawei.fitframework.model.RangedResultSet;

/**
 * 为任务树提供管理。
 *
 * @author 梁济时
 * @since 2023-08-09
 */
public interface TreeService {
    /**
     * 创建任务树。
     *
     * @param declaration 表示任务树的声明的 {@link TreeDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务树的 {@link TreeEntity}。
     */
    TreeEntity create(TreeDeclaration declaration, OperationContext context);

    /**
     * 修改任务树。
     *
     * @param treeId 表示待修改的任务树的唯一标识的 {@link String}。
     * @param declaration 表示任务树的声明的 {@link TreeDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patch(String treeId, TreeDeclaration declaration, OperationContext context);

    /**
     * 删除任务树。
     *
     * @param treeId 表示待删除的任务树的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void delete(String treeId, OperationContext context);

    /**
     * 检索任务树。
     *
     * @param treeId 表示待检索的任务树的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示任务树信息的 {@link TreeEntity}。
     */
    TreeEntity retrieve(String treeId, OperationContext context);

    /**
     * 查询任务树。
     *
     * @param filter 表示任务树的过滤器的 {@link TreeFilter}。
     * @param offset 表示待查询的结果集在所有符合条件的结果集中的偏移量的 64 位整数。
     * @param limit 表示待查询的结果集中允许包含结果的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的分页结果集的 {@link RangedResultSet}{@code <}{@link TreeEntity}{@code >}。
     */
    RangedResultSet<TreeEntity> list(TreeFilter filter, long offset, int limit, OperationContext context);
}
