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
import com.huawei.jade.store.entity.query.AppQuery;
import com.huawei.jade.store.entity.transfer.AppData;
import com.huawei.jade.store.repository.pgsql.entity.AppDo;
import com.huawei.jade.store.repository.pgsql.entity.TagDo;
import com.huawei.jade.store.repository.pgsql.repository.AppRepository;
import com.huawei.jade.store.repository.pgsql.repository.TagRepository;
import com.huawei.jade.store.service.AppService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应用的 Http 请求的服务层实现。
 *
 * @author 鲁为
 * @since 2024-06-19
 */
@Component
public class DefaultAppService implements AppService {
    private static final Logger logger = Logger.get(DefaultAppService.class);

    private final ToolService toolService;
    private final AppRepository appRepository;
    private final TagRepository tagRepository;

    /**
     * 通过应用服务接口来初始化 {@link DefaultAppService} 的实例。
     *
     * @param toolService 表示持久层实例的 {@link ToolService}。
     * @param appRepository 表示应用的仓库的 {@link AppRepository}。
     * @param tagRepository 表示标签仓库的 {@link TagRepository}。
     */
    public DefaultAppService(ToolService toolService, AppRepository appRepository, TagRepository tagRepository) {
        this.toolService = toolService;
        this.appRepository = appRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public String publishApp(AppData appData) {
        notNull(appData, "The app data cannot be null.");
        String uniqueName = appData.getUniqueName();
        if (StringUtils.isBlank(uniqueName)) {
            return this.addApp(appData);
        }
        return this.upgradeApp(appData, uniqueName);
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
        List<AppData> data = this.getAppDataList(appRepository.getApps(appQuery));

        appQuery.setLimit(null);
        appQuery.setOffset(null);
        return ListResult.create(data, this.appRepository.getAppsCount(appQuery));
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public AppData getApp(String toolUniqueName) {
        AppDo appDo = this.appRepository.getApp(toolUniqueName);
        ToolData toolData = this.toolService.getTool(toolUniqueName);
        AppData appData = new AppData(toolData,
                appDo.getLikeCount(), appDo.getDownloadCount());
        appData.setTags(this.tagRepository.getTags(appDo.getToolUniqueName())
                .stream()
                .map(TagDo::getName)
                .collect(Collectors.toSet()));
        return appData;
    }

    @Override
    @Fitable(id = "store-repository-pgsql")
    public String deleteApp(String toolUniqueName) {
        AppData oldAppData = this.getApp(toolUniqueName);
        try {
            this.appRepository.deleteApp(toolUniqueName);
            logger.info("Succeeded in deleting app and tags. [toolUniqueName={}]", toolUniqueName);
        } catch (FitException e) {
            logger.error("Failed to delete app and tags.");
            throw e;
        }
        try {
            this.toolService.deleteTool(toolUniqueName);
            logger.info("Succeeded in deleting existing tool. [toolUniqueName={}]", toolUniqueName);
        } catch (FitException e) {
            logger.error("Failed to delete existing tool.");
            // 回滚之前的数据库插入操作。
            this.appRepository.addApp(oldAppData);
            throw e;
        }
        return toolUniqueName;
    }

    private String addApp(AppData appData) {
        String uniqueName = UUID.randomUUID().toString();
        appData.setUniqueName(uniqueName);
        try {
            this.toolService.addTool(appData);
            logger.info("Succeeded in adding tool. [toolUniqueName={}]", appData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add tool.");
            throw e;
        }
        try {
            this.appRepository.addApp(appData);
            logger.info("Succeeded in adding app and tags. [toolUniqueName={}]", appData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add app and tags.");
            // 回滚之前的数据库插入操作。
            this.toolService.deleteToolByVersion(uniqueName, appData.getVersion());
            throw e;
        }
        return uniqueName;
    }

    private String upgradeApp(AppData appData, String uniqueName) {
        String oldVersion = this.toolService.getTool(uniqueName).getVersion();
        try {
            this.toolService.setNotLatest(uniqueName);
            logger.info("Succeeded in setting tool versions not latest. [toolUniqueName={}]", uniqueName);
        } catch (FitException e) {
            logger.error("Failed to set tool versions not latest.");
            throw e;
        }
        if (this.toolService.getToolByVersion(uniqueName, appData.getVersion()) != null) {
            try {
                this.toolService.setLatest(uniqueName, appData.getVersion());
                logger.info("Succeeded in updating existing tool version as the latest. [toolUniqueName={}]",
                        uniqueName);
            } catch (FitException e) {
                logger.error("Failed to update existing tool version as the latest.");
                // 回滚之前的数据库插入操作。
                this.toolService.setLatest(uniqueName, oldVersion);
                throw e;
            }
            return uniqueName;
        }
        try {
            this.toolService.addTool(appData);
            logger.info("Succeeded in adding tool. [toolUniqueName={}]", appData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add tool.");
            // 回滚之前的数据库插入操作。
            this.toolService.setLatest(uniqueName, oldVersion);
            throw e;
        }
        return uniqueName;
    }

    private List<AppData> getAppDataList(List<AppDo> list) {
        List<AppData> data = new ArrayList<>();
        for (AppDo appDo : list) {
            AppData appData = new AppData(this.toolService.getTool(appDo.getToolUniqueName()),
                    appDo.getLikeCount(), appDo.getDownloadCount());
            appData.setTags(this.tagRepository.getTags(appDo.getToolUniqueName())
                    .stream()
                    .map(TagDo::getName)
                    .collect(Collectors.toSet()));
            data.add(appData);
        }
        return data;
    }
}
