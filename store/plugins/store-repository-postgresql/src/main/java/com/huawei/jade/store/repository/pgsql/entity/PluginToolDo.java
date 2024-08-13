/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.store.entity.transfer.PluginToolData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存入数据库的模型的实体类。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginToolDo extends CommonDo {
    /**
     * 表示插件工具的点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示插件工具的下载量。
     */
    private Integer downloadCount;

    /**
     * 表示插件工具的名字。
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
     * 用传输层的插件数据 {@link PluginToolData} 构造 {@link PluginToolDo}。
     *
     * @param pluginToolData 表示传输层的插件数据的 {@link PluginToolData}。
     * @return 表示数据库层的插件数据的 {@link PluginToolDo}。
     */
    public static PluginToolDo fromPluginToolData(PluginToolData pluginToolData) {
        PluginToolDo pluginToolDo = new PluginToolDo();
        pluginToolDo.setLikeCount(pluginToolData.getLikeCount());
        pluginToolDo.setDownloadCount(pluginToolData.getDownloadCount());
        pluginToolDo.setName(pluginToolData.getName());
        pluginToolDo.setPluginId(pluginToolData.getPluginId());
        pluginToolDo.setToolUniqueName(pluginToolData.getUniqueName());
        return pluginToolDo;
    }

    /**
     * 用工具数据 {@link ToolData} 和插件工具持久层 {@link PluginToolDo} 构造 {@link PluginToolData}。
     *
     * @param toolData 表示工具数据的 {@link ToolData}。
     * @param pluginToolDo 表示插件工具持久层的 {@link PluginToolDo}。
     * @return 表示插件工具数据信息的 {@link PluginToolData}。
     */
    public static PluginToolData toPluginToolData(ToolData toolData, PluginToolDo pluginToolDo) {
        return new PluginToolData.Builder()
                .creator(toolData.getCreator())
                .modifier(toolData.getModifier())
                .name(toolData.getName())
                .description(toolData.getDescription())
                .uniqueName(toolData.getUniqueName())
                .schema(toolData.getSchema())
                .runnables(toolData.getRunnables())
                .source(toolData.getSource())
                .icon(toolData.getIcon())
                .version(toolData.getVersion())
                .likeCount(pluginToolDo.getLikeCount())
                .downloadCount(pluginToolDo.getDownloadCount())
                .pluginId(pluginToolDo.getPluginId())
                .build();
    }
}
