/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.deploy.config;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

/**
 * 插件部署状态查询配置参数。
 *
 * @author 罗帅
 * @since 2024-8-19
 */
@Component
@AcceptConfigValues("store.plugin.deployer.query")
public class PluginDeployQueryConfig {
    /** 插件部署状态查询超时时间，单位: 秒 */
    private int timeout;

    /** 插件部署状态查询间隔，单位: 秒 */
    private int interval;

    /** 保存工具的路径。 */
    private String toolsPath;

    /**
     * 获取插件部署状态查询间隔。
     *
     * @return 表示插件部署状态查询间隔的 {@code int}。
     */
    public int getInterval() {
        return this.interval;
    }

    /**
     * 设置插件部署状态查询间隔。
     *
     * @param interval 表示插件部署状态间隔的 {@code int}。
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * 获取插件部署状态查询超时时间。
     *
     * @return 表示插件部署状态查实时间的 {@code int}。
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * 设置插件部署查询超时时间。
     *
     * @param timeout 表示插件部署状态查询超时时间的 {@code int}。
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
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