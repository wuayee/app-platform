/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.repository.pgsql.entity.PluginDo;
import com.huawei.jade.store.repository.pgsql.mapper.PluginMapper;
import com.huawei.jade.store.repository.pgsql.repository.PluginRepository;
import com.huawei.jade.store.repository.pgsql.repository.TagRepository;

import java.util.List;

/**
 * 插件的仓库。
 *
 * @author 鲁为 l00839724
 * @since 2024-07-18
 */
@Component
public class DefaultPluginRepository implements PluginRepository {
    private final PluginMapper pluginMapper;
    private final TagRepository tagRepository;

    /**
     * 通过 Mapper 来初始化 {@link DefaultPluginRepository} 的实例。
     *
     * @param pluginMapper 表示持久层实例的 {@link PluginMapper}。
     * @param tagRepository 表示持久层实例的 {@link TagRepository}。
     */
    public DefaultPluginRepository(PluginMapper pluginMapper, TagRepository tagRepository) {
        this.pluginMapper = pluginMapper;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void addPlugin(PluginData pluginData) {
        PluginDo pluginDo = PluginDo.from(pluginData);
        this.pluginMapper.addPlugin(pluginDo);
        this.tagRepository.addTags(pluginData.getTags(), pluginData.getUniqueName());
    }

    @Override
    @Transactional
    public void deletePlugin(String toolUniqueName) {
        this.pluginMapper.deletePlugin(toolUniqueName);
        this.tagRepository.deleteTagByUniqueName(toolUniqueName);
    }

    @Override
    public List<PluginDo> getMyCollection(PluginQuery pluginQuery) {
        return this.pluginMapper.getMyCollection(pluginQuery);
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
    public PluginDo getPluginByUniqueName(String toolUniqueName) {
        return this.pluginMapper.getPluginByUniqueName(toolUniqueName);
    }
}
