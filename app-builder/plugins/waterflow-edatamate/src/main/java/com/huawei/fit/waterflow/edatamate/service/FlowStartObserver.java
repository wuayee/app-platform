/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginStartedObserver;

/**
 * 流程引擎启动后修正之前数据
 *
 * @author 杨祥宇
 * @since 2023/12/30
 */
public class FlowStartObserver implements PluginStartedObserver {
    private static final Logger log = Logger.get(FlowStartObserver.class);

    @Override
    public void onPluginStarted(Plugin plugin) {
        if (!"jober".equals(plugin.metadata().name())) {
            return;
        }
        log.info("Plugin init, plugin name:{}", plugin.metadata().name());
    }
}
