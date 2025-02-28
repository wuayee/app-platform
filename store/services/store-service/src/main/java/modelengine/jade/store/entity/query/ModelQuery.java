/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.query;

import modelengine.jade.common.Result;

/**
 * 动态条件查询模型的类。
 *
 * @author 鲁为
 * @since 2024-06-07
 */
public class ModelQuery extends CommonQuery {
    /**
     * 表示任务唯一标识。
     */
    private String taskName;

    /**
     * 构造动态查询条件。
     *
     * @param taskName 表示任务唯一标识的 {@link String}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param pageSize 表示限制的 {@link Integer}。
     */
    public ModelQuery(String taskName, Integer pageNum, Integer pageSize) {
        super(pageNum != null && pageSize != null ? Result.calculateOffset(pageNum, pageSize) : null, pageSize);
        this.taskName = taskName;
    }

    /**
     * 获取任务唯一标识。
     *
     * @return 标识任务唯一标识的 {@link String}。
     */
    public String getTaskName() {
        return this.taskName;
    }

    /**
     * 设置任务唯一标识。
     *
     * @param taskName 标识待设置的任务唯一标识的 {@link String}。
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
