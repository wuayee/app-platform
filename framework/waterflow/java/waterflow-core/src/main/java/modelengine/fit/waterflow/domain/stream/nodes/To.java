/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream.nodes;

import static modelengine.fit.waterflow.common.ErrorCodes.FLOW_NODE_CREATE_ERROR;
import static modelengine.fit.waterflow.common.ErrorCodes.FLOW_NODE_MAX_TASK;
import static modelengine.fit.waterflow.domain.enums.FlowNodeStatus.ARCHIVED;
import static modelengine.fit.waterflow.domain.enums.FlowNodeStatus.NEW;
import static modelengine.fit.waterflow.domain.enums.FlowNodeStatus.PENDING;
import static modelengine.fit.waterflow.domain.enums.FlowNodeStatus.READY;
import static modelengine.fit.waterflow.domain.enums.ParallelMode.EITHER;
import static modelengine.fit.waterflow.domain.enums.ProcessType.PRE_PROCESS;
import static modelengine.fit.waterflow.domain.enums.ProcessType.PROCESS;

import lombok.Getter;
import modelengine.fit.waterflow.common.exceptions.WaterflowException;
import modelengine.fit.waterflow.domain.common.Constant;
import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.context.WindowToken;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.domain.emitters.EmitterListener;
import modelengine.fit.waterflow.domain.enums.FlowNodeType;
import modelengine.fit.waterflow.domain.enums.ProcessType;
import modelengine.fit.waterflow.domain.stream.callbacks.ToCallback;
import modelengine.fit.waterflow.domain.stream.operators.Operators;
import modelengine.fit.waterflow.domain.stream.reactive.Callback;
import modelengine.fit.waterflow.domain.stream.reactive.Subscriber;
import modelengine.fit.waterflow.domain.stream.reactive.Subscription;
import modelengine.fit.waterflow.domain.utils.FlowExecutors;
import modelengine.fit.waterflow.domain.utils.IdGenerator;
import modelengine.fit.waterflow.domain.utils.Identity;
import modelengine.fit.waterflow.domain.utils.SleepUtil;
import modelengine.fit.waterflow.domain.utils.UUIDUtil;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FitStream的数据处理节点，上一个节点是下一个节点的publisher
 * 辉子 2019-10-31
 *
 * @param <I>该节点处理函数入参类型
 * @param <O>该节点处理函数返回值类型
 * @author 高诗意
 * @since 1.0
 */
public class To<I, O> extends IdGenerator implements Subscriber<I, O> {
    private static final Logger LOG = Logger.get(To.class);

    private static final String PROCESS_T_NAME_PREFIX = "NodeProcessT";

    private static final String PRE_PROCESS_T_NAME_PREFIX = "NodePreProcessT";

    /**
     * 最大流量，也就是该节点可以处理的最大数据量
     * TODO xiangyu 节点支持并发的配置，即解析后给该变量赋值
     */
    private static final int MAX_CONCURRENCY = 1;

    private static final int MAX_TRACE_CONCURRENCY = 16;

    private static final int SLEEP_MILLS = 10;

    /**
     * subscriber支持多publisher分发，前提是分发源出来的数据类型需要一致，不然无法统一处理
     */
    protected final List<Subscription<I>> froms = new ArrayList<>();

    /**
     * nodeType
     */
    @Getter
    protected FlowNodeType nodeType;

    private final String streamId;

    @Getter
    private final FlowContextMessenger messenger;

    @Getter
    private final FlowContextRepo flowContextRepo;

    @Getter
    private final FlowLocks locks;

    private final Map<String, Integer> traceConcurrency = new HashMap<>();

