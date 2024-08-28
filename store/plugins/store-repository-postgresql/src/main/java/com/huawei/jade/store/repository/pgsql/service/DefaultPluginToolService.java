/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;
import static com.huawei.jade.store.repository.pgsql.entity.PluginToolDo.toPluginToolData;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.store.entity.query.PluginToolQuery;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.repository.pgsql.entity.PluginToolDo;
import com.huawei.jade.store.repository.pgsql.entity.TagDo;
import com.huawei.jade.store.repository.pgsql.repository.PluginToolRepository;
import com.huawei.jade.store.repository.pgsql.repository.TagRepository;
import com.huawei.jade.store.service.PluginToolService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 插件工具的 Http 请求的服务层实现。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
@Component
public class DefaultPluginToolService implements PluginToolService {
    private static final Logger logger = Logger.get(DefaultPluginToolService.class);
    private final ToolService toolService;
    private final PluginToolRepository pluginToolRepository;
    private final TagRepository tagRepository;

    /**
     * 通过工具服务接口来初始化 {@link DefaultPluginToolService} 的实例。
     *
     * @param toolService 表示持久层实例的 {@link ToolService}。
     * @param pluginToolRepository 表示插件工具的仓库的 {@link PluginToolRepository}。
     * @param tagRepository 表示标签仓库的 {@link TagRepository}。
     */
    public DefaultPluginToolService(ToolService toolService, PluginToolRepository pluginToolRepository,
            TagRepository tagRepository) {
        this.toolService = toolService;
        this.pluginToolRepository = pluginToolRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public String addPluginTool(PluginToolData pluginToolData) {
        notNull(pluginToolData, "The plugin data cannot be null.");
        String uniqueName = UUID.randomUUID().toString();
        pluginToolData.setUniqueName(uniqueName);
        try {
            this.toolService.addTool(pluginToolData);
            logger.info("Succeeded to add tool. [toolUniqueName={}]", pluginToolData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add tool.");
            throw e;
        }
        try {
            this.pluginToolRepository.addPluginTool(pluginToolData);
            logger.info("Succeeded to add plugin tools and tags. [toolUniqueName={}]", pluginToolData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add plugin tools and tags.");
            // 回滚之前的数据库插入操作。
            String version = pluginToolData.getVersion();
            this.toolService.deleteToolByVersion(uniqueName, version == null ? "1.0.0" : version);
            throw e;
        }
        return uniqueName;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public List<String> addPluginTools(List<PluginToolData> pluginToolDataList) {
        notNull(pluginToolDataList, "The plugin data list cannot be null.");
        List<String> uniqueNames = new ArrayList<>();
        for (PluginToolData pluginToolData : pluginToolDataList) {
            String uniqueName = UUID.randomUUID().toString();
            pluginToolData.setUniqueName(uniqueName);
            uniqueNames.add(uniqueName);
        }
        List<ToolData> toolDataList = new ArrayList<>(pluginToolDataList);
        try {
            this.toolService.addTools(toolDataList);
            logger.info("Succeeded to add tools. [toolUniqueNames={}]", uniqueNames);
        } catch (FitException e) {
            logger.error("Failed to add tools.");
            throw e;
        }
        try {
            this.pluginToolRepository.addPluginTools(pluginToolDataList);
            logger.info("Succeeded to add plugin tools and tags. [toolUniqueNames={}]", uniqueNames);
        } catch (FitException e) {
            logger.error("Failed to add plugin tools and tags.");
            // 回滚之前的数据库插入操作。
            this.toolService.deleteTools(uniqueNames);
            throw e;
        }
        return uniqueNames;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public ListResult<PluginToolData> getPluginTools(PluginToolQuery pluginToolQuery) {
        if (pluginToolQuery == null) {
            return ListResult.empty();
        }
        if ((pluginToolQuery.getOffset() != null && pluginToolQuery.getOffset() < 0)
                || (pluginToolQuery.getLimit() != null && pluginToolQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        Set<String> includeTags = pluginToolQuery.getIncludeTags();
        pluginToolQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = pluginToolQuery.getExcludeTags();
        pluginToolQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<PluginToolDo> dos = this.pluginToolRepository.getPluginTools(pluginToolQuery);
        List<PluginToolData> data = this.getPluginToolDataList(dos);

        pluginToolQuery.setLimit(null);
        pluginToolQuery.setOffset(null);
        int count = this.pluginToolRepository.getPluginToolsCount(pluginToolQuery);
        return ListResult.create(data, count);
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public List<PluginToolData> getPluginTools(String pluginId) {
        List<PluginToolDo> dos = this.pluginToolRepository.getPluginTools(pluginId);
        return this.getPluginToolDataList(dos);
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public PluginToolData getPluginTool(String toolUniqueName) {
        PluginToolDo pluginToolDo = this.pluginToolRepository.getPluginToolByUniqueName(toolUniqueName);
        ToolData toolData = this.toolService.getTool(toolUniqueName);
        PluginToolData pluginToolData = toPluginToolData(toolData, pluginToolDo);
        pluginToolData.setTags(this.tagRepository.getTags(pluginToolDo.getToolUniqueName())
                .stream()
                .map(TagDo::getName)
                .collect(Collectors.toSet()));
        return pluginToolData;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public String deletePluginTool(String toolUniqueName) {
        PluginToolData oldPluginToolData = this.getPluginTool(toolUniqueName);
        try {
            this.pluginToolRepository.deletePluginTool(toolUniqueName);
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
            this.pluginToolRepository.addPluginTool(oldPluginToolData);
            throw e;
        }
        return toolUniqueName;
    }

    private List<PluginToolData> getPluginToolDataList(List<PluginToolDo> list) {
        List<PluginToolData> data = new ArrayList<>();
        for (PluginToolDo pluginToolDo : list) {
            ToolData toolData = this.toolService.getTool(pluginToolDo.getToolUniqueName());
            PluginToolData pluginToolData = toPluginToolData(toolData, pluginToolDo);
            pluginToolData.setTags(this.tagRepository.getTags(pluginToolDo.getToolUniqueName())
                    .stream()
                    .map(TagDo::getName)
                    .collect(Collectors.toSet()));
            data.add(pluginToolData);
        }
        return data;
    }
}
