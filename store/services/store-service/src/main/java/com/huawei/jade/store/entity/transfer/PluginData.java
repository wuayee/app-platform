/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import com.huawei.jade.carver.tool.model.transfer.ToolData;

/**
 * 表示插件的数据内容。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public class PluginData extends ToolData {
    /**
     * 表示是否发布到市场。
     */
    private Boolean isPublished;

    /**
     * 表示插件的拥有者。
     */
    private String owner;

    /**
     * 表示插件点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示插件下载数量。
     */
    private Integer downloadCount;

    /**
     * 用所有属性构造 {@link PluginData}。
     *
     * @param toolData 表示工具的数据的 {@link ToolData}。
     * @param isPublished 表示插件是否已发布的 {@code boolean}。
     * @param owner 表示插件的拥有者的 {@link String}。
     * @param likeCount 表示插件的点赞数量的 {@code int}。
     * @param downloadCount 表示插件的下载数量的 {@code int}。
     */
    public PluginData(ToolData toolData, Boolean isPublished, String owner, Integer likeCount, Integer downloadCount) {
        this.setCreator(toolData.getCreator());
        this.setModifier(toolData.getModifier());
        this.setName(toolData.getName());
        this.setDescription(toolData.getDescription());
        this.setUniqueName(toolData.getUniqueName());
        this.setSchema(toolData.getSchema());
        this.setRunnables(toolData.getRunnables());
        this.setSource(toolData.getSource());
        this.setIcon(toolData.getIcon());
        this.setTags(toolData.getTags());
        this.setVersion(toolData.getVersion());
        this.isPublished = isPublished;
        this.owner = owner;
        this.likeCount = likeCount;
        this.downloadCount = downloadCount;
    }

    /**
     * 空参构造 {@link PluginData}。
     */
    public PluginData() {}

    /**
     * 获取插件发布状态。
     *
     * @return 表示发布状态的 {@code boolean}。
     */
    public Boolean isPublished() {
        return this.isPublished;
    }

    /**
     * 设置插件发布状态。
     *
     * @param isPublished 表示发布状态的 {code boolean}。
     */
    public void setPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    /**
     * 获取插件拥有者。
     *
     * @return 表示拥有者的名字的 {@link String}。
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * 设置插件拥有者。
     *
     * @param owner 表示插件拥有者的名字的 {@link String}。
     */
    public void setOwner(String owner) {
        this.owner = owner;
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
}
