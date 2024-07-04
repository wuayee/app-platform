/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.query;

import com.huawei.jade.carver.tool.model.query.ToolQuery;

import java.util.List;

/**
 * 动态条件查询插件的类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public class PluginQuery extends ToolQuery {
    /**
     * 表示插件是否已发布。
     * <p>构造条件时按需传入。</p>
     */
    private Boolean isPublished;

    /**
     * 插件的拥有者。
     * <p>构造条件时按需传入。</p>
     */
    private String owner;

    /**
     * 插件的收藏者。
     * <p>构造条件时按需传入。</p>
     */
    private String collector;

    /**
     * 用所有参数构造 {@link PluginQuery}。
     *
     * @param isPublished 表示插件是否已发布的 {@link Boolean}。
     * @param owner 表示插件拥有者的 {@link String}。
     * @param collector 表示收藏者的名字的 {@link String}。
     * @param toolName 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @param version 表示工具版本的 {@link String}。
     */
    public PluginQuery(Boolean isPublished, String owner, String collector, String toolName, List<String> includeTags,
            List<String> excludeTags, Integer pageNum, Integer limit, String version) {
        super(toolName, includeTags, excludeTags, pageNum, limit, version);
        this.collector = collector;
        this.isPublished = isPublished;
        this.owner = owner;
    }

    /**
     * 空参构造 {@link PluginQuery}。
     */
    public PluginQuery() {}

    /**
     * 获取发布状态。
     *
     * @return 表示发布状态的 {@code boolean}。
     */
    public Boolean isPublished() {
        return this.isPublished;
    }

    /**
     * 设置发布状态。
     *
     * @param isPublished 表示发布状态的 {@code boolean}。
     */
    public void setPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    /**
     * 获取插件拥有者。
     *
     * @return 表示插件拥有者的 {@link String}。
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * 设置插件拥有者。
     *
     * @param owner 表示插件拥有者的 {@link String}。
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * 获取插件收藏者。
     *
     * @return 表示插件收藏者的 {@link String}。
     */
    public String getCollector() {
        return this.collector;
    }

    /**
     * 设置插件收藏者。
     *
     * @param collector 表示插件收藏者的 {@link String}。
     */
    public void setCollector(String collector) {
        this.collector = collector;
    }
}
