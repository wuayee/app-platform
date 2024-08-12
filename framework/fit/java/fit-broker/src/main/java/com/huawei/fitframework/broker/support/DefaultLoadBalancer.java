/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.client.Client;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.broker.LoadBalancer;
import com.huawei.fitframework.broker.SerializationService;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.broker.TargetLocator;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.TargetNotFoundException;
import com.huawei.fitframework.broker.client.filter.loadbalance.EnvironmentFilter;
import com.huawei.fitframework.broker.client.filter.loadbalance.FirstMatchedEnvironmentFilter;
import com.huawei.fitframework.broker.client.filter.loadbalance.ProtocolAndFormatSupportedFilter;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link LoadBalancer} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-28
 */
public class DefaultLoadBalancer implements LoadBalancer {
    private static final Logger log = Logger.get(DefaultLoadBalancer.class);

    private final BeanContainer container;
    private final SerializationService serializationService;
    private final TargetLocator targetLocator;

    public DefaultLoadBalancer(BeanContainer container, SerializationService serializationService,
            TargetLocator targetLocator) {
        this.container = notNull(container, "The bean container cannot be null.");
        this.serializationService = notNull(serializationService, "The serialization service cannot be null.");
        this.targetLocator = notNull(targetLocator, "The target locator cannot be null.");
    }

    @Override
    public List<Target> balance(Fitable fitable, InvocationContext context, Object[] args) {
        Invoker.Filter filter = Invoker.Filter.combine(this.getFirstMatchedEnvironmentFilter(fitable, context),
                this.getProtocolAndFormatSupportedFilter(context),
                context.loadBalanceFilter());
        return this.filterCandidateTargets(filter, fitable, context, this.getTargets(fitable.toUniqueId()));
    }

    private Invoker.Filter getFirstMatchedEnvironmentFilter(Fitable fitable, InvocationContext context) {
        if (context.specifiedEnvironment() != null) {
            log.debug("Environment is specified. [id={}, environment={}]",
                    fitable.toUniqueId(),
                    context.specifiedEnvironment());
            return new EnvironmentFilter(context.specifiedEnvironment());
        }
        return new FirstMatchedEnvironmentFilter(context.environmentPrioritySequence());
    }

    private Invoker.Filter getProtocolAndFormatSupportedFilter(InvocationContext context) {
        return new ProtocolAndFormatSupportedFilter(this.getClients(),
                this.serializationService,
                context.protocol(),
                context.format());
    }

    private List<Client> getClients() {
        return this.container.all(Client.class).stream().map(BeanFactory::<Client>get).collect(Collectors.toList());
    }

    private List<Target> filterCandidateTargets(Invoker.Filter filter, Fitable fitable, InvocationContext context,
            List<Target> toFilterTargets) {
        List<Target> filteredTargets =
                filter.filter(fitable, context.localWorkerId(), toFilterTargets, context.filterExtensions());
        filteredTargets = this.filterWithOtherFitables(context, filteredTargets);
        if (CollectionUtils.isEmpty(filteredTargets)) {
            String message = StringUtils.format("No matched fitable targets left after loadbalance. [id={0}]",
                    fitable.toUniqueId());
            TargetNotFoundException exception = new TargetNotFoundException(message);
            exception.associateFitable(fitable.genericable().id(), fitable.id());
            throw exception;
        }
        return filteredTargets;
    }

    private List<Target> filterWithOtherFitables(InvocationContext context, List<Target> targets) {
        List<Target> intersection = targets;
        for (UniqueFitableId id : context.loadBalanceWith()) {
            intersection = this.intersect(intersection, this.getTargets(id));
        }
        return intersection;
    }

    private List<Target> intersect(List<Target> targets, List<Target> toFilterTargets) {
        Set<String> workerIds = toFilterTargets.stream().map(Target::workerId).collect(Collectors.toSet());
        return targets.stream().filter(target -> workerIds.contains(target.workerId())).collect(Collectors.toList());
    }

    private List<Target> getTargets(UniqueFitableId id) {
        return this.targetLocator.lookup(id);
    }
}
