/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;

import java.util.Map;
import java.util.Set;

/**
 * 表示插件的数据内容。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public class PluginToolData extends ToolData {
    /**
     * 表示插件工具点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示插件工具下载数量。
     */
    private Integer downloadCount;

    /**
     * 表示工具名。
     */
    private String name;

    /**
     * 表示插件的唯一标识。
     */
    private String pluginId;

    /**
     * 表示工具的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 空参构造 {@link PluginToolData}。
     */
    public PluginToolData() {
    }

    /**
     * 获取插件点赞数量。
     *
     * @return 表示点赞数量的 {@link Integer}。
     */
    public Integer getLikeCount() {
        return this.likeCount;
    }

    /**
     * 设置点赞数量。
     *
     * @param likeCount 表示点赞数量的 {@link Integer}。
     */
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * 获取下载数量。
     *
     * @return 表示下载数量的 {@link Integer}。
     */
    public Integer getDownloadCount() {
        return this.downloadCount;
    }

    /**
     * 设置下载数量。
     *
     * @param downloadCount 表示下载数量的 {@link Integer}。
     */
    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    /**
     * 获取工具名。
     *
     * @return 表示工具名的 {@link String}。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置工具名。
     *
     * @param name 表示工具名的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取插件唯一标识。
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
     * 获取插件工具唯一标识。
     *
     * @return 表示插件工具唯一标识的 {@link String}。
     */
    public String getToolUniqueName() {
        return this.toolUniqueName;
    }

    /**
     * 设置插件工具唯一标识。
     *
     * @param toolUniqueName 表示插件工具唯一标识的 {@link String}。
     */
    public void setToolUniqueName(String toolUniqueName) {
        this.toolUniqueName = toolUniqueName;
    }

    /**
     * {@link PluginToolData} 的构建器。
     */
    public static class Builder {
        private String creator;

        private String modifier;

        private String name;

        private String description;

        private String uniqueName;

        private Map<String, Object> schema;

        private Map<String, Object> runnables;

        private String source;

        private String icon;

        private Set<String> tags;

        private String version;

        private Integer likeCount;

        private Integer downloadCount;

        private String pluginId;

        private String toolUniqueName;

        /**
         * 向当前构建器中设置创建者。
         *
         * @param creator 表示插件工具创建者的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder creator(String creator) {
            this.creator = creator;
            return this;
        }

        /**
         * 向当前构建器中设置修改者。
         *
         * @param modifier 表示修改者的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder modifier(String modifier) {
            this.modifier = modifier;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具名。
         *
         * @param name 表示排除标签的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 向当前构建器中设置描述。
         *
         * @param description 表示描述的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具的唯一标识。
         *
         * @param uniqueName 表示页码的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder uniqueName(String uniqueName) {
            this.uniqueName = uniqueName;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具格式规范。
         *
         * @param schema 表示页码的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder schema(Map<String, Object> schema) {
            this.schema = schema;
            return this;
        }

        /**
         * 向当前构建器中设置工具的运行描述规范。
         *
         * @param runnables 表示页码的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder runnables(Map<String, Object> runnables) {
            this.runnables = runnables;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具的来源。
         *
         * @param source 表示来源的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder source(String source) {
            this.source = source;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具的图标。
         *
         * @param icon 表示来源的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具的标签。
         *
         * @param tags 表示标签的 {@link Set}{@code <}{@link String}{@link >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder tags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具的版本。
         *
         * @param version 表示版本的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具的点赞量。
         *
         * @param likeCount 表示点赞量的 {@link Integer}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder likeCount(Integer likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具的下载量。
         *
         * @param downloadCount 表示下载量的 {@link Integer}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder downloadCount(Integer downloadCount) {
            this.downloadCount = downloadCount;
            return this;
        }

        /**
         * 向当前构建器中设置插件的唯一标识。
         *
         * @param pluginId 表示下载量的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder pluginId(String pluginId) {
            this.pluginId = pluginId;
            return this;
        }

        /**
         * 向当前构建器中设置插件工具的唯一标识。
         *
         * @param toolUniqueName 表示插件工具唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder toolUniqueName(String toolUniqueName) {
            this.toolUniqueName = toolUniqueName;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link ToolQuery}。
         */
        public PluginToolData build() {
            PluginToolData pluginToolData = new PluginToolData();
            pluginToolData.setCreator(this.creator);
            pluginToolData.setModifier(this.modifier);
            pluginToolData.setName(this.name);
            pluginToolData.setDescription(this.description);
            pluginToolData.setUniqueName(this.uniqueName);
            pluginToolData.setSchema(this.schema);
            pluginToolData.setRunnables(this.runnables);
            pluginToolData.setSource(this.source);
            pluginToolData.setIcon(this.icon);
            pluginToolData.setTags(this.tags);
            pluginToolData.setVersion(this.version);
            pluginToolData.setLikeCount(this.likeCount);
            pluginToolData.setDownloadCount(this.downloadCount);
            pluginToolData.setPluginId(this.pluginId);
            pluginToolData.setToolUniqueName(this.toolUniqueName);
            return pluginToolData;
        }
    }
}
