/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.query;

import com.huawei.jade.common.Result;

/**
 * 动态条件查询模型的类。
 *
 * @author 鲁为
 * @since 2024-06-07
 */
public class ModelQuery {
    /**
     * 表示任务唯一标识。
     */
    private String taskName;

    /**
     * 表示偏移量。
     */
    private Integer offset;

    /**
     * 表示数量限制。
     */
    private Integer limit;

    /**
     * 构造动态查询条件。
     *
     * @param taskName 表示任务唯一标识的 {@link String}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param pageSize 表示限制的 {@link Integer}。
     */
    public ModelQuery(String taskName, Integer pageNum, Integer pageSize) {
        this.taskName = taskName;
        this.limit = pageSize;
        if (pageNum != null && pageSize != null) {
            this.offset = Result.calculateOffset(pageNum, pageSize);
        }
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

    /**
     * 获取偏移量。
     *
     * @return 表示偏移量的 {@link Integer}。
     */
    public Integer getOffset() {
        return this.offset;
    }

    /**
     * 设置偏移量。
     *
     * @param offset 表示待设置的偏移量的 {@link Integer}。
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * 获取数量限制值。
     *
     * @return 表示数量限制值的 {@link Integer}。
     */
    public Integer getLimit() {
        return this.limit;
    }

    /**
     * 设置数量限制值。
     *
     * @param limit 表示待设置的数量限制值的 {@link Integer}。
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
