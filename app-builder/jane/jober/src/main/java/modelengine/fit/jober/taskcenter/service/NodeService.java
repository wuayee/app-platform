/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.model.TextStringValue;
import modelengine.fit.jober.taskcenter.declaration.NodeDeclaration;
import modelengine.fit.jober.taskcenter.domain.NodeEntity;
import modelengine.fit.jober.taskcenter.filter.NodeFilter;

import modelengine.fitframework.model.RangedResultSet;

import java.util.List;
import java.util.Map;

/**
 * 为任务树的节点提供管理。s
 *
 * @author 梁济时
 * @since 2023-08-09
 */
public interface NodeService {
    /**
     * 创建一个任务树节点。
     *
     * @param treeId 表示节点所在任务树的唯一标识的 {@link String}。
     * @param declaration 表示节点的声明的 {@link NodeDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的节点的 {@link NodeEntity}。
     */
    NodeEntity create(String treeId, NodeDeclaration declaration, OperationContext context);

    /**
     * 修改节点信息。
     *
     * @param treeId 表示待修改的节点所在的任务树的唯一标识的 {@link String}。
     * @param nodeId 表示待修改的节点的唯一标识的 {@link String}。
     * @param declaration 表示节点的声明的 {@link NodeDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patch(String treeId, String nodeId, NodeDeclaration declaration, OperationContext context);

    /**
     * 删除指定节点。
     *
     * @param treeId 表示待删除的节点所在任务树的唯一标识的 {@link String}。
     * @param nodeId 表示待删除的节点的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void delete(String treeId, String nodeId, OperationContext context);

    /**
     * 删除符合条件的节点。
     *
     * @param treeId 表示待删除的节点所在任务树的唯一标识的 {@link String}。
     * @param filter 表示待删除的节点的过滤器的 {@link NodeFilter}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void delete(String treeId, NodeFilter filter, OperationContext context);

    /**
     * 检索指定节点。
     *
     * @param treeId 表示待检索的节点所在任务树的唯一标识的 {@link String}。
     * @param nodeId 表示待检索的节点的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示检索到的节点信息的 {@link NodeEntity}。
     */
    NodeEntity retrieve(String treeId, String nodeId, OperationContext context);

    /**
     * 列出符合条件的节点。
     *
     * @param treeId 表示待查询的节点所在任务树的唯一标识的 {@link String}。
     * @param filter 表示待查询的节点的过滤器的 {@link NodeFilter}。
     * @param offset 表示待查询的结果集在所有符合条件的结果集中的偏移量的 64 位整数。
     * @param limit 表示待查询的结果集中允许包含结果的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的分页结果集的 {@link RangedResultSet}{@code <}{@link NodeEntity}{@code >}。
     */
    RangedResultSet<NodeEntity> list(String treeId, NodeFilter filter, long offset, int limit,
            OperationContext context);

    /**
     * 查询儿子节点列表。
     *
     * @param nodeIds 表示父节点列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示返回的儿子节点列表信息。
     */
    Map<String, List<TextStringValue>> findChild(List<String> nodeIds);
}
