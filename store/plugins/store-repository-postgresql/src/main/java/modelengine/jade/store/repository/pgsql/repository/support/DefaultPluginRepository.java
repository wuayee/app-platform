/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.repository.support;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.repository.pgsql.entity.PluginDo;
import modelengine.jade.store.repository.pgsql.mapper.PluginMapper;
import modelengine.jade.store.repository.pgsql.repository.PluginRepository;
import modelengine.jade.store.service.support.DeployStatus;

import java.util.List;

/**
 * 插件的仓库。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
@Component
public class DefaultPluginRepository implements PluginRepository {
    private final PluginMapper pluginMapper;

    private final ObjectSerializer serializer;

    /**
     * 通过仓库持久层和序列化器来初始化 {@link DefaultPluginRepository} 的实例。
     *
     * @param pluginMapper 表示仓库持久层的 {@link PluginMapper}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     */
    public DefaultPluginRepository(PluginMapper pluginMapper, @Fit(alias = "json") ObjectSerializer serializer) {
        this.pluginMapper = pluginMapper;
        this.serializer = serializer;
    }

    @Override
    @Transactional
    public String addPlugin(PluginData pluginData) {
        this.pluginMapper.addPlugin(PluginDo.fromPluginData(pluginData, serializer));
        return pluginData.getPluginId();
    }

    @Override
    @Transactional
    public void deletePlugin(String pluginId) {
        this.pluginMapper.deletePlugin(pluginId);
    }

    @Override
    public List<PluginDo> getPlugins(PluginQuery pluginQuery) {
        return this.pluginMapper.getPlugins(pluginQuery);
    }

    @Override
    public int getPluginsCount(PluginQuery pluginQuery) {
        return this.pluginMapper.getPluginsCount(pluginQuery);
    }

    @Override
    public PluginDo getPluginByPluginId(String pluginId) {
        return this.pluginMapper.getPluginByPluginId(pluginId);
    }

    @Override
    public List<PluginDo> getPlugins(DeployStatus deployStatus) {
        return this.pluginMapper.getPluginsByDeployStatus(deployStatus);
    }

    @Override
    public int getPluginsCount(DeployStatus deployStatus) {
        return this.pluginMapper.getPluginsCountByDeployStatus(deployStatus);
    }

    @Override
    @Transactional
    public void updateDeployStatus(List<String> pluginIdList, DeployStatus deployStatus) {
        this.pluginMapper.updateDeployStatus(pluginIdList, deployStatus);
    }
}
