/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.streams;

import static com.huawei.fit.jober.common.Constant.STREAM_ID_SEPARATOR;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_NODE_CREATE_ERROR;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_NODE_MAX_TASK;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus.ARCHIVED;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus.NEW;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus.PENDING;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus.PROCESSING;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus.READY;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeType.END;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.ParallelMode.EITHER;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.ProcessType.PRE_PROCESS;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.ProcessType.PROCESS;

import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.utils.SleepUtil;
import com.huawei.fit.jober.common.utils.UUIDUtil;
import com.huawei.fit.jober.flowsengine.domain.flows.InterStreamHandler;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.ProcessType;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Callback;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Subscriber;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Subscription;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Error;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Filter;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.FlatMap;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Just;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Map;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Produce;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Reduce;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Validator;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.callbacks.ToCallback;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.Blocks.Block;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.Retryable;
import com.huawei.fit.jober.flowsengine.utils.FlowExecutors;
import com.huawei.fit.jober.flowsengine.utils.PriorityThreadPool;
import com.huawei.fit.jober.flowsengine.utils.WaterFlows;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * FitStream的数据处理节点，上一个节点是下一个节点的publisher
 * 辉子 2019-10-31
 *
 * @param <I>该节点处理函数入参类型
 * @param <O>该节点处理函数返回值类型
 * @author g00564732
 * @since 2023/08/14
 */
public class To<I, O> extends IdGenerator implements Subscriber<I, O> {
    private static final Logger LOG = Logger.get(To.class);

    /**
     * 最大流量，也就是该节点可以处理的最大数据量
     */
    public static final int MAX_CONCURRENCY = 16;

    private static final String PROCESS_T_NAME_PREFIX = "NodeProcessT";

    private static final String PRE_PROCESS_T_NAME_PREFIX = "NodePreProcessT";

    private static final int SLEEP_MILLS = 3_000;

    /**
     * subscriber支持多publisher分发，前提是分发源出来的数据类型需要一致，不然无法统一处理
     */
    protected final List<Subscription<?, I>> froms = new ArrayList<>();

    /**
     * nodeType
     */
    @Getter
    protected FlowNodeType nodeType;

    private final String streamId;

    @Getter
    private final FlowContextMessenger messenger;

    @Getter
    private final FlowContextRepo repo;

    @Getter
    private final FlowLocks locks;

    // 默认自动流转过滤器是按batchID批次过滤contexts
    private final Filter<I> defaultAutoFilter = (contexts) -> {
        if (CollectionUtils.isEmpty(contexts)) {
            return new ArrayList<>();
        }
        String batchId = contexts.stream()
                .filter(context -> StringUtils.isNotEmpty(context.getBatchId()))
                .findAny()
                .map(FlowContext::getBatchId)
                .orElse("");
        return contexts.stream()
                .filter(context -> StringUtils.isNotEmpty(context.getBatchId()))
                .filter(context -> context.getBatchId().equals(batchId))
                .collect(Collectors.toList());
    };

    // 默认人工恢复流转过滤器是按toBatch批次过滤contexts
    private final Filter<I> defaultManualFilter = (contexts) -> {
        if (CollectionUtils.isEmpty(contexts)) {
            return new ArrayList<>();
        }
        String toBatch = contexts.stream()
                .filter(context -> StringUtils.isNotEmpty(context.getToBatch()))
                .findAny()
                .map(FlowContext::getToBatch)
                .orElse("");
        return contexts.stream()
                .filter(context -> StringUtils.isNotEmpty(context.getToBatch()))
                .filter(context -> context.getToBatch().equals(toBatch))
                .collect(Collectors.toList());
    };

    private ProcessMode processMode;

    @Setter
    private Boolean isAsyncJob = false;

    private Validator<I> validator = (i, all) -> true;

    private Block<I> block = null;

    private Filter<I> preFilter = null;

    private Filter<I> postFilter = null;

    /**
     * 该节点只做单数据处理，理解为一条数据一条数据处理，是一个mapping操作
     */
    private Map<FlowContext<I>, O> map;

    /**
     * 该节点只做单数据处理，但处理结果为多条输出
     */
    private FlatMap<FlowContext<I>, O> flatMap;

