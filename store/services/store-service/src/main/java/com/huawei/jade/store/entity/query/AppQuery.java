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
 * @since 2024-06-15
 */
public class AppQuery extends ToolQuery {
    /**
     * 空参构造 {@link AppQuery}。
     */
    public AppQuery() {}

    /**
     * {@link AppQuery} 的构建器。
     */
    public static class Builder {
        private String toolName;
        private Set<String> includeTags;
        private Set<String> excludeTags;
        private String mode;
        private Integer offset;
        private Integer limit;
        private String version;

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
         * 向当前构建器中设置页码。
         *
         * @param offset 表示页码的 {@link Integer}。
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
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link AppQuery}。
         */
        public AppQuery build() {
            AppQuery appQuery = new AppQuery();
            appQuery.setToolName(this.toolName);
            appQuery.setIncludeTags(this.includeTags);
            appQuery.setExcludeTags(this.excludeTags);
            appQuery.setMode(this.mode);
            appQuery.setOffset(this.offset);
            appQuery.setLimit(this.limit);
            appQuery.setVersion(this.version);
            return appQuery;
        }
    }
}