    // 默认自动流转过滤器是按batchID批次过滤contexts
    private final Operators.Filter<I> defaultAutoFilter = (contexts) -> {
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
    private final Operators.Filter<I> defaultManualFilter = (contexts) -> {
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

    @Getter
    private ProcessMode processMode;

    private Operators.Validator<I> validator = (i, all) -> true;

    private Blocks.Block<I> block = null;

    private Operators.Filter<I> preFilter = null;

    private Operators.Filter<I> postFilter = null;

    /**
     * 该节点只做单数据处理，理解为一条数据一条数据处理，是一个mapping操作
     */
    private Operators.Map<FlowContext<I>, O> map;

    /**
     * 该节点同时处理最多MAX_TRAFFIC条数据，这种情况适合n条a数据生产出m条b数据，是一个producing操作
     */
    private Operators.Produce<FlowContext<I>, O> produce;

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
    private Operators.Just<Callback<FlowContext<O>>> callback = any -> {
    };

    private Operators.ErrorHandler<I> errorHandler = null;

    private Operators.ErrorHandler globalErrorHandler = null;

    private boolean isAuto = true;

    private Thread processT = null;

    private Thread preProcessT = null;

    private Set<EmitterListener> listeners = new HashSet<>();

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public To(String streamId, Operators.Map<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        this(streamId, repo, messenger, locks);
        if (!Optional.ofNullable(processor).isPresent()) {
            throw new WaterflowException(FLOW_NODE_CREATE_ERROR);
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
    public To(String streamId, String nodeId, Operators.Map<FlowContext<I>, O> processor,
            FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, processor, repo, messenger, locks);
        Optional.ofNullable(nodeId).ifPresent(id -> To.this.id = id);
        this.nodeType = nodeType;
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
    public To(String streamId, String nodeId, Operators.Produce<FlowContext<I>, O> processor,
            FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, repo, messenger, locks);
        if (!Optional.ofNullable(processor).isPresent()) {
            throw new WaterflowException(FLOW_NODE_CREATE_ERROR);
        }
        this.produce = processor;
        this.processMode = ProcessMode.PRODUCING;

        this.id = nodeId;
        this.nodeType = nodeType;
    }

    private To(String streamId, FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        this.streamId = streamId;
        this.flowContextRepo = repo;
        this.messenger = messenger;
        this.locks = locks;
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
            preProcessT.setUncaughtExceptionHandler(
                    (thread, error) -> LOG.error("run preProcessT error, message:{}", error.getMessage()));
            preProcessT.start();
            LOG.debug("[{}] preprocess main loop starts for stream-id: {}, node-id: {}", threadName, this.streamId,
                    this.id);
        }
        if (type == PROCESS && (processT == null || !processRunning)) {
            processRunning = true;
            String threadName = getThreadName(PROCESS_T_NAME_PREFIX);
            processT = new Thread(this::process, threadName);
            processT.setUncaughtExceptionHandler(
                    (thread, error) -> LOG.error("run processT error, message:{}", error.getMessage()));
            processT.start();
            LOG.debug("[{}] process main loop starts for stream-id: {}, node-id: {}", threadName, this.streamId,
                    this.id);
        }
    }

    private String getThreadName(String tNamePrefix) {
        return StringUtils.join(Constant.STREAM_ID_SEPARATOR, tNamePrefix, this.streamId, this.id);
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
                    LOG.debug("[{}] preprocess main loop exit for stream-id: {}, node-id: {}",
                            this.getThreadName(PRE_PROCESS_T_NAME_PREFIX), this.streamId, this.id);
                    this.handlePreProcessConcurrentConflict();
                    return;
                }
                messenger.send(this.getId(), ready);
            } catch (Exception ex) {
                ready.forEach( // 如果是数据库或者redis挂了，会死循环，线程不退出等待数据库或者redis恢复
                        r -> LOG.error(
                                "Preprocess main loop exception stream-id: {}, node-id: {}, context-id: {}.",
                                this.streamId, this.id, r.getId()));
                LOG.debug("Preprocess main loop exception details: ", ex);
            } finally {
                SleepUtil.sleep(SLEEP_MILLS);
            }
        }
    }

    private void process() {
        this.getProcessMode().request(this);
    }

    private void handlePreProcessConcurrentConflict() {
        List<FlowContext<I>> concurrentConflictContexts = this.preFilter()
                .process(flowContextRepo.getContextsByPosition(this.streamId,
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
     * @return ready的contextList
     */
    private List<FlowContext<I>> requestReady() {
        Lock lock = locks.getDistributeLock(locks.lockKey(this.streamId, this.id, "PreProcess"));
        lock.lock();
        try {
            List<FlowContext<I>> contexts = this.preFilter()
                    .process(flowContextRepo.getContextsByPosition(this.streamId,
                            this.froms.stream().map(Identity::getId).collect(Collectors.toList()), PENDING.toString()));
            contexts = filterTerminate(contexts);
            if (CollectionUtils.isEmpty(contexts)) {
                return new ArrayList<>();
            }
            flowContextRepo.updateToSent(contexts);
            return contexts;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void block(Blocks.Block<I> block) {
        this.isAuto = false;
        this.block = block;
        block.setTarget(this);
    }

    @Override
    public Blocks.Block<I> block() {
        return this.block;
    }

    /**
     * preFilter
     *
     * @param filter filter
     */
    @Override
    public void preFilter(Operators.Filter<I> filter) {
        this.preFilter = Optional.ofNullable(filter).orElse(defaultAutoFilter);
    }

    /**
     * preFilter
     *
     * @return preFilter
     */
    @Override
    public Operators.Filter<I> preFilter() {
        return Optional.ofNullable(this.preFilter).orElse(defaultAutoFilter);
    }

    /**
     * postFilter
     *
     * @param filter filter
     */
    @Override
    public void postFilter(Operators.Filter<I> filter) {
        this.postFilter = Optional.ofNullable(filter).orElseGet(this::defaultFilter);
    }

    /**
     * postFilter
     *
     * @return postFilter
     */
    @Override
    public Operators.Filter<I> postFilter() {
        return Optional.ofNullable(this.postFilter).orElseGet(this::defaultFilter);
    }

    /**
     * 获得默认的filter
     *
     * @return 默认的filter
     */
    public Operators.Filter<I> defaultFilter() {
        if (this.isAuto) {
            return defaultAutoFilter;
        } else {
            return defaultManualFilter;
        }
    }

    public void setValidator(Operators.Validator<I> validator) {
        if (validator == null) {
            this.validator = (i, all) -> true;
        } else {
            this.validator = validator;
        }
    }

    @Override
    public void onSubscribe(Subscription<I> subscription) {
        this.froms.add(subscription); // 将该节点的from的event加入
    }

    @Override
    public void onProcess(List<FlowContext<I>> preList) {
        try {
            if (CollectionUtils.isEmpty(preList)) {
                return;
            }
            if (preList.size() == 1 && preList.get(0).getData() == null) {
                this.afterProcess(preList, new ArrayList<>());
                return;
            }
            List<FlowContext<O>> afterList = this.getProcessMode().process(this, preList);
            this.afterProcess(preList, afterList);
            if (CollectionUtils.isNotEmpty(afterList)) {
                feedback(afterList); // 查找一个transaction里的所有数据的都完成了，运行callback给stream外反馈数据
                this.onNext(afterList.get(0).getBatchId());
            }
            // 处理好数据后对外送数据，驱动其他flow响应
            afterList.forEach(context -> this.emit(context.getData(), context.getSession()));
        } catch (Exception ex) {
            LOG.error("Node process exception stream-id: {}, node-id: {}, position-id: {}, traceId: {}, errors: {}.",
                    this.streamId, this.id, preList.get(0).getPosition(), preList.get(0).getTraceId(), ex.getMessage());
            LOG.debug("Node process exception details: ", ex);
            Retryable<I> retryable = new Retryable<>(this.getFlowContextRepo(), this);
            Optional.ofNullable(this.errorHandler).ifPresent(handler -> handler.handle(ex, retryable, preList));
            Optional.ofNullable(this.globalErrorHandler).ifPresent(handler -> handler.handle(ex, retryable, preList));
        } finally {
            updateConcurrency(preList, -1);
        }
    }

    private List<FlowContext<I>> filterTerminate(List<FlowContext<I>> contexts) {
        if (CollectionUtils.isEmpty(contexts)) {
            return Collections.emptyList();
        }

        List<String> traceIds = getTraceIds(contexts);
        if (isTracesTerminate(traceIds)) {
            getFlowContextRepo().updateToTerminated(traceIds);
            return Collections.emptyList();
        }
        return contexts;
    }

    private boolean isTracesTerminate(List<String> traceIds) {
        return this.flowContextRepo.isTracesTerminate(traceIds);
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

    private synchronized void updateConcurrency(List<FlowContext<I>> ready, int distance) {
        ready.stream().forEach(c -> c.getTraceId().forEach(traceId -> this.updateConcurrency(traceId, distance)));
    }

    private void updateConcurrency(String traceId, int distance) {
        Integer concurrency = this.traceConcurrency.get(traceId);
        if (concurrency == null) {
            this.traceConcurrency.put(traceId, distance);
            return;
        }
        concurrency += distance;
        if (concurrency == 0) {
            this.traceConcurrency.remove(traceId);
        }
    }

    /**
     * 判断该节点是否达到负载上限
     *
     * @return true-已经满负载， false-未满负载
     */
    public synchronized boolean isOverLimit() {
        return this.traceConcurrency.size() >= MAX_TRACE_CONCURRENCY && this.traceConcurrency.entrySet()
                .stream()
                .allMatch(e -> e.getValue() >= MAX_CONCURRENCY);
    }

    /**
     * 获取满负载的traceIds
     *
     * @return 满负载的traceIds
     */
    public synchronized Set<String> getOverLimitTraceIds() {
        return this.traceConcurrency.entrySet()
                .stream()
                .filter(e -> e.getValue() >= MAX_CONCURRENCY)
                .map(e -> e.getKey())
                .collect(Collectors.toSet());
    }

    /**
     * 节点处理处理完后执行的操作，默认是保存处理之前和之后的对上下文
     *
     * @param preList 当前节点处理之前的context集合
     * @param afterList 当前节点处理之后的新生产的context结合
     */
    @Override
    public void afterProcess(List<FlowContext<I>> preList, List<FlowContext<O>> afterList) {
        updateBatch(preList, afterList);
        Set<String> traces = new HashSet<>();
        preList.forEach(contest -> {
            traces.addAll(contest.getTraceId());
            contest.setStatus(ARCHIVED);
        });
        afterList.forEach(context -> context.getTraceId().addAll(traces));

        if ((Objects.isNull(this.nodeType) || !this.nodeType.equals(FlowNodeType.END)) && !afterList.isEmpty()) {
            this.getFlowContextRepo().updateContextPool(afterList, traces);
            this.getFlowContextRepo().save(afterList);
        }
        this.getFlowContextRepo().update(preList);
        this.getFlowContextRepo()
                .updateStatus(preList, preList.get(0).getStatus().toString(), preList.get(0).getPosition());
    }

    /**
     * 更新contexts的batchID，负责更新当前contexts的toBatch和新contexts的batchId
     * 特殊情况：先有toBatch，再有after的情况
     * 1、平行节点第一个节点处理完会生成toBatch，后续的平行节点拿到同一批context，需要保证同一批toBatch
     * 2、人工任务恢复执行会先生成toBatch，after直接复用外部生成的toBatch
     *
     * @param preList 当前节点处理完的contexts
     * @param afterList 当前节点新生产的contexts
     */
    private void updateBatch(List<FlowContext<I>> preList, List<FlowContext<O>> afterList) {
        if (!Objects.isNull(this.nodeType) && this.nodeType.equals(FlowNodeType.END)) {
            return;
        }
        String toBatch = preList.stream()
                .filter(context -> StringUtils.isNotEmpty(context.getToBatch()))
                .findAny()
                .map(FlowContext::getToBatch)
                .orElseGet(UUIDUtil::uuid);
        preList.forEach(context -> context.toBatch(toBatch));
        afterList.forEach(context -> context.batchId(toBatch));
    }

    @Override
    public void onComplete(Operators.Just<Callback<FlowContext<O>>> callback) {
        To<I, O> me = this;
        this.callback = callback;
    }

    @Override
    public Boolean isAuto() {
        return this.isAuto;
    }

    @Override
    public List<FlowContext<O>> nextContexts(String batchId) {
        return ObjectUtils.cast(
                this.flowContextRepo.getContextsByPosition(this.streamId, this.getId(), batchId, NEW.toString()));
    }

    @Override
    public void onError(Operators.ErrorHandler<I> handler) {
        this.errorHandler = handler;
    }

    @Override
    public void onGlobalError(Operators.ErrorHandler handler) {
        this.globalErrorHandler = handler;
    }

    @Override
    public List<Operators.ErrorHandler> getErrorHandlers() {
        return Stream.of(this.errorHandler, this.globalErrorHandler)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
        List<FlowContext<T1>> contextsByParallel = this.getFlowContextRepo()
                .getContextsByParallel(context.getParallel());
        return contextsByParallel.stream().anyMatch(FlowContext::isJoined);
    }

    @Override
    public String getStreamId() {
        return this.streamId;
    }

    @Override
    public void register(EmitterListener<O, FlowSession> handler) {
        this.listeners.add(handler);
    }

    @Override
    public void emit(O data, FlowSession trans) {
        this.listeners.forEach(listener -> listener.handle(data, trans));
    }

    /**
     * ProcessMode
     *
     * @since 1.0
     */
    public enum ProcessMode {
        PRODUCING {
            @Override
            public <T1, R1> List<FlowContext<R1>> process(To<T1, R1> to, List<FlowContext<T1>> contexts) {
                return to.produce.process(contexts)
                        .stream()
                        .map(data -> contexts.get(0).generate(data, to.getId()))
                        .collect(Collectors.toList());
            }

            @Override
            protected <T1, R1> List<FlowContext<T1>> requestAll(To<T1, R1> to) {
                return to.flowContextRepo.requestProducingContext(to.streamId,
                        to.froms.stream().map(Identity::getId).collect(Collectors.toList()),
                        to.getOverLimitTraceIds(), to.postFilter());
            }
        },
        MAPPING {
            @Override
            public <T1, R1> List<FlowContext<R1>> process(To<T1, R1> to, List<FlowContext<T1>> contexts) {
                // null数据不处理，用于等待外部处理，或者累积节点等待窗口满足场景
                return contexts.stream().filter(context -> context.getData() != null).map(context -> {
                    R1 data = to.map.process(context);
                    // 给window添加没有完成的数据，window符合条件时判断所有数据是否已经在累积节点（reduce)加工结束，否则不能往下流
                    WindowToken windowToken = context.getWindowToken();
                    if (windowToken != null) {
                        windowToken.removeToDo(context.getData());
                        if (!context.isAccumulator()) {
                            windowToken.addToDo(data);
                        } else {
                            // 累积节点尝试往下流转，fire会判断是否满足window条件
                            windowToken.fire();
                        }
                    }
                    return context.generate(data, to.getId());
                }).collect(Collectors.toList());
            }

            @Override
            protected <T1, R1> List<FlowContext<T1>> requestAll(To<T1, R1> to) {
                return to.flowContextRepo.requestMappingContext(to.streamId,
                        to.froms.stream().map(Identity::getId).collect(Collectors.toList()), to.getOverLimitTraceIds(),
                        to.defaultFilter(), to.validator);
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
                Optional<FlowExecutors.ConcurrencyHolder> concurrencyHolder = FlowExecutors.incrementConcurrency();
                if (!concurrencyHolder.isPresent()) {
                    SleepUtil.sleep(SLEEP_MILLS);
                    continue;
                }
                if (to.isOverLimit()) {
                    concurrencyHolder.get().release();
                    SleepUtil.sleep(SLEEP_MILLS);
                    continue;
                }
                boolean isSubmitted = false;
                List<FlowContext<T1>> ready = new ArrayList<>();
                try {
                    ready = requestReady(to);
                    if (CollectionUtils.isEmpty(ready)) {
                        to.processRunning = false;
                        LOG.debug("[{}] process main loop exit for stream-id: {}, node-id: {}",
                                to.getThreadName(To.PROCESS_T_NAME_PREFIX), to.streamId, to.id);
                        handleProcessConcurrentConflict(to);
                        return;
                    }
                    if (to.inParallelMode(ready)) {
                        to.onProcess(ready);
                    } else {
                        this.submit(to, ready, concurrencyHolder.get());
                        isSubmitted = true;
                    }
                } catch (Exception ex) {
                    // 如果是数据库或者redis挂了，会死循环，线程不退出等待数据库或者redis恢复
                    ready.forEach(r -> LOG.error(
                            "Process main loop exception, " + "stream-id: {}, node-id: {}, context-id: {}.",
                            to.streamId, to.id, r.getId()));
                    LOG.debug("Process main loop exception details: ", ex);
                } finally {
                    if (!isSubmitted) {
                        concurrencyHolder.get().release();
                    }
                    SleepUtil.sleep(SLEEP_MILLS);
                }
            }
        }

        /**
         * 查找节点连接的边上所有的contexts，由子类负责实现
         *
         * @param to 本节点节点类
         * @param <T1> 流程实例执行时的入参数据类型，用于泛型推倒
         * @param <R1> 流程实例执行时的出参数据类型，用于泛型推倒
         * @return 获取所有该节点待处理的数据
         */
        protected abstract <T1, R1> List<FlowContext<T1>> requestAll(To<T1, R1> to);

        private <T1, R1> List<FlowContext<T1>> requestReady(To<T1, R1> to) {
            Lock lock = to.locks.getDistributeLock(to.locks.lockKey(to.streamId, to.id, "RequestReady"));
            lock.lock();
            try {
                List<FlowContext<T1>> ready = filterReady(to, requestAll(to));
                ready = to.filterTerminate(ready);
                if (CollectionUtils.isEmpty(ready)) {
                    return new ArrayList<>();
                }
                if (to.isOverLimit()) {
                    throw new WaterflowException(FLOW_NODE_MAX_TASK, to.getId());
                }
                to.updateConcurrency(ready, 1);
                to.flowContextRepo.updateStatus(ready, ready.get(0).getStatus().toString(), ready.get(0).getPosition());
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
         * @return ready的context列表
         */
        private <T1, R1> List<FlowContext<T1>> filterReady(To<T1, R1> to, List<FlowContext<T1>> pre) {
            to.introduceToProcess(pre);
            return pre.stream().filter(context -> context.getStatus() == READY).collect(Collectors.toList());
        }

        private <T1, R1> void submit(To<T1, R1> to, List<FlowContext<T1>> ready,
                FlowExecutors.ConcurrencyHolder concurrencyHolder) {
            FlowExecutors.getThreadPool().execute(Task.builder().runnable(() -> {
                to.onProcess(ready);
                concurrencyHolder.release();
            }).buildDisposable());
        }

        private <T1, R1> void handleProcessConcurrentConflict(To<T1, R1> to) {
            List<FlowContext<T1>> pending = requestAll(to).stream()
                    .filter(context -> !context.getParallelMode().equals(EITHER.name()) || context.isJoined()
                            || !to.isParallelJoined(context))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(pending) || to.inParallelMode(pending)) {
                return;
            }
            LOG.info("[{}] process thread conflict happens for stream-id: {}, node-id: {}",
                    to.getThreadName(To.PROCESS_T_NAME_PREFIX), to.streamId, to.id);
            to.accept(PROCESS, pending);
        }
    }
}