    /**
     * 该节点把n条数据处理成一条数据
     */
    private Reduce<FlowContext<I>, O> reduce;

    /**
     * 该节点同时处理最多MAX_TRAFFIC条数据，这种情况适合n条a数据生产出m条b数据，是一个producing操作
     */
    private Produce<FlowContext<I>, O> produce;

    /**
     * 当前并发度，已经提交的批次
     */
    @Getter
    private volatile int curConcurrency = 0;

    /**
     * 当前节点预处理是否在运行中
     */
    private volatile boolean preProcessRunning = false;

    /**
     * 当前节点处理是否在运行中
     */
    private volatile boolean processRunning = false;

    /**
     * 数据处理完后callback函数，用于外界的侦听或者数据处理完后后续操作
     */
    private Just<Callback<FlowContext<O>>> callback = i -> {};

    private Error<I> errorHandler = null;

    private Error globalErrorHandler = null;

    private boolean isAuto = true;

    private Thread processT = null;

    private Thread preProcessT = null;

    private Set<InterStreamHandler> listeners = new HashSet<>();

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public To(String streamId, Map<FlowContext<I>, O> processor, FlowContextRepo repo, FlowContextMessenger messenger,
            FlowLocks locks) {
        this(streamId, repo, messenger, locks);
        if (!Optional.ofNullable(processor).isPresent()) {
            throw new JobberException(FLOW_NODE_CREATE_ERROR);
        }
        this.map = processor;
        this.processMode = ProcessMode.MAPPING;
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public To(String streamId, String nodeId, Map<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, processor, repo, messenger, locks);
        this.id = nodeId;
        this.nodeType = nodeType;
    }

    /**
     * 1->N 处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public To(String streamId, FlatMap<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        this(streamId, repo, messenger, locks);
        if (!Optional.ofNullable(processor).isPresent()) {
            throw new JobberException(FLOW_NODE_CREATE_ERROR);
        }
        this.flatMap = processor;
        this.processMode = ProcessMode.FLATMAPPING;
    }

    /**
     * n->1 处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public To(String streamId, Reduce<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        this(streamId, repo, messenger, locks);
        if (!Optional.ofNullable(processor).isPresent()) {
            throw new JobberException(FLOW_NODE_CREATE_ERROR);
        }
        this.reduce = processor;
        this.processMode = ProcessMode.REDUCING;
    }

    /**
     * n->1 处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public To(String streamId, String nodeId, Reduce<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, processor, repo, messenger, locks);
        this.id = nodeId;
        this.nodeType = nodeType;
    }

    /**
     * m->n处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public To(String streamId, Produce<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        this(streamId, repo, messenger, locks);
        if (!Optional.ofNullable(processor).isPresent()) {
            throw new JobberException(FLOW_NODE_CREATE_ERROR);
        }
        this.produce = processor;
        this.processMode = ProcessMode.PRODUCING;
    }

    /**
     * m->n处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public To(String streamId, String nodeId, Produce<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, processor, repo, messenger, locks);
        this.id = nodeId;
        this.nodeType = nodeType;
    }

    private To(String streamId, FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        this.streamId = streamId;
        this.repo = repo;
        this.messenger = messenger;
        this.locks = locks;
    }

    private static <T> void logFileTest(To to, String step, List<FlowContext<T>> ready) {
        ready.forEach(context -> {
            LOG.warn("thread_performance. transId:{}, fileName: {}, node: {}, step: {}, concurrent:{}",
                    context.getTrans().getId(), getFileName(context), to.getId(), step, to.curConcurrency);
        });
    }

    private static <T> String getFileName(FlowContext<T> context) {
        T data = context.getData();
        if (!(data instanceof FlowData)) {
            return "not_file";
        }
        FlowData flowData = (FlowData) data;
        java.util.Map<String, Object> businessData = flowData.getBusinessData();
        java.util.Map<String, Object> origin = (java.util.Map<String, Object>) businessData.get("origin");
        if (origin == null) {
            return "not_file";
        }
        return ObjectUtils.cast(origin.get("fileName"));
    }

    /**
     * 节点接收外部处理事件入口，接收到事件后开始启动执行节点的contexts
     *
     * @param type 触发节点处理的类型，有PRE_PROCESS和PROCESS两种
     * @param contexts 待处理的contexts，留给平行节点处理时进行条件判断
     */
    @Override
    public synchronized void accept(ProcessType type, List<FlowContext<I>> contexts) {
        if (CollectionUtils.isEmpty(contexts)) {
            return;
        }
        if (type == PRE_PROCESS && inParallelMode(contexts)) {
            this.preProcess();
            return;
        }
        if (type == PROCESS && inParallelMode(contexts)) {
            this.process();
            return;
        }
        if (type == PRE_PROCESS && (preProcessT == null || !preProcessRunning)) {
            preProcessRunning = true;
            String threadName = getThreadName(PRE_PROCESS_T_NAME_PREFIX);
            preProcessT = new Thread(this::preProcess, threadName);
            preProcessT.setUncaughtExceptionHandler((tr, ex) -> LOG.error(tr.getName() + " : " + ex.getMessage()));
            preProcessT.start();
            LOG.info("[{}] preprocess main loop starts for stream-id: {}, node-id: {}", threadName, this.streamId,
                    this.id);
        }
        if (type == PROCESS && (processT == null || !processRunning)) {
            processRunning = true;
            String threadName = getThreadName(PROCESS_T_NAME_PREFIX);
            processT = new Thread(this::process, threadName);
            processT.setUncaughtExceptionHandler((tr, ex) -> LOG.error(tr.getName() + " : " + ex.getMessage()));
            processT.start();
            LOG.info("[{}] process main loop starts for stream-id: {}, node-id: {}", threadName, this.streamId,
                    this.id);
        }
    }

