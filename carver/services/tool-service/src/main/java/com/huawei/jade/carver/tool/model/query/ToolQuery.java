/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.model.query;

import java.util.Set;

/**
 * 动态条件查询的类。
 *
 * @author 李金绪 l00878072
 * @since 2024-05-10
 */
public class ToolQuery {
    /**
     * 表示工具名称。
     * <p>构造条件时按需传入。</p>
     */
    private String toolName;

    /**
     * 表示需要包括的标签列表。
     * <p>构造条件时按需传入。</p>
     */
    private Set<String> includeTags;

    /**
     * 表示需要排除的标签列表。
     * <p>构造条件时按需传入。</p>
     */
    private Set<String> excludeTags;

    /**
     * 表示选择标签的与和或逻辑。
     * <p>构造条件时按需传入，默认为 AND，表示与逻辑；可传 OR 表示或逻辑。</p>
     */
    private String mode;

    /**
     * 表示偏移量。
     * <p>构造条件时按需传入。</p>
     */
    private Integer offset;

    /**
     * 表示数量限制。
     * <p>构造条件时按需传入。</p>
     */
    private Integer limit;

    /**
     * 表示工具版本。
     * <p>构造条件时按需传入。</p>
     */
    private String version;

    /**
     * 空参构造 {@link ToolQuery}。
     */
    public ToolQuery() {}

    /**
     * {@link ToolQuery} 的构建器。
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
         * @return 表示当前构建器的 {@link ToolQuery.Builder}。
         */
        public Builder toolName(String toolName) {
            this.toolName = toolName;
            return this;
        }

        /**
         * 向当前构建器中设置包含标签。
         *
         * @param includeTags 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link ToolQuery.Builder}。
         */
        public Builder includeTags(Set<String> includeTags) {
            this.includeTags = includeTags;
            return this;
        }

        /**
         * 向当前构建器中设置排除标签。
         *
         * @param excludeTags 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link ToolQuery.Builder}。
         */
        public Builder excludeTags(Set<String> excludeTags) {
            this.excludeTags = excludeTags;
            return this;
        }

        /**
         * 向当前构建器中设置模式。
         *
         * @param mode 表示标签与和或模式的 {@link String}。
         * @return 表示当前构建器的 {@link ToolQuery.Builder}。
         */
        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        /**
         * 向当前构建器中设置页面大小。
         *
         * @param offset 表示页码的 {@link Integer}。
         * @return 表示当前构建器的 {@link ToolQuery.Builder}。
         */
        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        /**
         * 向当前构建器中设置页面大小。
         *
         * @param limit 表示页码的 {@link Integer}。
         * @return 表示当前构建器的 {@link ToolQuery.Builder}。
         */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /**
         * 向当前构建器中设置版本。
         *
         * @param version 表示页码的 {@link String}。
         * @return 表示当前构建器的 {@link ToolQuery.Builder}。
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link ToolQuery}。
         */
        public ToolQuery build() {
            ToolQuery toolQuery = new ToolQuery();
            toolQuery.setToolName(this.toolName);
            toolQuery.setIncludeTags(this.includeTags);
            toolQuery.setExcludeTags(this.excludeTags);
            toolQuery.setMode(this.mode);
            toolQuery.setOffset(this.offset);
            toolQuery.setLimit(this.limit);
            toolQuery.setVersion(this.version);
            return toolQuery;
        }
    }

    /**
     * 获取工具名。
     *
     * @return 工具名的 {@link String}。
     */
    public String getToolName() {
        return this.toolName;
    }

    /**
     * 设置工具名。
     *
     * @param toolName 表示工具名的 {@link String}。
     */
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /**
     * 获取需要包含的标签。
     *
     * @return 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> getIncludeTags() {
        return this.includeTags;
    }

    /**
     * 设置包含标签。
     *
     * @param includeTags 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setIncludeTags(Set<String> includeTags) {
        this.includeTags = includeTags;
    }

    /**
     * 获取排除标签。
     *
     * @return 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> getExcludeTags() {
        return this.excludeTags;
    }

    /**
     * 设置包含标签。
     *
     * @param excludeTags 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setExcludeTags(Set<String> excludeTags) {
        this.excludeTags = excludeTags;
    }

    /**
     * 获取标签与和或的模式。
     *
     * @return 表示标签与和或模式的 {@link String}。
     */
    public String getMode() {
        return this.mode;
    }

    /**
     * 设置标签的与和或的模式。
     *
     * @param mode 表示标签与和或模式的 {@link String}。
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * 获取偏移量。
     *
     * @return 表示偏移量的 {@link Integer}。
     */
    public Integer getOffset() {
        return this.offset;
    }

    /**
     * 设置偏移量。
     *
     * @param offset 表示偏移量的 {@link String}。
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * 获取页面大小。
     *
     * @return 表示页面大小的 {@link Integer}。
     */
    public Integer getLimit() {
        return this.limit;
    }

    /**
     * 设置页面大小。
     *
     * @param limit 表示页面大小的 {@link Integer}。
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * 获取版本。
     *
     * @return 表示版本的 {@link String}。
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * 设置版本。
     *
     * @param version 表示版本的 {@link String}。
     */
    public void setVersion(String version) {
        this.version = version;
    }
}

