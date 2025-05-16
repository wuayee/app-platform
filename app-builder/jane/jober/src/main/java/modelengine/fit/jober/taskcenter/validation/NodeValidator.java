/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

import modelengine.fit.jane.task.util.OperationContext;

import java.util.Set;

/**
 * 为任务节点提供校验器。
 *
 * @author 梁济时
 * @since 2023-08-17
 */
public interface NodeValidator {
    /**
     * parentId
     *
     * @param parentId parentId
     * @param context context
     * @return String
     */
    String parentId(String parentId, OperationContext context);

    /**
     * name
     *
     * @param name name
     * @param names names
     * @param context context
     * @return String
     */
    String name(String name, Set<String> names, OperationContext context);
}