    private String getThreadName(String tNamePrefix) {
        return StringUtils.join(STREAM_ID_SEPARATOR, tNamePrefix, this.streamId, this.id);
    }

    private boolean inParallelMode(List<FlowContext<I>> contexts) {
        return StringUtils.isNotEmpty(contexts.get(0).getParallel());
    }

    /**
     * 节点request边上pending的数据
     * 首先通过分布式锁，保证每次只有一个节点线程可以请求到一批次contexts（以batchID为维度）
     * 其次过滤出ready的contexts，并且将其标记为sent，然后释放分布式锁
     * 最后将ready的contexts通过事件发送给引擎外部
     * 保证一批次contexts一次只有一个线程在处理
     * 非常重要！退出机制增加保护策略，避免A线程退出过程中，B线程放数据到边上数据得不到处理的场景：
     * 这时A线程未标记退出，B线程已经完成触发动作，B线程以为A线程还在处理，而A线程直接就会退出，因此由A线程判断是否再触发一次
     */
    private void preProcess() {
        while (true) {
            List<FlowContext<I>> ready = new ArrayList<>();
            try {
                ready = requestReady();
                if (CollectionUtils.isEmpty(ready)) {
                    preProcessRunning = false;
                    LOG.info("[{}] preprocess main loop exit for stream-id: {}, node-id: {}",
                            this.getThreadName(PRE_PROCESS_T_NAME_PREFIX), this.streamId, this.id);
                    this.handlePreProcessConcurrentConflict();
                    return;
                }
                messenger.send(this.getId(), ready);
                this.releaseTrace(ready);
            } catch (Exception ex) {
                ready.forEach( // 如果是数据库或者redis挂了，会死循环，线程不退出等待数据库或者redis恢复
                        r -> LOG.error(
                                "preprocess main loop exception stream-id: {}, node-id: {}, context-id: {}, errors: {}",
                                this.streamId, this.id, r.getId(), ex));
                LOG.error("preprocess main loop exception details: ", ex);
            } finally {
                SleepUtil.sleep(SLEEP_MILLS);
            }
        }
    }

    /**
     * todo 目前不完善，遇到人工就释放了，如果人工和系统有并行，会有问题
     *
     * @param contexts 待处理的context
     */
    private void releaseTrace(List<FlowContext<I>> contexts) {
        contexts.forEach(context -> context.getTraceId().forEach(traceId -> {
            LOG.warn("preProcess release trace:{0}, contextId:{1}", traceId, context.getId());
            repo.getTraceOwnerService().release(traceId);
        }));
    }

