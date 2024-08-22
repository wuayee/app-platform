/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.query;

import com.huawei.jade.carver.tool.model.query.ToolQuery;

import java.util.Set;

/**
 * 动态条件查询插件的类。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
public class PluginQuery extends ToolQuery {
    private Boolean isBuiltin;

    /**
     * 空参构造 {@link PluginQuery}。
     */
    public PluginQuery() {}

    /**
     * {@link PluginQuery} 的构建器。
     */
    public static class Builder {
        private String toolName;
        private Set<String> includeTags;
        private Set<String> excludeTags;
        private String mode;
        private Integer offset;
        private Integer limit;
        private Boolean isBuiltin;

        /**
         * 向当前构建器中设置关键词。
         *
         * @param toolName 表示关键词的 {@link String}。
         * @return 表示当前构建器的 {@link PluginToolQuery.Builder}。
         */
        public Builder toolName(String toolName) {
            this.toolName = toolName;
            return this;
        }

        /**
         * 向当前构建器中设置包含标签。
         *
         * @param includeTags 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link PluginToolQuery.Builder}。
         */
        public Builder includeTags(Set<String> includeTags) {
            this.includeTags = includeTags;
            return this;
        }

        /**
         * 向当前构建器中设置排除标签。
         *
         * @param excludeTags 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link PluginToolQuery.Builder}。
         */
        public Builder excludeTags(Set<String> excludeTags) {
            this.excludeTags = excludeTags;
            return this;
        }

        /**
         * 向当前构建器中设置模式。
         *
         * @param mode 表示标签与和或模式的 {@link String}。
         * @return 表示当前构建器的 {@link PluginToolQuery.Builder}。
         */
        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        /**
         * 向当前构建器中设置偏移量。
         *
         * @param offset 表示偏移量的 {@link Integer}。
         * @return 表示当前构建器的 {@link PluginToolQuery.Builder}。
         */
        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        /**
         * 向当前构建器中设置页面大小。
         *
         * @param limit 表示页码的 {@link Integer}。
         * @return 表示当前构建器的 {@link PluginToolQuery.Builder}。
         */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /**
         * 向构建器中设置是否内置。
         *
         * @param isBuiltin 表示是否内置的 {@link Boolean}。
         * @return 表示当前构建器的 {@link PluginToolQuery.Builder}。
         */
        public Builder isBuiltin(Boolean isBuiltin) {
            this.isBuiltin = isBuiltin;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link PluginToolQuery}。
         */
        public PluginQuery build() {
            PluginQuery pluginQuery = new PluginQuery();
            pluginQuery.setToolName(this.toolName);
            pluginQuery.setIncludeTags(this.includeTags);
            pluginQuery.setExcludeTags(this.excludeTags);
            pluginQuery.setMode(this.mode);
            pluginQuery.setOffset(this.offset);
            pluginQuery.setLimit(this.limit);
            pluginQuery.setBuiltin(this.isBuiltin);
            return pluginQuery;
        }
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
