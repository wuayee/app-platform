/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.domain;

import com.huawei.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fitframework.inspection.Validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Slf4j
public class AppBuilderApp extends BaseDomain {
    private String id;
    private String name;
    private String tenantId;
    private String configId;
    private String flowGraphId;
    private String type;
    private String version;
    private Map<String, Object> attributes;
    private String state;
    private AppBuilderFlowGraph flowGraph;
    private AppBuilderConfig config;
    private AppBuilderFlowGraphRepository flowGraphRepository;
    private AppBuilderConfigRepository configRepository;
    private AppBuilderConfigPropertyRepository configPropertyRepository;
    private AppBuilderFormRepository formRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;

    public AppBuilderApp(AppBuilderFlowGraphRepository flowGraphRepository, AppBuilderConfigRepository configRepository,
            AppBuilderFormRepository formRepository, AppBuilderConfigPropertyRepository configPropertyRepository,
            AppBuilderFormPropertyRepository formPropertyRepository) {
        this.flowGraphRepository = flowGraphRepository;
        this.configRepository = configRepository;
        this.formRepository = formRepository;
        this.configPropertyRepository = configPropertyRepository;
        this.formPropertyRepository = formPropertyRepository;
    }

    public AppBuilderFlowGraph getFlowGraph() {
        return lazyGet(this.flowGraph, this::loadFlowGraph, this::setFlowGraph);
    }

    private AppBuilderFlowGraph loadFlowGraph() {
        Validation.notNull(this.flowGraphId, "App builder config can not be null.");
        return this.flowGraphRepository.selectWithId(this.flowGraphId);
    }

    public AppBuilderConfig getConfig() {
        return lazyGet(this.config, this::loadConfig, this::setConfig);
    }

    private AppBuilderConfig loadConfig() {
        Validation.notNull(this.configId, "App builder config can not be null.");
        AppBuilderConfig appBuilderConfig = this.configRepository.selectWithId(this.configId);
        Validation.notNull(appBuilderConfig, "App builder config can not be null.");
        appBuilderConfig.setFormRepository(this.formRepository);
        appBuilderConfig.setFormPropertyRepository(this.formPropertyRepository);
        appBuilderConfig.setConfigPropertyRepository(this.configPropertyRepository);
        return appBuilderConfig;
    }
}
