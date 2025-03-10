/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.config;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

/**
 * 插件上传约束性配置。
 *
 * @author 杭潇
 * @since 2024-9-18
 */
@Component
@AcceptConfigValues("store.plugin.uploader")
public class PluginUploadConstraintConfig {
    /** 插件上传最大插件数量。 */
    private int maxPluginNumber;

    /** 插件可上传的最大存储使用量占比。 */
    private double maxStorageRatio;

    /** 保存工具的路径。 */
    private String toolsPath;

    /**
     * 获取最大的插件上传数量。
     *
     * @return 表示获取最大插件上传数量的 {@code int}。
     */
    public int getMaxPluginNumber() {
        return this.maxPluginNumber;
    }

    /**
     * 设置最大插件上传代码数量。
     *
     * @param maxPluginNumber 表示设置的最大代码的数量值的 {@code int}。
     */
    public void setMaxPluginNumber(int maxPluginNumber) {
        this.maxPluginNumber = maxPluginNumber;
    }

    /**
     * 获取最大的可上传插件的存储容量占比。
     *
     * @return 表示最大的可上传插件的存储容量占比的 {@code double}。
     */
    public double getMaxStorageRatio() {
        return this.maxStorageRatio;
    }

    /**
     * 设置最大可上传插件的存储容量占比值。
     *
     * @param maxStorageRatio 表示设置的最大的可上传插件的存储容量占比值的 {@code double}。
     */
    public void setMaxStorageRatio(double maxStorageRatio) {
        this.maxStorageRatio = maxStorageRatio;
    }

    /**
     * 获取保存工具的路径。
     *
     * @return 表示保存工具的路径的 {@link String}。
     */
    public String getToolsPath() {
        return this.toolsPath;
    }

    /**
     * 设置保存工具的路径。
     *
     * @param toolsPath 表示保存工具的路径的 {@link String}。
     */
    public void setToolsPath(String toolsPath) {
        this.toolsPath = toolsPath;
    }
}