/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.factory;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.condition.AppQueryCondition;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AppBuilderApp 的工厂类
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@Component
public class AppBuilderAppFactory {
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final AppBuilderConfigRepository configRepository;
    private final AppBuilderFormRepository formRepository;
    private final AppBuilderConfigPropertyRepository configPropertyRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AppBuilderAppRepository appRepository;

    public AppBuilderAppFactory(AppBuilderFlowGraphRepository flowGraphRepository,
            AppBuilderConfigRepository configRepository, AppBuilderFormRepository formRepository,
            AppBuilderConfigPropertyRepository configPropertyRepository,
            AppBuilderFormPropertyRepository formPropertyRepository, AppBuilderAppRepository appRepository) {
        this.flowGraphRepository = flowGraphRepository;
        this.configRepository = configRepository;
        this.configPropertyRepository = configPropertyRepository;
        this.formRepository = formRepository;
        this.formPropertyRepository = formPropertyRepository;
        this.appRepository = appRepository;
    }

    /**
     * 创建一个新的AppBuilderApp对象。
     *
     * @return AppBuilderApp。
     */
    public AppBuilderApp create() {
        return new AppBuilderApp(this.flowGraphRepository,
                this.configRepository,
                this.formRepository,
                this.configPropertyRepository,
                this.formPropertyRepository);
    }

    /**
     * 根据appId创建一个新的AppBuilderApp对象。
     *
     * @param appId app的id。
     * @return AppBuilderApp。
     */
    public AppBuilderApp create(String appId) {
        AppBuilderApp appBuilderApp = this.appRepository.selectWithId(appId);
        if (appBuilderApp == null || StringUtils.isEmpty(appBuilderApp.getId())) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
        appBuilderApp.setFlowGraphRepository(this.flowGraphRepository);
        appBuilderApp.setConfigRepository(this.configRepository);
        appBuilderApp.setFormRepository(this.formRepository);
        appBuilderApp.setConfigPropertyRepository(this.configPropertyRepository);
        appBuilderApp.setFormPropertyRepository(this.formPropertyRepository);
        return appBuilderApp;
    }

    /**
     * 保存app
     *
     * @param appBuilderApp app
     */
    public void save(AppBuilderApp appBuilderApp) {
        this.appRepository.insertOne(appBuilderApp);
    }

    /**
     * 更新app
     *
     * @param appBuilderApp app
     */
    public void update(AppBuilderApp appBuilderApp) {
        this.appRepository.updateOne(appBuilderApp);
    }

    /**
     * 删除app
     *
     * @param appIds app标识列表
     */
    public void delete(List<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return;
        }
        AppQueryCondition condition = new AppQueryCondition();
        condition.setIds(appIds);
        List<AppBuilderApp> apps = this.appRepository.selectWithCondition(condition);
        List<String> configIds = this.getConfigIds(apps);
        if (CollectionUtils.isNotEmpty(configIds)) {
            this.configRepository.delete(configIds);
        }
        List<String> flowGraphIds = this.getFlowGraphIds(apps);
        this.flowGraphRepository.delete(flowGraphIds);
        this.appRepository.delete(appIds);
    }

    private List<String> getConfigIds(List<AppBuilderApp> apps) {
        return apps.stream().map(AppBuilderApp::getConfigId).collect(Collectors.toList());
    }

    private List<String> getFlowGraphIds(List<AppBuilderApp> apps) {
        return apps.stream().map(AppBuilderApp::getFlowGraphId).collect(Collectors.toList());
    }
}
