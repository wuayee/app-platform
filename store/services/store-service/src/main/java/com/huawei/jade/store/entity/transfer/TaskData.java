/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import java.util.Map;

/**
 * 存入数据库的任务的传输实体类。
 *
 * @author 鲁为
 * @since 2024-06-06
 */
public class TaskData {
    /**
     * 表示任务的唯一标识。
     */
    private String taskName;

    /**
     * 表示任务的上下文。
     */
    private Map<String, Object> context;

    /**
     * 表示工具的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 获取任务的唯一标识。
     *
     * @return 表示任务的唯一标识的 {@link String}。
     */
    public String getTaskName() {
        return this.taskName;
    }

    /**
     * 设置任务的唯一标识。
     *
     * @param taskName 表示任务的唯一标识的 {@link String}。
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * 获取任务的上下文。
     *
     * @return 表示任务的上下文的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public Map<String, Object> getContext() {
        return this.context;
    }

    /**
     * 设置任务的上下文。
     *
     * @param context 表示任务的上下文的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * 获取工具的唯一标识。
     *
     * @return 表示工具的唯一标识的 {@link String}。
     */
    public String getToolUniqueName() {
        return this.toolUniqueName;
    }

    /**
     * 设置工具的唯一标识。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     */
    public void setToolUniqueName(String toolUniqueName) {
        this.toolUniqueName = toolUniqueName;
    }
}
