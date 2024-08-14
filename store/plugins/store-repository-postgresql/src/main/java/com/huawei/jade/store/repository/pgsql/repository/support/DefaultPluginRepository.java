/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.repository.pgsql.entity.PluginDo;
import com.huawei.jade.store.repository.pgsql.mapper.PluginMapper;
import com.huawei.jade.store.repository.pgsql.repository.PluginRepository;
import com.huawei.jade.store.service.support.DeployStatus;

import java.util.List;

/**
 * 插件的仓库。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
@Component
public class DefaultPluginRepository implements PluginRepository {
    private static final Logger logger = Logger.get(DefaultPluginRepository.class);

    private final PluginMapper pluginMapper;

    private final ObjectSerializer serializer;

    /**
     * 通过仓库持久层和序列化器来初始化 {@link DefaultPluginRepository} 的实例。
     *
     * @param pluginMapper 表示仓库持久层的 {@link PluginMapper}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     */
    public DefaultPluginRepository(PluginMapper pluginMapper, ObjectSerializer serializer) {
        this.pluginMapper = pluginMapper;
        this.serializer = serializer;
    }

    @Override
    public String addPlugin(PluginData pluginData) {
        this.pluginMapper.addPlugin(PluginDo.fromPluginData(pluginData, serializer));
        return pluginData.getPluginId();
    }

    @Override
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
    public void updateDeployStatus(List<String> pluginIdList, DeployStatus deployStatus) {
        this.pluginMapper.updateDeployStatus(pluginIdList, deployStatus);
    }
}
