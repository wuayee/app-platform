/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository.support;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;

import com.huawei.jade.store.entity.query.PluginToolQuery;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.repository.pgsql.entity.PluginToolDo;
import com.huawei.jade.store.repository.pgsql.mapper.PluginToolMapper;
import com.huawei.jade.store.repository.pgsql.repository.PluginToolRepository;
import com.huawei.jade.store.repository.pgsql.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件工具的仓库。
 *
 * @author 鲁为
 * @since 2024-07-18
 */
@Component
public class DefaultPluginToolRepository implements PluginToolRepository {
    private final PluginToolMapper pluginToolMapper;
    private final TagRepository tagRepository;

    /**
     * 通过 Mapper 来初始化 {@link DefaultPluginToolRepository} 的实例。
     *
     * @param pluginToolMapper 表示持久层实例的 {@link PluginToolMapper}。
     * @param tagRepository 表示持久层实例的 {@link TagRepository}。
     */
    public DefaultPluginToolRepository(PluginToolMapper pluginToolMapper, TagRepository tagRepository) {
        this.pluginToolMapper = pluginToolMapper;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void addPluginTool(PluginToolData pluginToolData) {
        PluginToolDo pluginToolDo = PluginToolDo.fromPluginToolData(pluginToolData);
        this.pluginToolMapper.addPluginTool(pluginToolDo);
        this.tagRepository.addTags(pluginToolData.getTags(), pluginToolData.getUniqueName());
    }

    @Override
    @Transactional
    public void addPluginTools(List<PluginToolData> pluginToolDataList) {
        List<PluginToolDo> pluginToolDoList = pluginToolDataList
                .stream()
                .map(PluginToolDo::fromPluginToolData)
                .collect(Collectors.toList());
        this.pluginToolMapper.addPluginTools(pluginToolDoList);
        for (PluginToolData pluginToolData : pluginToolDataList) {
            this.tagRepository.addTags(pluginToolData.getTags(), pluginToolData.getUniqueName());
        }
    }

    @Override
    @Transactional
    public void deletePluginTool(String toolUniqueName) {
        this.pluginToolMapper.deletePluginTool(toolUniqueName);
        this.tagRepository.deleteTagByUniqueName(toolUniqueName);
    }

    @Override
    public List<PluginToolDo> getPluginTools(PluginToolQuery pluginToolQuery) {
        return this.pluginToolMapper.getPluginTools(pluginToolQuery);
    }

    @Override
    public List<PluginToolDo> getPluginTools(String pluginId) {
        return this.pluginToolMapper.getPluginToolsByPluginId(pluginId);
    }

    @Override
    public int getPluginToolsCount(PluginToolQuery pluginToolQuery) {
        return this.pluginToolMapper.getPluginToolsCount(pluginToolQuery);
    }

    @Override
    public PluginToolDo getPluginToolByUniqueName(String toolUniqueName) {
        return this.pluginToolMapper.getPluginToolByUniqueName(toolUniqueName);
    }
}
