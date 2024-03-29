/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.support;

import com.huawei.fitframework.event.Event;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.event.EventPublisher;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.type.ParameterizedTypeResolver;
import com.huawei.fitframework.type.ParameterizedTypeResolvingResult;
import com.huawei.fitframework.type.TypeMatcher;

import java.util.List;

/**
 * 为插件提供事件发布程序。
 *
 * @author 梁济时 l00815032
 * @since 2023-01-29
 */
final class PluginEventPublisher implements EventPublisher {
    private final Plugin plugin;

    PluginEventPublisher(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <E extends Event> void publishEvent(E event) {
        RuntimeException exception = null;
        List<BeanFactory> factories = this.plugin.container().factories(EventHandler.class);
        for (BeanFactory factory : factories) {
            if (handleable(factory, event)) {
                EventHandler<E> handler = factory.get();
                try {
                    handler.handleEvent(event);
                } catch (RuntimeException ex) {
                    if (exception == null) {
                        exception = ex;
                    } else {
                        exception.addSuppressed(ex);
                    }
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private static <E extends Event> boolean handleable(BeanFactory factory, E event) {
        ParameterizedTypeResolvingResult result = ParameterizedTypeResolver.resolve(
                factory.metadata().type(), EventHandler.class);
        return result.resolved() && TypeMatcher.match(event.getClass(), result.parameters().get(0));
    }
}
