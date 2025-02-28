/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.repository.support;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.store.entity.query.AppQuery;
import modelengine.jade.store.entity.transfer.AppPublishData;
import modelengine.jade.store.repository.pgsql.entity.AppDo;
import modelengine.jade.store.repository.pgsql.mapper.AppMapper;
import modelengine.jade.store.repository.pgsql.repository.AppRepository;

import java.util.List;

/**
 * 应用的仓库。
 *
 * @author 鲁为
 * @since 2024-07-12
 */
@Component
public class DefaultAppRepository implements AppRepository {
    private final AppMapper appMapper;

    /**
     * 通过 Mapper 来初始化 {@link DefaultAppRepository} 的实例。
     *
     * @param appMapper 表示持久层实例的 {@link AppMapper}。
     */
    public DefaultAppRepository(AppMapper appMapper) {
        this.appMapper = appMapper;
    }

    @Override
    @Transactional
    public void addApp(AppPublishData appData) {
        AppDo appDo = AppDo.from(appData);
        this.appMapper.addApp(appDo);
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
    }
}
