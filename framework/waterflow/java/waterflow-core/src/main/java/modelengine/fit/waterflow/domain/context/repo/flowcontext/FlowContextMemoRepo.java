/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.context.repo.flowcontext;

import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowTrace;
import modelengine.fit.waterflow.domain.stream.operators.Operators;

import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 持久化{@link FlowContext}对象到内存中核心类
 *
 * @author 高诗意
 * @since 1.0
 */
public class FlowContextMemoRepo<T> implements FlowContextRepo<T> {
    private final ConcurrentLinkedHashMap<String, FlowContext<T>> contextsMap = new ConcurrentLinkedHashMap<>();

    private final boolean isReserveTerminal;

    /**
     * 构造方法
     */
    public FlowContextMemoRepo() {
        this(false);
    }

    /**
     * 构造方法
     *
     * @param isReserveTerminal 是否保留结束的数据，一般只有测试才保留
     */
    public FlowContextMemoRepo(boolean isReserveTerminal) {
        this.isReserveTerminal = isReserveTerminal;
    }

    @Override
    public List<FlowContext<T>> getContextsByPosition(String streamId, List<String> posIds, String status) {
        return this.contextsMap.stream()
                .filter(context -> context.getStreamId().equals(streamId))
                .filter(context -> posIds.contains(context.getPosition()))
                .filter(context -> context.getStatus().toString().equals(status))
                .filter(context -> !context.isSent())
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getContextsByPosition(String streamId, String posId, String batchId, String status) {
        return this.contextsMap.stream()
                .filter(context -> context.getStreamId().equals(streamId))
                .filter(context -> context.getPosition().equals(posId))
                .filter(context -> context.getBatchId().equals(batchId))
                .filter(context -> context.getStatus().toString().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getContextsByTrace(String traceId) {
        return this.contextsMap.stream()
                .filter(context -> context.getTraceId().contains(traceId))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void save(List<FlowContext<T>> contexts) {
        contexts.forEach(context -> {
            if (this.isReserveTerminal) {
                this.contextsMap.put(context.getId(), context);
                return;
            }
            if (context.getStatus() == FlowNodeStatus.ARCHIVED || context.getStatus() == FlowNodeStatus.ERROR) {
                this.contextsMap.remove(context.getId());
            } else {
                this.contextsMap.put(context.getId(), context);
            }
        });
    }

    @Override
    public void updateToSent(List<FlowContext<T>> contexts) {
        save(contexts);
    }

    @Override
    public List<FlowContext<T>> getContextsByParallel(String parallelId) {
        return this.contextsMap.stream()
                .filter(context -> context.getParallel().equals(parallelId))
                .collect(Collectors.toList());
    }

    @Override
    public FlowContext<T> getById(String id) {
        return this.contextsMap.stream().filter(context -> context.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<FlowContext<T>> getPendingAndSentByIds(List<String> ids) {
        return this.contextsMap.stream()
                .filter(context -> context.getStatus().equals(FlowNodeStatus.PENDING))
                .filter(FlowContext::isSent)
                .filter(context -> ids.contains(context.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getByIds(List<String> ids) {
        return ids.stream().map(contextsMap::get).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> requestMappingContext(String streamId, List<String> subscriptions,
            Set<String> excludeTraceIds, Operators.Filter<T> filter, Operators.Validator<T> validator) {
        List<FlowContext<T>> all = getFlowContexts(streamId, subscriptions, excludeTraceIds);
        List<FlowContext<T>> flowContexts = filter.process(all);
        return flowContexts.stream()
                .filter(context -> validator.check(context, flowContexts))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> requestProducingContext(String streamId, List<String> subscriptions,
            Set<String> excludeTraceIds, Operators.Filter<T> filter) {
        List<FlowContext<T>> all = getFlowContexts(streamId, subscriptions, excludeTraceIds);
        return filter.process(all);
    }

    @Override
    public void save(FlowTrace trace, FlowContext<T> flowContext) {
    }

    @Override
    public void updateFlowData(List<FlowContext<T>> contexts) {
        save(contexts);
    }

    private List<FlowContext<T>> getFlowContexts(String streamId, List<String> subscriptions,
            Set<String> excludeTraceIds) {
        return this.contextsMap.stream().filter(context -> context.getStreamId().equals(streamId))
                .filter(context -> subscriptions.contains(context.getPosition()))
                .filter(context -> context.getStatus() == FlowNodeStatus.PENDING)
                .filter(context -> !context.getTraceId().stream().anyMatch(excludeTraceIds::contains))
                .collect(Collectors.toList());
    }

    /**
     * 构造一个支持并发且可以保障元素顺序的map
     *
     * @param <K> map的key类型
     * @param <V> map的value类型
     */
    private static class ConcurrentLinkedHashMap<K, V> {
        private final Map<K, V> map = new ConcurrentHashMap<>();

        private final Deque<K> order = new ConcurrentLinkedDeque<>();

        /**
         * put一个键值对
         *
         * @param key key
         * @param value value
         */
        public void put(K key, V value) {
            if (!map.containsKey(key)) {
                order.add(key);
            }
            map.put(key, value);
        }

        /**
         * 根据key获取结果
         *
         * @param key key
         * @return 对应的value，可能为null
         */
        public V get(K key) {
            return map.get(key);
        }

        /**
         * 删除一个key
         *
         * @param key key
         * @return 被删除的值
         */
        public V remove(K key) {
            order.remove(key);
            return map.remove(key);
        }

        /**
         * 获取流式的元素
         *
         * @return 流式元素
         */
        public Stream<V> stream() {
            return order.stream().map(map::get).filter(obj -> !Objects.isNull(obj));
        }
    }
}
