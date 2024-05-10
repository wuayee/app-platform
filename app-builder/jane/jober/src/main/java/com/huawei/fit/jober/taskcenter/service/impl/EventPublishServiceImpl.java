/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import com.huawei.fit.jober.taskcenter.service.EventPublishService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.event.Event;
import com.huawei.fitframework.plugin.Plugin;

/**
 * {@link EventPublishService} 的默认实现。
 *
 * @author 陈镕希 c00572808
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
