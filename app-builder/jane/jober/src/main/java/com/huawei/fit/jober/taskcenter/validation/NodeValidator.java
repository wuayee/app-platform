/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.util.OperationContext;

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
