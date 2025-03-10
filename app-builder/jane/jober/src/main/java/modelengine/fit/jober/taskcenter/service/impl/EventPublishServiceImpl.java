/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.impl;

import modelengine.fit.jober.taskcenter.service.EventPublishService;

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
