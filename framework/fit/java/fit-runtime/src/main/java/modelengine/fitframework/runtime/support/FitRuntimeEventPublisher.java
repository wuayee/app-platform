/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.runtime.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.event.Event;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;

import java.util.List;

/**
 * 为 {@link FitRuntime} 提供事件发布程序。
 *
 * @author 梁济时
 * @since 2023-01-31
 */
final class FitRuntimeEventPublisher implements EventPublisher {
    private final FitRuntime runtime;

    FitRuntimeEventPublisher(FitRuntime runtime) {
        this.runtime = notNull(runtime, "The FIT runtime of event publisher cannot be null.");
    }

    @Override
    public <E extends Event> void publishEvent(E event) {
        List<Plugin> plugins = this.runtime.plugins();
        RuntimeException exception = null;
        for (Plugin plugin : plugins) {
            try {
                plugin.publisherOfEvents().publishEvent(event);
            } catch (RuntimeException ex) {
                if (exception == null) {
                    exception = ex;
                } else {
                    exception.addSuppressed(ex);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
}
