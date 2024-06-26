/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.repository.pgsql.entity.PluginDo;
import com.huawei.jade.store.repository.pgsql.entity.TagDo;
import com.huawei.jade.store.repository.pgsql.mapper.PluginMapper;
import com.huawei.jade.store.repository.pgsql.mapper.TagMapper;
import com.huawei.jade.store.service.PluginService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 插件的 Http 请求的服务层实现。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
@Component
public class DefaultPluginService implements PluginService {
    private final ToolService toolService;
    private final PluginMapper pluginMapper;
    private final TagMapper tagMapper;

    /**
     * 通过工具服务接口来初始化 {@link DefaultPluginService} 的实例。
     *
     * @param toolService 表示持久层实例的 {@link ToolService}。
     * @param pluginMapper 表示持久层实例的 {@link PluginMapper}。
     * @param tagMapper 表示持久层实例的 {@link TagMapper}。
     */
    public DefaultPluginService(ToolService toolService, PluginMapper pluginMapper, TagMapper tagMapper) {
        this.toolService = toolService;
        this.pluginMapper = pluginMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    @Transactional
    public String addPlugin(PluginData pluginData) {
        PluginDo pluginDo = PluginDo.from(pluginData);
        String uniqueName = this.toolService.addTool(pluginData);
        pluginDo.setToolUniqueName(uniqueName);
        this.pluginMapper.addPlugin(pluginDo);
        Set<String> tagNames = pluginData.getTags();
        if (CollectionUtils.isNotEmpty(tagNames)) {
            tagNames.forEach(tagName -> this.tagMapper.addTag(new TagDo(uniqueName, tagName)));
        }
        return uniqueName;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public ListResult<PluginData> getPlugins(PluginQuery pluginQuery) {
        if (pluginQuery == null) {
            return ListResult.empty();
        }
        if (pluginQuery.getCollector() != null && !StringUtils.isBlank(pluginQuery.getCollector())) {
            List<PluginDo> list = this.pluginMapper.getMyCollection(pluginQuery);
            List<PluginData> data = getPluginDataList(list);
            pluginQuery.setLimit(null);
            pluginQuery.setOffset(null);
            int count = this.pluginMapper.getMyCollection(pluginQuery).size();
            return ListResult.create(data, count);
        }
        if ((pluginQuery.getPageNum() != null && pluginQuery.getPageNum() < 0) || (pluginQuery.getLimit() != null
                && pluginQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        Set<String> includeTags = pluginQuery.getIncludeTags();
        pluginQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = pluginQuery.getExcludeTags();
        pluginQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<PluginDo> dos = this.pluginMapper.getPlugins(pluginQuery);
        List<PluginData> data = getPluginDataList(dos);

        pluginQuery.setLimit(null);
        pluginQuery.setOffset(null);
        int count = this.pluginMapper.getPluginsCount(pluginQuery);
        return ListResult.create(data, count);
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public PluginData getPlugin(String toolUniqueName) {
        PluginDo pluginDo = this.pluginMapper.getPluginByUniqueName(toolUniqueName);
        ToolData toolData = this.toolService.getTool(toolUniqueName);
        PluginData pluginData = new PluginData(toolData,
                pluginDo.getIsPublished(), pluginDo.getOwner(), pluginDo.getLikeCount(), pluginDo.getDownloadCount());
        pluginData.setTags(tagMapper.getTags(pluginDo.getToolUniqueName())
                .stream()
                .map(TagDo::getName)
                .collect(Collectors.toSet()));
        return pluginData;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    @Transactional
    public String deletePlugin(String toolUniqueName) {
        this.pluginMapper.deletePlugin(toolUniqueName);
        this.tagMapper.deleteTagByUniqueName(toolUniqueName);
        this.toolService.deleteTool(toolUniqueName);
        return toolUniqueName;
    }

    private List<PluginData> getPluginDataList(List<PluginDo> list) {
        List<PluginData> data = new ArrayList<>();
        for (PluginDo pluginDo : list) {
            PluginData pluginData = new PluginData(this.toolService.getTool(pluginDo.getToolUniqueName()),
                    pluginDo.getIsPublished(), pluginDo.getOwner(),
                    pluginDo.getLikeCount(), pluginDo.getDownloadCount());
            pluginData.setTags(tagMapper.getTags(pluginDo.getToolUniqueName())
                    .stream()
                    .map(TagDo::getName)
                    .collect(Collectors.toSet()));
            data.add(pluginData);
        }
        return data;
    }
}
