/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.query;

/**
 * 动态条件查询任务的类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-07
 */
public class TaskQuery {
    /**
     * 表示工具唯一标识。
     * <p>构造条件时按需传入。</p>
     */
    private String toolUniqueName;

    /**
     * 表示偏移量。
     * <p>构造条件时按需传入。</p>
     */
    private Integer offset;

    /**
     * 表示数量限制。
     * <p>构造条件时按需传入。</p>
     */
    private Integer limit;

    /**
     * 构造动态查询条件。
     *
     * @param toolUniqueName 表示工具名的 {@link String}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param pageSize 表示限制的 {@link Integer}。
     */
    public TaskQuery(String toolUniqueName, Integer pageNum,
            Integer pageSize) {
        this.toolUniqueName = toolUniqueName;
        this.limit = pageSize;
        if (pageNum != null && pageSize != null) {
            this.offset = this.getOffset(pageNum, pageSize);
        }
    }

    public String getToolUniqueName() {
        return this.toolUniqueName;
    }

    public void setToolUniqueName(String toolUniqueName) {
        this.toolUniqueName = toolUniqueName;
    }

    public Integer getOffset() {
        return this.offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    private int getOffset(int pageNum, int pageSize) {
        return pageNum < 0 || pageSize < 0 ? 0 : (pageNum - 1) * pageSize;
    }
}

