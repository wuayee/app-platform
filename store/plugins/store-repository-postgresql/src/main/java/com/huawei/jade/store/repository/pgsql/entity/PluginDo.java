/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import static com.huawei.jade.carver.util.SerializeUtils.json2obj;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.support.DeployStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存入插件数据库的实体类。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginDo extends CommonDo {
    /**
     * 表示插件的唯一标识。
     */
    private String pluginId;

    /**
     * 表示插件的名字。
     */
    private String pluginName;

    /**
     * 表示插件的扩展。
     */
    private String extension;

    /**
     * 表示插件的部署状态。
     */
    private DeployStatus deployStatus;

    /**
     * 用传输层的插件数据 {@link PluginData} 构造 {@link PluginDo}。
     *
     * @param pluginData 表示传输层的插件数据的 {@link PluginData}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @return 表示数据库层的插件数据的 {@link PluginDo}。
     */
    public static PluginDo fromPluginData(PluginData pluginData, ObjectSerializer serializer) {
        PluginDo pluginDo = new PluginDo();
        if (pluginData == null) {
            return pluginDo;
        }
        pluginDo.setPluginId(pluginData.getPluginId());
        pluginDo.setPluginName(pluginData.getPluginName());
        pluginDo.setExtension(serializer.serialize(pluginData.getExtension()));
        pluginDo.setDeployStatus(DeployStatus.from(pluginData.getDeployStatus()));
        return pluginDo;
    }

    /**
     * 用数据库持久层 {@link PluginDo} 构造传输层的插件数据 {@link PluginData}。
     *
     * @param pluginDo 表示持久层的插件数据的 {@link PluginDo}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @return 表示传输层的插件数据的 {@link PluginData}。
     */
    public static PluginData toPluginData(PluginDo pluginDo, ObjectSerializer serializer) {
        PluginData pluginData = new PluginData();
        if (pluginDo == null) {
            return pluginData;
        }
        pluginData.setCreator(pluginDo.getCreator());
        pluginData.setModifier(pluginDo.getModifier());
        pluginData.setPluginId(pluginDo.getPluginId());
        pluginData.setPluginName(pluginDo.getPluginName());
        pluginData.setExtension(json2obj(pluginDo.getExtension(), serializer));
        if (pluginDo.getDeployStatus() != null) {
            pluginData.setDeployStatus(pluginDo.getDeployStatus().toString());
        }
        return pluginData;
    }
}
