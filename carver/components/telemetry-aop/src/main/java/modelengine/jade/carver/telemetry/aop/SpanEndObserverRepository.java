/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.util.LazyLoader;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 向 span 中增加系统内置事件。
 *
 * @author 方誉州
 * @since 2024-08-06
 */
@Component
public class SpanEndObserverRepository {
    private final BeanContainer container;
    private final LazyLoader<List<SpanEndObserver>> observers;

    public SpanEndObserverRepository(BeanContainer container) {
        this.container = Validation.notNull(container, "The container cannot be null.");
        this.observers = new LazyLoader<>(() -> this.container.all(SpanEndObserver.class).stream()
                .map(BeanFactory::<SpanEndObserver>get).collect(Collectors.toList()));
    }

    /**
     * 获取事件收集器。
     *
     * @return 表示事件收集器列表的 {@link List}{@code <}{@link SpanEndObserver}{@code >}。
     */
    public List<SpanEndObserver> get() {
        return this.observers.get();
    }
}
