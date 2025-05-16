/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

/**
 * 为任务类型提供校验器。
 *
 * @author 梁济时
 * @since 2023-09-13
 */
public interface TaskTypeValidator {
    /**
     * 校验任务唯一标识。
     *
     * @param taskId 表示输入的任务唯一标识的 {@link String}。
     * @return 表示符合要求的任务唯一标识的 {@link String}。
     */
    String taskId(String taskId);

    /**
     * 校验任务类型的名称。
     *
     * @param name 表示任务类型的名称的 {@link String}。
     * @return 表示符合要求的任务类型的名称的 {@link String}。
     */
    String name(String name);

    /**
     * 校验任务类型的父类型的唯一标识。
     *
     * @param parentId 表示父类型唯一标识的 {@link String}。
     * @return 表示符合要求的父类型唯一标识的 {@link String}。
     */
    String parentId(String parentId);
}
