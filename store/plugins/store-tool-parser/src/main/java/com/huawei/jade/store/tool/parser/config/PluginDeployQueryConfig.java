/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.config;

import com.huawei.fitframework.annotation.AcceptConfigValues;
import com.huawei.fitframework.annotation.Component;

/**
 * 插件部署状态查询配置参数。
 *
 * @author 罗帅
 * @since 2024-8-19
 */
@Component
@AcceptConfigValues("store.plugin.deploy.query")
public class PluginDeployQueryConfig {
    /** 插件部署状态查询超时时间，单位: 秒 */
    private int timeout;

    /** 插件部署状态查询间隔，单位: 秒 */
    private int interval;

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
}