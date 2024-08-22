/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import java.util.List;
import java.util.Map;

/**
 * 表示插件的数据内容。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
public class PluginData {
    /**
     * 表示插件的创建者。
     * <p>
     *     <ul>
     *         <li>添加插件时可选。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String creator;

    /**
     * 表示插件的修改者。
     * <p>
     *     <ul>
     *         <li>添加插件时可选。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String modifier;

    /**
     * 表示插件的唯一标识。
     * <p>
     *     <ul>
     *         <li>添加插件时不需要设置。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String pluginId;

    /**
     * 表示插件的名字。
     * <p>
     *     <ul>
     *         <li>添加插件时需传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String pluginName;

    /**
     * 表示插件的扩展信息。
     * <p>
     *     <ul>
     *         <li>添加插件时需传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private Map<String, Object> extension;

    /**
     * 表示插件的部署状态。
     * <p>
     *     <ul>
     *         <li>添加插件时不传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String deployStatus;

    /**
     * 表示插件包含的插件工具数据。
     * <p>
     *     <ul>
     *         <li>添加插件时不传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private List<PluginToolData> pluginToolDataList;

    /**
     * 表示插件是否内置。
     * <p>
     *     <ul>
     *         <li>添加插件时不传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private Boolean isBuiltin;

    /**
     * 获取插件的创建者。
     *
     * @return 表示插件创建者的 {@link String}。
     */
    public String getCreator() {
        return this.creator;
    }

    /**
     * 设置插件的创建者。
     *
     * @param creator 表示插件创建者的 {@link String}。
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 获取插件的修改者。
     *
     * @return 表示插件修改者的 {@link String}。
     */
    public String getModifier() {
        return this.modifier;
    }

    /**
     * 设置插件的修改者。
     *
     * @param modifier 表示插件修改者的 {@link String}。
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * 获取插件的唯一标识。
     *
     * @return 表示插件唯一标识的 {@link String}。
     */
    public String getPluginId() {
        return this.pluginId;
    }

    /**
     * 设置插件唯一标识。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     */
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    /**
     * 获取插件的名字。
     *
     * @return 表示插件名字的 {@link String}。
     */
    public String getPluginName() {
        return this.pluginName;
    }

    /**
     * 设置插件的名字。
     *
     * @param pluginName 表示插件名字的 {@link String}。
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * 获取插件的扩展。
     *
     * @return 表示插件扩展的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public Map<String, Object> getExtension() {
        return this.extension;
    }

    /**
     * 设置插件的扩展。
     *
     * @param extension 表示插件扩展的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public void setExtension(Map<String, Object> extension) {
        this.extension = extension;
    }

    /**
     * 获取插件的部署状态。
     *
     * @return 表示部署状态的 {@link String}。
     */
    public String getDeployStatus() {
        return deployStatus;
    }

    /**
     * 设置插件的部署状态。
     *
     * @param deployStatus 表示插件部署状态的 {@link String}。
     */
    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }


    /**
     * 获取插件包含的插件工具的列表。
     *
     * @return 表示插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     */
    public List<PluginToolData> getPluginToolDataList() {
        return this.pluginToolDataList;
    }

    /**
     * 设置插件工具列表。
     *
     * @param pluginToolDataList 表示插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     */
    public void setPluginToolDataList(List<PluginToolData> pluginToolDataList) {
        this.pluginToolDataList = pluginToolDataList;
    }

    /**
     * 获取是否内置。
     *
     * @return 表示是否内置的 {@link Boolean}。
     */
    public Boolean getBuiltin() {
        return this.isBuiltin;
    }

    /**
     * 设置是否内置。
     *
     * @param builtin 表示是否内置的 {@link Boolean}。
     */
    public void setBuiltin(Boolean builtin) {
        this.isBuiltin = builtin;
    }
}