    private void process() {
        this.getProcessMode().request(this);
    }

    private void handlePreProcessConcurrentConflict() {
        List<FlowContext<I>> concurrentConflictContexts = this.preFilter()
                .process(repo.getContextsByPosition(this.streamId,
                        this.froms.stream().map(Identity::getId).collect(Collectors.toList()), PENDING.toString()));
        if (CollectionUtils.isEmpty(concurrentConflictContexts) || inParallelMode(concurrentConflictContexts)) {
            return;
        }
        LOG.info("[{}] preprocess thread conflict happens for stream-id: {}, node-id: {}",
                this.getThreadName(PRE_PROCESS_T_NAME_PREFIX), this.streamId, this.id);
        this.accept(PRE_PROCESS, concurrentConflictContexts);
    }

    /**
     * PREPROCESS与PROCESS拿到的context会冲突
     * PREPROCESS查询边上PENDING的数据，且SENT为false
     * PROCESS查询边上PENDING的数据，有可能是SENT为false的
     * 如果是同一批数据，一个只更新status，一个只更新sent标记，会被覆盖
     * 处理方式为，增加两个更新方法，只更新对应的字段，其他字段不更新
     *
     * @return List<FlowContext < I>>
     */
    private List<FlowContext<I>> requestReady() {
        Lock lock = locks.getDistributedLock(locks.streamNodeLockKey(this.streamId, this.id, PRE_PROCESS.toString()));
        lock.lock();
        try {
            List<FlowContext<I>> contexts = this.preFilter()
                    .process(repo.getContextsByPosition(this.streamId,
                            this.froms.stream().map(Identity::getId).collect(Collectors.toList()), PENDING.toString()));
            contexts = filterTerminate(contexts);
            if (CollectionUtils.isEmpty(contexts)) {
                return new ArrayList<>();
            }
            repo.updateToSent(contexts);
            return contexts;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void block(Block<I> block) {
        this.isAuto = false;
        this.block = block;
        block.setTarget(this);
    }

    @Override
    public Block<I> block() {
        return this.block;
    }

    /**
     * preFilter
     *
     * @param filter filter
     */
    public void preFilter(Filter<I> filter) {
        this.preFilter = Optional.ofNullable(filter).orElse(defaultAutoFilter);
    }

    /**
     * preFilter
     *
     * @return preFilter
     */
    public Filter<I> preFilter() {
        return Optional.ofNullable(this.preFilter).orElse(defaultAutoFilter);
    }

    /**
     * postFilter
     *
     * @param filter filter
     */
    public void postFilter(Filter<I> filter) {
        this.postFilter = Optional.ofNullable(filter).orElseGet(this::defaultFilter);
    }

    /**
     * postFilter
     *
     * @return postFilter
     */
    public Filter<I> postFilter() {
        return Optional.ofNullable(this.postFilter).orElseGet(this::defaultFilter);
    }

    /**
     * defaultFilter
     *
     * @return filter
     */
    public Filter<I> defaultFilter() {
        if (this.isAuto) {
            return defaultAutoFilter;
        } else {
            return defaultManualFilter;
        }
    }

    public void setValidator(Validator<I> validator) {
        if (validator == null) {
            this.validator = (i, all) -> true;
        } else {
            this.validator = validator;
        }
    }

    public ProcessMode getProcessMode() {
        return this.processMode;
    }

    @Override
    public void onSubscribe(Subscription<?, I> subscription) {
        this.froms.add(subscription); // 将该节点的from的event加入
    }

    @Override
    public void onProcess(List<FlowContext<I>> pre) {
        if (CollectionUtils.isEmpty(pre)) {
            return;
        }
        try {
            if (!isOwnTrace(pre)) {
                LOG.warn("[BeforeProcess] The trace is not belong to this node, traceId={}.",
                        String.join(",", pre.get(0).getTraceId()));
                return;
            }
            // todo 待确认
            if (pre.size() == 1 && pre.get(0).getData() == null) {
                this.afterProcess(pre, new ArrayList<>());
                return;
            }
            if (this.isAsyncJob) {
                beforeAsyncProcess(pre);
                this.getProcessMode().process(this, pre);
                return;
            }
            List<FlowContext<O>> after = this.getProcessMode().process(this, pre);
            if (!isOwnTrace(pre)) {
                LOG.warn("[AfterProcess] The trace is not belong to this node, traceId={}.",
                        String.join(",", pre.get(0).getTraceId()));
                return;
            }
            this.afterProcess(pre, after);
        } catch (Exception ex) {
            LOG.error("node process exception stream-id: {}, node-id: {}, position-id: {}, traceId: {}. errors: {}",
                    this.streamId, this.id, pre.get(0).getPosition(), pre.get(0).getTraceId(), ex);
            LOG.error("node process exception details: ", ex);
            setFailed(pre, ex);
        } finally {
            updateConcurrency(-1);
        }
    }

    public void setFailed(List<FlowContext<I>> pre, Exception ex) {
        Retryable<I> retryable = new Retryable<>(this.getRepo(), (To<I, I>) this);
        Optional.ofNullable(this.errorHandler).ifPresent(handler -> handler.handle(ex, retryable, pre));
        Optional.ofNullable(this.globalErrorHandler).ifPresent(handler -> handler.handle(ex, retryable, pre));
    }

    private boolean isOwnTrace(List<FlowContext<I>> pre) {
        return pre.get(0).getTraceId().stream().allMatch(traceId -> {
            if (!repo.getTraceOwnerService().isOwn(traceId)) {
                LOG.warn("[BeforeProcess] The trace is not belong to this node, traceId={}.", traceId);
                return false;
            }
            return true;
        });
    }

    /**
     * 更新并发度
     *
     * @param diff 变化的并发度
     */
    public synchronized void updateConcurrency(int diff) {
        this.curConcurrency += diff;
    }

    private List<FlowContext<I>> filterTerminate(List<FlowContext<I>> contexts) {
        if (CollectionUtils.isEmpty(contexts)) {
            return Collections.emptyList();
        }

        List<String> traceIds = getTraceIds(contexts);
        if (isTracesTerminate(traceIds)) {
            getRepo().updateToTerminated(traceIds);
            return Collections.emptyList();
        }
        return contexts;
    }

    private boolean isTracesTerminate(List<String> traceIds) {
        return this.repo.isTracesTerminate(traceIds);
    }

    private List<String> getTraceIds(List<FlowContext<I>> contexts) {
        return contexts.stream()
                .flatMap(context -> context.getTraceId().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void onNext(String batchId) {
    }

    private void feedback(List<FlowContext<O>> contexts) {
        this.callback.process(new ToCallback<>(contexts));
    }
    private void beforeAsyncProcess(List<FlowContext<I>> pre) {
        updateBatch(pre, Collections.emptyList());
        pre.forEach(p -> p.setStatus(PROCESSING));
        this.getRepo().update(pre);
        this.getRepo().updateStatus(pre, pre.get(0).getStatus().toString(), pre.get(0).getPosition());
    }

    /**
     * 节点处理处理完后执行的操作，默认是保存处理之前和之后的对上下文
     *
     * @param preContexts 当前节点处理之前的context集合
     * @param after 当前节点处理之后的新生产的context结合
     */
    @Override
    public void afterProcess(List<FlowContext<I>> preContexts, List<FlowContext<O>> after) {
        Optional.ofNullable(preContexts.get(0).getToBatch())
                .ifPresent(toBatch -> this.getRepo().deleteRetryRecord(Collections.singletonList(toBatch)));
        updateBatch(preContexts, after);
        Set<String> traces = new HashSet<>();
        preContexts.forEach(context -> {
            traces.addAll(context.getTraceId());
            context.setStatus(ARCHIVED);
        });
        after.forEach(context -> context.getTraceId().addAll(traces));
        if ((Objects.isNull(this.nodeType) || !this.nodeType.equals(END)) && !after.isEmpty()) {
            updateContextPool(after, traces);
            this.getRepo().save(after);
        }
        // 合并一次操作，并不需要处理data部分（这部分用户控制，尽量减少性能影响），只更新toBatch, 状态和位置
        LOG.warn("afterProcess before updateProcessStatus");
        this.getRepo().updateProcessStatus(preContexts);
        LOG.warn("afterProcess after updateProcessStatus");

        if (CollectionUtils.isNotEmpty(after)) {
            feedback(after); // 查找一个transaction里的所有数据的都完成了，运行callback给stream外反馈数据
            this.onNext(after.get(0).getBatchId());
        }

        // 处理好数据后对外送数据，驱动其他flow响应
        after.forEach(context -> this.publish(context.getData(), context.getTrans().getId()));
    }

    private void updateContextPool(List<FlowContext<O>> after, Set<String> traces) {
        Lock lock = this.locks.getDistributedLock(
                this.locks.streamNodeLockKey(this.streamId, this.id, "UpdateContextPool"));
        lock.lock();
        try {
            this.getRepo().updateContextPool(after, traces);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 更新contexts的batchID，负责更新当前contexts的toBatch和新contexts的batchId
     * 特殊情况：先有toBatch，再有after的情况
     * 1、平行节点第一个节点处理完会生成toBatch，后续的平行节点拿到同一批context，需要保证同一批toBatch
     * 2、人工任务恢复执行会先生成toBatch，after直接复用外部生成的toBatch
     *
     * @param pre 当前节点处理完的contexts
     * @param after 当前节点新生产的contexts
     */
    private void updateBatch(List<FlowContext<I>> pre, List<FlowContext<O>> after) {
        if (!Objects.isNull(this.nodeType) && this.nodeType.equals(END)) {
            return;
        }
        String toBatch = pre.stream()
                .filter(context -> StringUtils.isNotEmpty(context.getToBatch()))
                .findAny()
                .map(FlowContext::getToBatch)
                .orElseGet(UUIDUtil::uuid);
        pre.forEach(context -> context.toBatch(toBatch));
        after.forEach(context -> context.batchId(toBatch));
    }

    @Override
    public void onComplete(Just<Callback<FlowContext<O>>> callback) {
        To<I, O> me = this;
        this.callback = callback;
    }

    @Override
    public Boolean isAuto() {
        return this.isAuto;
    }

    @Override
    public List<FlowContext<O>> nextContexts(String batchId) {
        return this.repo.getContextsByPosition(this.streamId, this.getId(), batchId, NEW.toString());
    }

    @Override
    public void onError(Error<I> handler) {
        this.errorHandler = handler;
    }

    @Override
    public void onGlobalError(Error handler) {
        this.globalErrorHandler = handler;
    }

    private <T1> void introduceToProcess(List<FlowContext<T1>> contexts) {
        // parallelMode.EITHER模式下，如果有完成的context，则本context处理退出
        contexts.stream()
                .filter(context -> !context.getParallelMode().equals(EITHER.name()) || context.isJoined()
                        || !isParallelJoined(context))
                .forEach(context -> {
                    context.setPosition(this.getId());
                    context.setStatus(READY);
                });
    }

    private <T1> boolean isParallelJoined(FlowContext<T1> context) {
        List<FlowContext<T1>> contextsByParallel = this.getRepo().getContextsByParallel(context.getParallel());
        return contextsByParallel.stream().anyMatch(FlowContext::isJoined);
    }

    @Override
    public String getStreamId() {
        return this.streamId;
    }

    @Override
    public void register(InterStreamHandler<O> handler) {
        this.listeners.add(handler);
    }

    @Override
    public void publish(O data, String id) {
        this.listeners.forEach(listener -> listener.handle(data, id));
    }

    @Override
    public void publish(O[] data, String id) {
        this.listeners.forEach(listener -> listener.handle(data, id));
    }

    /**
     * ProcessMode
     *
     * @since 2023-09-15
     */
    public enum ProcessMode {
        PRODUCING {
            @Override
            public <T1, R1> List<FlowContext<R1>> process(To<T1, R1> to, List<FlowContext<T1>> contexts) {
                // 异步任务，不关心结果
                if (to.isAsyncJob) {
                    processData(to, contexts);
                    return null;
                }
                // 同步任务，返回结果
                return processData(to, contexts)
                        .stream()
                        .map(data -> contexts.get(0).generate(data, to.getId()))
                        .collect(Collectors.toList());
            }

            private <T1, R1> List<R1> processData(To<T1, R1> to, List<FlowContext<T1>> contexts) {
                return to.produce.process(contexts);
            }

            @Override
            protected <T1, R1> List<FlowContext<T1>> requestAll(To<T1, R1> to) {
                return to.repo.requestProducingContext(to.streamId,
                        to.froms.stream().map(Identity::getId).collect(Collectors.toList()), to.postFilter());
            }
        },
        REDUCING {
            @Override
            public <T1, R1> List<FlowContext<R1>> process(To<T1, R1> to, List<FlowContext<T1>> contexts) {
                List<FlowContext<R1>> result = new ArrayList<>();
                result.add(contexts.get(0).generate(to.reduce.process(contexts), to.getId()));
                return result;
            }

            @Override
            protected <T1, R1> List<FlowContext<T1>> requestAll(To<T1, R1> to) {
                return PRODUCING.requestAll(to);
            }
        },
        MAPPING {
            @Override
            public <T1, R1> List<FlowContext<R1>> process(To<T1, R1> to, List<FlowContext<T1>> contexts) {
                return contexts.stream()
                        .parallel()
                        .map(context -> context.generate(to.map.process(context), to.getId()))
                        .collect(Collectors.toList());
            }

            @Override
            protected <T1, R1> List<FlowContext<T1>> requestAll(To<T1, R1> to) {
                return to.repo.requestMappingContext(to.streamId,
                        to.froms.stream().map(Identity::getId).collect(Collectors.toList()), to.defaultFilter(),
                        to.validator);
            }
        },
        FLATMAPPING {
            @Override
            public <T1, R1> List<FlowContext<R1>> process(To<T1, R1> to, List<FlowContext<T1>> contexts) {
                return contexts.stream()
                        .parallel()
                        .flatMap(context -> context.generate(to.flatMap.process(context), to.getId()).stream())
                        .collect(Collectors.toList());
            }

            @Override
            protected <T1, R1> List<FlowContext<T1>> requestAll(To<T1, R1> to) {
                return MAPPING.requestAll(to);
            }
        };

        /**
         * 节点处理器
         *
         * @param to to
         * @param contexts contexts
         * @return List<FlowContext < R1>>
         */
        public abstract <T1, R1> List<FlowContext<R1>> process(To<T1, R1> to, List<FlowContext<T1>> contexts);

        /**
         * 节点request边上pending的数据
         * 首先通过分布式锁，保证每次只有一个节点线程可以请求到一批次contexts（以batchID为维度）
         * 其次过滤出ready的contexts，并且将其状态更新为ready，然后释放分布式锁
         * 最后将ready的contexts提交给节点线程池处理
         * 保证一批次contexts一次只有一个线程在处理
         * 非常重要！退出机制增加保护策略，避免A线程退出过程中，B线程放数据到边上数据得不到处理的场景：
         * 这时A线程未标记退出，B线程已经完成触发动作，B线程以为A线程还在处理，而A线程直接就会退出，因此由A线程判断是否再触发一次
         *
         * @param <T1> 流程实例执行时的入参数据类型，用于泛型推倒
         * @param <R1> 流程实例执行时的出参数据类型，用于泛型推倒
         * @param to 当前节点
         */
        public <T1, R1> void request(To<T1, R1> to) {
            while (true) {
                LOG.warn("request enter");
                if (to.curConcurrency >= MAX_CONCURRENCY) {
                    SleepUtil.sleep(1000);
                    continue;
                }
                List<FlowContext<T1>> ready = new ArrayList<>();
                try {
                    ready = requestReady(to);
                    if (CollectionUtils.isEmpty(ready)) {
                        to.processRunning = false;
                        LOG.info("[{}] process main loop exit for stream-id: {}, node-id: {}",
                                to.getThreadName(To.PROCESS_T_NAME_PREFIX), to.streamId, to.id);
                        handleProcessConcurrentConflict(to);
                        return;
                    }
                    if (to.inParallelMode(ready)) {
                        to.onProcess(ready);
                    } else {
                        this.submit(to, ready);
                    }
                    LOG.warn("request after submit");
                } catch (Exception ex) {
                    ready.forEach( // 如果是数据库或者redis挂了，会死循环，线程不退出等待数据库或者redis恢复
                            r -> LOG.error("process main loop exception stream-id: {}, node-id: {}, context-id: {}"
                                    + ", errors: {}", to.streamId, to.id, r.getId(), ex));
                    LOG.error("process main loop exception details: ", ex);
                    LOG.warn("request before sleep");
                    SleepUtil.sleep(50);
                    LOG.warn("request after sleep");
                } finally {
                    LOG.warn("request end");
                }
            }
        }

        /**
         * 查找节点连接的边上所有的contexts，由子类负责实现
         *
         * @param to 本节点节点类
         * @param <T1> 流程实例执行时的入参数据类型，用于泛型推倒
         * @param <R1> 流程实例执行时的出参数据类型，用于泛型推倒
         * @return List<FlowContext < T1>>
         */
        protected abstract <T1, R1> List<FlowContext<T1>> requestAll(To<T1, R1> to);

        private <T1, R1> List<FlowContext<T1>> requestReady(To<T1, R1> to) {
            LOG.warn("requestReady enter");
            Lock lock = to.locks.getDistributedLock(to.locks.streamNodeLockKey(to.streamId, to.id, PROCESS.toString()));
            lock.lock();
            try {
                List<FlowContext<T1>> all = requestAll(to);
                LOG.warn("requestReady after request all");
                List<FlowContext<T1>> ready = filterReady(to, all);
                ready = to.filterTerminate(ready);
                LOG.warn("requestReady after terminate all");
                if (CollectionUtils.isEmpty(ready)) {
                    return new ArrayList<>();
                }
                if (to.curConcurrency + 1 > MAX_CONCURRENCY) {
                    throw new JobberException(FLOW_NODE_MAX_TASK, to.getId());
                }
                to.updateConcurrency(1);
                LOG.warn("requestReady after updateConcurrency");
                String toBatchId = UUIDUtil.uuid();
                ready.forEach(context -> context.toBatch(toBatchId));

                to.repo.updateProcessStatus(ready);
                LOG.warn("requestReady after updateProcessStatus");
                return ready;
            } finally {
                lock.unlock();
            }
        }

        /**
         * 查找ready的context
         *
         * @param to 本节点节点类
         * @param pre 本节点获取到边上所有的contexts集合
         * @param <T1> 流程实例执行时的入参数据类型，用于泛型推倒
         * @param <R1> 流程实例执行时的出参数据类型，用于泛型推倒
         * @return List<FlowContext>
         */
        private <T1, R1> List<FlowContext<T1>> filterReady(To<T1, R1> to, List<FlowContext<T1>> pre) {
            to.introduceToProcess(pre);
            return pre.stream().filter(context -> context.getStatus() == READY).collect(Collectors.toList());
        }

        private <T1, R1> void submit(To<T1, R1> to, List<FlowContext<T1>> ready) {
            logFileTest(to, "submit", ready);
            FlowExecutors.getThreadPool(StringUtils.join(STREAM_ID_SEPARATOR, to.streamId, to.id), MAX_CONCURRENCY)
                    .submit(PriorityThreadPool.PriorityTask.builder()
                            .priority(PriorityThreadPool.PriorityTask.PriorityInfo.builder()
                                    .order(WaterFlows.getNodeOrder(ready.get(0).getStreamId(), to.getId()))
                                    .createTime(System.currentTimeMillis())
                                    .build())
                            .runner(() -> to.onProcess(ready))
                            .build());
        }

        private <T1, R1> void handleProcessConcurrentConflict(To<T1, R1> to) {
            List<FlowContext<T1>> ready = filterReady(to, requestAll(to));
            if (CollectionUtils.isEmpty(ready) || to.inParallelMode(ready)) {
                return;
            }
            LOG.info("[{}] process thread conflict happens for stream-id: {}, node-id: {}",
                    to.getThreadName(To.PROCESS_T_NAME_PREFIX), to.streamId, to.id);
            to.accept(PROCESS, ready);
        }
    }
}
