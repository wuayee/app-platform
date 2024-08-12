/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.query;

import com.huawei.jade.carver.tool.model.query.ToolQuery;

import java.util.Set;

/**
 * 动态条件查询插件工具的类。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
public class PluginToolQuery extends ToolQuery {
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
     * 空参构造 {@link PluginToolQuery}。
     */
    public PluginToolQuery() {
    }

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

    /**
     * {@link PluginToolQuery} 的构建器。
     */
    public static class Builder {
        private String toolName;

        private Set<String> includeTags;

        private Set<String> excludeTags;

        private String mode;

        private Integer offset;

        private Integer limit;

        private String version;

        private Boolean isPublished;

        private String owner;

        private String collector;

        /**
         * 向当前构建器中设置工具名。
         *
         * @param toolName 表示工具名的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder toolName(String toolName) {
            this.toolName = toolName;
            return this;
        }

        /**
         * 向当前构建器中设置包含标签。
         *
         * @param includeTags 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder includeTags(Set<String> includeTags) {
            this.includeTags = includeTags;
            return this;
        }

        /**
         * 向当前构建器中设置排除标签。
         *
         * @param excludeTags 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder excludeTags(Set<String> excludeTags) {
            this.excludeTags = excludeTags;
            return this;
        }

        /**
         * 向当前构建器中设置模式。
         *
         * @param mode 表示标签与和或模式的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        /**
         * 向当前构建器中设置偏移量。
         *
         * @param offset 表示偏移量的 {@link Integer}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        /**
         * 向当前构建器中设置页面大小。
         *
         * @param limit 表示页码的 {@link Integer}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /**
         * 向当前构建器中设置版本。
         *
         * @param version 表示页码的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * 向当前构建器中设置发布状态。
         *
         * @param isPublished 表示发布状态的 {@link Boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder isPublished(Boolean isPublished) {
            this.isPublished = isPublished;
            return this;
        }

        /**
         * 向当前构建器中设置拥有者。
         *
         * @param owner 表示拥有者的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        /**
         * 向当前构建器中设置收藏者。
         *
         * @param collector 表示收藏者的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder collector(String collector) {
            this.collector = collector;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link PluginToolQuery}。
         */
        public PluginToolQuery build() {
            PluginToolQuery pluginToolQuery = new PluginToolQuery();
            pluginToolQuery.setToolName(this.toolName);
            pluginToolQuery.setIncludeTags(this.includeTags);
            pluginToolQuery.setExcludeTags(this.excludeTags);
            pluginToolQuery.setMode(this.mode);
            pluginToolQuery.setOffset(this.offset);
            pluginToolQuery.setLimit(this.limit);
            pluginToolQuery.setVersion(this.version);
            pluginToolQuery.setPublished(this.isPublished);
            pluginToolQuery.setOwner(this.owner);
            pluginToolQuery.setCollector(this.collector);
            return pluginToolQuery;
        }
    }
}
