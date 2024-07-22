/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.repository.pgsql.entity.PluginDo;
import com.huawei.jade.store.repository.pgsql.entity.TagDo;
import com.huawei.jade.store.repository.pgsql.repository.PluginRepository;
import com.huawei.jade.store.repository.pgsql.repository.TagRepository;
import com.huawei.jade.store.service.PluginService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 插件的 Http 请求的服务层实现。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
@Component
public class DefaultPluginService implements PluginService {
    private static final Logger logger = Logger.get(DefaultPluginService.class);
    private final ToolService toolService;
    private final PluginRepository pluginRepository;
    private final TagRepository tagRepository;

    /**
     * 通过工具服务接口来初始化 {@link DefaultPluginService} 的实例。
     *
     * @param toolService 表示持久层实例的 {@link ToolService}。
     * @param pluginRepository 表示插件的仓库的 {@link PluginRepository}。
     * @param tagRepository 表示标签仓库的 {@link TagRepository}。
     */
    public DefaultPluginService(ToolService toolService, PluginRepository pluginRepository,
            TagRepository tagRepository) {
        this.toolService = toolService;
        this.pluginRepository = pluginRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public String addPlugin(PluginData pluginData) {
        notNull(pluginData, "The plugin data cannot be null.");
        String uniqueName = UUID.randomUUID().toString();
        pluginData.setUniqueName(uniqueName);
        try {
            this.toolService.addTool(pluginData);
            logger.info("Succeeded to add tool. [toolUniqueName={}]", pluginData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add tool.");
            throw e;
        }
        try {
            this.pluginRepository.addPlugin(pluginData);
            logger.info("Succeeded to add plugin and tags. [toolUniqueName={}]", pluginData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add plugin and tags.");
            // 回滚之前的数据库插入操作。
            String version = pluginData.getVersion();
            this.toolService.deleteToolByVersion(uniqueName, version == null ? "1.0.0" : version);
            throw e;
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
            List<PluginDo> list = this.pluginRepository.getMyCollection(pluginQuery);
            List<PluginData> data = getPluginDataList(list);
            pluginQuery.setLimit(null);
            pluginQuery.setOffset(null);
            int count = this.pluginRepository.getMyCollection(pluginQuery).size();
            return ListResult.create(data, count);
        }
        if ((pluginQuery.getOffset() != null && pluginQuery.getOffset() < 0) || (pluginQuery.getLimit() != null
                && pluginQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        Set<String> includeTags = pluginQuery.getIncludeTags();
        pluginQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = pluginQuery.getExcludeTags();
        pluginQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<PluginDo> dos = this.pluginRepository.getPlugins(pluginQuery);
        List<PluginData> data = this.getPluginDataList(dos);

        pluginQuery.setLimit(null);
        pluginQuery.setOffset(null);
        int count = this.pluginRepository.getPluginsCount(pluginQuery);
        return ListResult.create(data, count);
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public PluginData getPlugin(String toolUniqueName) {
        PluginDo pluginDo = this.pluginRepository.getPluginByUniqueName(toolUniqueName);
        ToolData toolData = this.toolService.getTool(toolUniqueName);
        PluginData pluginData = new PluginData(toolData,
                pluginDo.getIsPublished(), pluginDo.getOwner(), pluginDo.getLikeCount(), pluginDo.getDownloadCount());
        pluginData.setTags(this.tagRepository.getTags(pluginDo.getToolUniqueName())
                .stream()
                .map(TagDo::getName)
                .collect(Collectors.toSet()));
        return pluginData;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public String deletePlugin(String toolUniqueName) {
        PluginData oldPluginData = this.getPlugin(toolUniqueName);
        try {
            this.pluginRepository.deletePlugin(toolUniqueName);
            logger.info("Succeeded to delete plugin and tags. [toolUniqueName={}]", toolUniqueName);
        } catch (FitException e) {
            logger.error("Failed to delete plugin and tags.");
            throw e;
        }
        try {
            this.toolService.deleteTool(toolUniqueName);
            logger.info("Succeeded to delete existing tool. [toolUniqueName={}]", toolUniqueName);
        } catch (FitException e) {
            logger.error("Failed to delete existing tool.");
            // 回滚之前的数据库插入操作。
            this.pluginRepository.addPlugin(oldPluginData);
            throw e;
        }
        return toolUniqueName;
    }

    private List<PluginData> getPluginDataList(List<PluginDo> list) {
        List<PluginData> data = new ArrayList<>();
        for (PluginDo pluginDo : list) {
            PluginData pluginData = new PluginData(this.toolService.getTool(pluginDo.getToolUniqueName()),
                    pluginDo.getIsPublished(), pluginDo.getOwner(),
                    pluginDo.getLikeCount(), pluginDo.getDownloadCount());
            pluginData.setTags(this.tagRepository.getTags(pluginDo.getToolUniqueName())
                    .stream()
                    .map(TagDo::getName)
                    .collect(Collectors.toSet()));
            data.add(pluginData);
        }
        return data;
    }
}
