/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.context.repo.flowcontext;

import static com.huawei.fit.waterflow.domain.enums.FlowNodeStatus.PENDING;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowTrace;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 持久化{@link FlowContext}对象到内存中核心类
 *
 * @author g00564732
 * @since 1.0
 */
public class FlowContextMemoRepo<T> implements FlowContextRepo<T> {
    private final List<FlowContext<T>> contexts = new CopyOnWriteArrayList<>();

    @Override
    public List<FlowContext<T>> getContextsByPosition(String streamId, List<String> posIds, String status) {
        return this.contexts.stream()
                .filter(context -> context.getStreamId().equals(streamId))
                .filter(context -> posIds.contains(context.getPosition()))
                .filter(context -> context.getStatus().toString().equals(status))
                .filter(context -> !context.isSent())
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getContextsByPosition(String streamId, String posId, String batchId, String status) {
        return this.contexts.stream()
                .filter(context -> context.getStreamId().equals(streamId))
                .filter(context -> context.getPosition().equals(posId))
                .filter(context -> context.getBatchId().equals(batchId))
                .filter(context -> context.getStatus().toString().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getContextsByTrace(String traceId) {
        return this.contexts.stream()
                .filter(context -> context.getTraceId().contains(traceId))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void save(List<FlowContext<T>> contexts) {
        this.contexts.removeIf(context -> contexts.stream().anyMatch(c1 -> context.getId().equals(c1.getId())));
        this.contexts.addAll(contexts);
    }

    @Override
    public void updateToSent(List<FlowContext<T>> contexts) {
        save(contexts);
    }

    @Override
    public List<FlowContext<T>> getContextsByParallel(String parallelId) {
        return this.contexts.stream()
                .filter(context -> context.getParallel().equals(parallelId))
                .collect(Collectors.toList());
    }

    @Override
    public FlowContext<T> getById(String id) {
        return contexts.stream().filter(context -> context.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<FlowContext<T>> getPendingAndSentByIds(List<String> ids) {
        return contexts.stream()
                .filter(context -> context.getStatus().equals(PENDING))
                .filter(FlowContext::isSent)
                .filter(context -> ids.contains(context.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getByIds(List<String> ids) {
        return contexts.stream().filter(context -> ids.contains(context.getId())).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> requestMappingContext(String streamId, List<String> subscriptions,
            Operators.Filter<T> filter, Operators.Validator<T> validator) {
        List<FlowContext<T>> all = this.contexts.stream()
                .filter(context -> context.getStreamId().equals(streamId))
                .filter(context -> subscriptions.contains(context.getPosition()))
                .filter(context -> context.getStatus() == PENDING)
                .collect(Collectors.toList());
        List<FlowContext<T>> flowContexts = filter.process(all);
        return flowContexts.stream()
                .filter(context -> validator.check(context, flowContexts))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> requestProducingContext(String streamId, List<String> subscriptions,
            Operators.Filter<T> filter) {
        List<FlowContext<T>> all = this.contexts.stream()
                .filter(context -> context.getStreamId().equals(streamId))
                .filter(context -> subscriptions.contains(context.getPosition()))
                .filter(context -> context.getStatus() == PENDING)
                .collect(Collectors.toList());
        return filter.process(all);
    }

    @Override
    public void save(FlowTrace trace, FlowContext<T> flowContext) {
    }

    @Override
    public void updateFlowData(List<FlowContext<T>> contexts) {
        save(contexts);
    }
}
