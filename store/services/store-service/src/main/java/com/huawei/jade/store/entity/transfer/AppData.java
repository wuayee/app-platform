/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.entity.transfer;

import com.huawei.jade.carver.tool.model.transfer.ToolData;

/**
 * 表示应用的数据内容。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public class AppData extends ToolData {
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
     * @param likeCount 表示应用的点赞数量的 {@link Integer}。
     * @param downloadCount 表示应用的下载数量的 {@link Integer}。
     */
    public AppData(ToolData toolData, Integer likeCount, Integer downloadCount) {
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
        this.likeCount = likeCount;
        this.downloadCount = downloadCount;
    }

    /**
     * 空参构造 {@link AppData}。
     */
    public AppData() {}

    /**
     * 获取应用点赞数量。
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
