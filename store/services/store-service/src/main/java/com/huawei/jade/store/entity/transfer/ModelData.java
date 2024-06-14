/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import java.util.Map;

/**
 * 存入数据库的模型的传输实体类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-07
 */
public class ModelData {
    /**
     * 表示模型的创建时间。
     */
    private String createdTime;

    /**
     * 表示模型的更新时间。
     */
    private String updatedTime;

    /**
     * 表示任务的唯一标识。
     */
    private String taskName;

    /**
     * 表示模型的名字。
     */
    private String name;

    /**
     * 表示模型的跳转链接。
     */
    private String url;

    /**
     * 表示任务的上下文。
     */
    private Map<String, Object> context;

    /**
     * 用所有属性的参数构造 {@link ModelData}。
     *
     * @param createdTime 表示模型创建时间的 {@link String}。
     * @param updatedTime 表示模型更新时间的 {@link String}。
     * @param taskName 表示任务唯一标识的 {@link String}。
     * @param name 表示模型名字的 {@link String}。
     * @param url 表示模型跳转链接的 {@link String}。
     * @param context 表示模型的上下文的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public ModelData(String createdTime, String updatedTime, String taskName, String name, String url,
            Map<String, Object> context) {
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.taskName = taskName;
        this.name = name;
        this.url = url;
        this.context = context;
    }

    /**
     * 参数为空的构造函数 {@link ModelData}。
     */
    public ModelData() {}

    /**
     * 获取模型的创建时间。
     *
     * @return 表示模型创建时间的 {@link String}。
     */
    public String getCreatedTime() {
        return this.createdTime;
    }

    /**
     * 设置模型的创建时间。
     *
     * @param createdTime 表示模型创建时间的 {@link String}。
     */
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取模型的更新时间。
     *
     * @return 表示模型更新时间的 {@link String}。
     */
    public String getUpdatedTime() {
        return this.updatedTime;
    }

    /**
     * 设置模型的更新时间。
     *
     * @param updatedTime 表示模型更新时间的 {@link String}。
     */
    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * 获取任务的唯一标识。
     *
     * @return 表示任务的唯一标识的 {@link String}。
     */
    public String getTaskName() {
        return this.taskName;
    }

    /**
     * 设置任务的名称。
     *
     * @param taskName 表示任务的名称的 {@link String}。
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * 获取模型的名字。
     *
     * @return 表示模型名字的 {@link String}。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置模型的唯一标识。
     *
     * @param name 表示任务的唯一标识的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取模型的跳转链接。
     *
     * @return 表示模型跳转链接的 {@link String}。
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * 设置模型的跳转链接。
     *
     * @param url 表示任务的唯一标识的 {@link String}。
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取模型的上下文。
     *
     * @return 表示模型上下文的 {@link String}。
     */
    public Map<String, Object> getContext() {
        return this.context;
    }

    /**
     * 设置模型的上下文。
     *
     * @param context 表示模型的上下文的 {@link String}。
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
