/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import com.huawei.fit.jober.taskcenter.service.EventPublishService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.event.Event;
import modelengine.fitframework.plugin.Plugin;

/**
 * {@link EventPublishService} 的默认实现。
 *
 * @author 陈镕希
 * @since 2023-08-21
 */
@Component
public class EventPublishServiceImpl implements EventPublishService {
    private final Plugin plugin;

    public EventPublishServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void sendEvent(Event event) {
        this.plugin.runtime().publisherOfEvents().publishEvent(event);
    }

    @Override
    public Plugin plugin() {
        return this.plugin;
    }
}
