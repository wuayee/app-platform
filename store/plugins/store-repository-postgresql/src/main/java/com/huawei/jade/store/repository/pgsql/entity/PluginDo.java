/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import com.huawei.jade.store.entity.transfer.PluginData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存入数据库的模型的实体类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginDo extends CommonDo {
    /**
     * 表示插件的发布状态。
     */
    private Boolean isPublished;

    /**
     * 表示插件的拥有者。
     */
    private String owner;

    /**
     * 表示插件的点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示插件的下载量。
     */
    private Integer downloadCount;

    /**
     * 表示插件的名称。
     */
    private String toolName;

    /**
     * 表示插件的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 用传输层的插件数据 {@link PluginData} 构造 {@link PluginDo}。
     *
     * @param pluginData 表示传输层的插件数据的 {@link PluginData}。
     * @return 表示数据库层的插件数据的 {@link PluginDo}。
     */
    public static PluginDo from(PluginData pluginData) {
        PluginDo pluginDo = new PluginDo();
        pluginDo.setIsPublished(pluginData.isPublished());
        pluginDo.setOwner(pluginData.getOwner());
        pluginDo.setLikeCount(pluginData.getLikeCount());
        pluginDo.setDownloadCount(pluginData.getDownloadCount());
        pluginDo.setToolName(pluginData.getName());
        pluginDo.setToolUniqueName(pluginData.getUniqueName());
        return pluginDo;
    }
}
