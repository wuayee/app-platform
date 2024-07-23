/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.jade.store.entity.query.AppQuery;
import com.huawei.jade.store.entity.transfer.AppData;
import com.huawei.jade.store.repository.pgsql.entity.AppDo;
import com.huawei.jade.store.repository.pgsql.mapper.AppMapper;
import com.huawei.jade.store.repository.pgsql.repository.AppRepository;
import com.huawei.jade.store.repository.pgsql.repository.TagRepository;

import java.util.List;

/**
 * 应用的仓库。
 *
 * @author 鲁为 l00839724
 * @since 2024-07-12
 */
@Component
public class DefaultAppRepository implements AppRepository {
    private final AppMapper appMapper;
    private final TagRepository tagRepository;

    /**
     * 通过 Mapper 来初始化 {@link DefaultAppRepository} 的实例。
     *
     * @param appMapper 表示持久层实例的 {@link AppMapper}。
     * @param tagRepository 表示持久层实例的 {@link TagRepository}。
     */
    public DefaultAppRepository(AppMapper appMapper, TagRepository tagRepository) {
        this.appMapper = appMapper;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void addApp(AppData appData) {
        AppDo appDo = AppDo.from(appData);
        this.appMapper.addApp(appDo);
        this.tagRepository.addTags(appData.getTags(), appData.getUniqueName());
    }

    @Override
    public List<AppDo> getApps(AppQuery appQuery) {
        return this.appMapper.getApps(appQuery);
    }

    @Override
    public int getAppsCount(AppQuery appQuery) {
        return this.appMapper.getAppsCount(appQuery);
    }

    @Override
    public AppDo getApp(String toolUniqueName) {
        return this.appMapper.getAppByUniqueName(toolUniqueName);
    }

    @Override
    @Transactional
    public void deleteApp(String toolUniqueName) {
        this.appMapper.deleteApp(toolUniqueName);
        this.tagRepository.deleteTagByUniqueName(toolUniqueName);
    }
}
