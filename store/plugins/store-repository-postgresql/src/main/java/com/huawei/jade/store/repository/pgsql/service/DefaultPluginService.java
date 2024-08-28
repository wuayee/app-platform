/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import static com.huawei.jade.store.repository.pgsql.entity.PluginDo.toPluginData;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.StringUtils;

import com.huawei.jade.carver.ListResult;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.repository.pgsql.entity.PluginDo;
import com.huawei.jade.store.repository.pgsql.repository.PluginRepository;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.service.PluginToolService;
import com.huawei.jade.store.service.support.DeployStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 插件的 Http 请求的服务层实现。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
@Component
public class DefaultPluginService implements PluginService {
    private static final Logger logger = Logger.get(DefaultPluginService.class);
    private static final String FITABLE_ID = "store-repository-pgsql";

    private final PluginRepository pluginRepository;
    private final PluginToolService pluginToolService;
    private final ObjectSerializer serializer;

    /**
     * 通过插件仓库、插件工具服务和序列化器来初始化 {@link DefaultPluginService} 的实例。
     *
     * @param pluginRepository 表示插件的仓库的 {@link PluginRepository}。
     * @param pluginToolService 表示插件工具的服务的 {@link PluginToolService}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     */
    public DefaultPluginService(PluginRepository pluginRepository, PluginToolService pluginToolService,
            ObjectSerializer serializer) {
        this.pluginRepository = pluginRepository;
        this.pluginToolService = pluginToolService;
        this.serializer = serializer;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public String addPlugin(PluginData pluginData) {
        String pluginId = this.pluginRepository.addPlugin(pluginData);
        List<PluginToolData> pluginToolDataList = pluginData.getPluginToolDataList();
        for (PluginToolData pluginToolData : pluginToolDataList) {
            pluginToolData.setPluginId(pluginId);
        }
        this.pluginToolService.addPluginTools(pluginToolDataList);
        return pluginId;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ListResult<PluginData> getPlugins(PluginQuery pluginQuery) {
        if (pluginQuery == null) {
            return ListResult.empty();
        }
        if ((pluginQuery.getOffset() != null && pluginQuery.getOffset() < 0) || (pluginQuery.getLimit() != null
                && pluginQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        Set<String> includeTags = pluginQuery.getIncludeTags();
        pluginQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = pluginQuery.getExcludeTags();
        pluginQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<PluginDo> pluginDos = this.pluginRepository.getPlugins(pluginQuery);
        List<PluginData> pluginDataList =
                pluginDos.stream().map(pluginDo -> toPluginData(pluginDo, serializer)).collect(Collectors.toList());

        pluginQuery.setLimit(null);
        pluginQuery.setOffset(null);
        int count = this.pluginRepository.getPluginsCount(pluginQuery);
        return ListResult.create(pluginDataList, count);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<PluginData> getPlugins(DeployStatus deployStatus) {
        List<PluginDo> pluginDos = this.pluginRepository.getPlugins(deployStatus);
        return pluginDos.stream()
                .map(pluginDo -> PluginDo.toPluginData(pluginDo, serializer))
                .collect(Collectors.toList());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public int getPluginsCount(DeployStatus deployStatus) {
        return this.pluginRepository.getPluginsCount(deployStatus);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public void updateDeployStatus(List<String> pluginIdList, DeployStatus deployStatus) {
        this.pluginRepository.updateDeployStatus(pluginIdList, deployStatus);
        logger.info("Succeed in updating deploy status. [deployStatus={}]", deployStatus);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public PluginData getPlugin(String pluginId) {
        PluginData pluginData = toPluginData(this.pluginRepository.getPluginByPluginId(pluginId), serializer);
        List<PluginToolData> pluginToolDataList = this.pluginToolService.getPluginTools(pluginId);
        pluginData.setPluginToolDataList(pluginToolDataList);
        return pluginData;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public String deletePlugin(String pluginId) {
        List<PluginToolData> pluginToolDataList = this.pluginToolService.getPluginTools(pluginId);
        for (PluginToolData pluginToolData : pluginToolDataList) {
            this.pluginToolService.deletePluginTool(pluginToolData.getUniqueName());
        }
        this.pluginRepository.deletePlugin(pluginId);
        return pluginId;
    }
}
