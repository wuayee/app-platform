/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.transfer;

import java.util.Map;

/**
 * 存入数据库的模型的传输实体类。
 *
 * @author 鲁为
 * @since 2024-06-07
 */
public class ModelData {
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
