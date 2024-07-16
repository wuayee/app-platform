/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.service;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.store.entity.query.AppQuery;
import com.huawei.jade.store.entity.transfer.AppData;
import com.huawei.jade.store.repository.pgsql.entity.AppDo;
import com.huawei.jade.store.repository.pgsql.entity.TagDo;
import com.huawei.jade.store.repository.pgsql.mapper.AppMapper;
import com.huawei.jade.store.repository.pgsql.mapper.TagMapper;
import com.huawei.jade.store.service.AppService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应用的 Http 请求的服务层实现。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-19
 */
@Component
public class DefaultAppService implements AppService {
    private final ToolService toolService;
    private final AppMapper appMapper;
    private final TagMapper tagMapper;

    /**
     * 通过应用服务接口来初始化 {@link DefaultAppService} 的实例。
     *
     * @param toolService 表示持久层实例的 {@link ToolService}。
     * @param appMapper 表示持久层实例的 {@link AppMapper}。
     * @param tagMapper 表示持久层实例的 {@link TagMapper}。
     */
    public DefaultAppService(ToolService toolService, AppMapper appMapper, TagMapper tagMapper) {
        this.toolService = toolService;
        this.appMapper = appMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    @Transactional
    public String publishApp(AppData appData) {
        notNull(appData, "The app data cannot be null.");
        if (StringUtils.isBlank(appData.getUniqueName())) {
            appData.setUniqueName(UUID.randomUUID().toString());
            return this.addApp(appData);
        }
        if (this.toolService.getToolByVersion(appData.getUniqueName(), appData.getVersion()) != null) {
            this.toolService.deleteToolByVersion(appData.getUniqueName(), appData.getVersion());
        }
        this.toolService.setNotLatest(appData.getUniqueName());
        return this.addApp(appData);
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public ListResult<AppData> getApps(AppQuery appQuery) {
        if (appQuery == null) {
            return ListResult.empty();
        }
        if ((appQuery.getOffset() != null && appQuery.getOffset() < 0) || (appQuery.getLimit() != null
                && appQuery.getLimit() < 0)) {
            return ListResult.empty();
        }
        Set<String> includeTags = appQuery.getIncludeTags();
        appQuery.setIncludeTags(includeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        Set<String> excludeTags = appQuery.getExcludeTags();
        appQuery.setExcludeTags(excludeTags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet()));
        List<AppDo> dos = this.appMapper.getApps(appQuery);
        List<AppData> data = getAppDataList(dos);

        appQuery.setLimit(null);
        appQuery.setOffset(null);
        int count = this.appMapper.getAppsCount(appQuery);
        return ListResult.create(data, count);
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public AppData getApp(String toolUniqueName) {
        AppDo appDo = this.appMapper.getAppByUniqueName(toolUniqueName);
        ToolData toolData = this.toolService.getTool(toolUniqueName);
        AppData appData = new AppData(toolData,
                appDo.getLikeCount(), appDo.getDownloadCount());
        appData.setTags(tagMapper.getTags(appDo.getToolUniqueName())
                .stream()
                .map(TagDo::getName)
                .collect(Collectors.toSet()));
        return appData;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    @Transactional
    public String deleteApp(String toolUniqueName) {
        this.appMapper.deleteApp(toolUniqueName);
        this.tagMapper.deleteTagByUniqueName(toolUniqueName);
        this.toolService.deleteTool(toolUniqueName);
        return toolUniqueName;
    }

    private List<AppData> getAppDataList(List<AppDo> list) {
        List<AppData> data = new ArrayList<>();
        for (AppDo appDo : list) {
            AppData appData = new AppData(this.toolService.getTool(appDo.getToolUniqueName()),
                    appDo.getLikeCount(), appDo.getDownloadCount());
            appData.setTags(tagMapper.getTags(appDo.getToolUniqueName())
                    .stream()
                    .map(TagDo::getName)
                    .collect(Collectors.toSet()));
            data.add(appData);
        }
        return data;
    }

    private String addApp(AppData appData) {
        AppDo appDo = AppDo.from(appData);
        String uniqueName = this.toolService.addTool(appData);
        if (this.appMapper.getAppByUniqueName(appDo.getToolUniqueName()) != null) {
            return uniqueName;
        }
        this.appMapper.addApp(appDo);
        Set<String> tagNames = appData.getTags().stream().map(StringUtils::toUpperCase).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(tagNames)) {
            tagNames.forEach(tagName -> this.tagMapper.addTag(new TagDo(uniqueName, tagName)));
        }
        return uniqueName;
    }
}
