/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import java.util.Map;

/**
 * 存入数据库的任务的传输实体类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-06
 */
public class TaskData {
    /**
     * 表示任务的唯一标识。
     */
    private String taskId;

    /**
     * 表示任务的结构。
     */
    private Map<String, Object> schema;

    /**
     * 表示任务的上下文。
     */
    private Map<String, Object> context;

    /**
     * 表示工具的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 用所有属性的参数构造 {@link TaskData}。
     *
     * @param taskId 表示任务唯一标识的 {@link String}。
     * @param schema 表示工具格式的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param context 表示任务的上下文的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param toolUniqueName 表示工具唯一标识的 {@link String}。
     */
    public TaskData(String taskId, Map<String, Object> schema, Map<String, Object> context, String toolUniqueName) {
        this.taskId = taskId;
        this.schema = schema;
        this.context = context;
        this.toolUniqueName = toolUniqueName;
    }

    /**
     * 参数为空的构造函数 {@link TaskData}。
     */
    public TaskData() {}

    /**
     * 获取任务的唯一标识。
     *
     * @return 表示任务的唯一标识的 {@link String}。
     */
    public String getTaskId() {
        return this.taskId;
    }

    /**
     * 设置任务的唯一标识。
     *
     * @param taskId 表示任务的唯一标识的 {@link String}。
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 获取任务的格式。
     *
     * @return 表示任务的格式的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public Map<String, Object> getSchema() {
        return this.schema;
    }

    /**
     * 设置任务的格式。
     *
     * @param schema 表示任务的格式的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
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
