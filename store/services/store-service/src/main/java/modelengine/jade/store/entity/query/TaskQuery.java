/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.query;

import modelengine.jade.common.Result;

/**
 * 动态条件查询任务的类。
 *
 * @author 鲁为
 * @since 2024-06-07
 */
public class TaskQuery extends CommonQuery {
    /**
     * 表示工具唯一标识。
     */
    private String toolUniqueName;

    /**
     * 构造动态查询条件。
     *
     * @param toolUniqueName 表示工具名的 {@link String}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param pageSize 表示限制的 {@link Integer}。
     */
    public TaskQuery(String toolUniqueName, Integer pageNum, Integer pageSize) {
        super(pageNum != null && pageSize != null ? Result.calculateOffset(pageNum, pageSize) : null, pageSize);
        this.toolUniqueName = toolUniqueName;
    }

    /**
     * 获取工具的唯一名字。
     *
     * @return 表示工具唯一名字的 {@link String}。
     */
    public String getToolUniqueName() {
        return this.toolUniqueName;
    }

    /**
     * 设置工具的唯一名字。
     *
     * @param toolUniqueName 表示待设置的工具唯一名字的 {@link String}。
     */
    public void setToolUniqueName(String toolUniqueName) {
        this.toolUniqueName = toolUniqueName;
    }
}

