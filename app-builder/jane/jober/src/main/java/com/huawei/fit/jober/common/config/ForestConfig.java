/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.config;

import com.huawei.fitframework.annotation.Component;

import com.dtflys.forest.config.ForestConfiguration;

/**
 * 初始化设置Forest不打印日志。
 *
 * @author 陈镕希 c00572808
 * @since 2023-09-01
 */
@Component("JoberForestConfig")
public class ForestConfig {
    ForestConfig() {
        ForestConfiguration configuration = ForestConfiguration.getDefaultConfiguration();
        configuration.setLogHandler(new DefaultForestLogHandler());
    }
}
