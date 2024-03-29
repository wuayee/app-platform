/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.ConfigurableFitable;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.FitableFactory;
import com.huawei.fitframework.broker.LoadBalancer;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.ioc.BeanContainer;

/**
 * 表示 {@link FitableFactory} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-24
 */
public class DefaultFitableFactory implements FitableFactory {
    private final BeanContainer container;
    private final LoadBalancer loadBalancer;

    public DefaultFitableFactory(BeanContainer container, LoadBalancer loadBalancer) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.loadBalancer = notNull(loadBalancer, "The load balancer cannot be null.");
    }

    @Override
    public ConfigurableFitable create(String id, String version) {
        return new DefaultFitable(this.container, this.loadBalancer, id, version);
    }

    @Override
    public ConfigurableFitable create(UniqueFitableId id) {
        notNull(id, "The unique fitable id cannot be null.");
        return this.create(id.fitableId(), id.fitableVersion());
    }

    @Override
    public ConfigurableFitable create(Fitable fitable) {
        notNull(fitable, "The fitable cannot be null.");
        return this.create(fitable.id(), fitable.version())
                .aliases(fitable.aliases().all())
                .tags(fitable.tags().all())
                .degradationFitableId(fitable.degradationFitableId())
                .genericable(fitable.genericable());
    }
}
