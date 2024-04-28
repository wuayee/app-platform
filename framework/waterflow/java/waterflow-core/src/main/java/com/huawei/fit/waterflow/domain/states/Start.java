/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.states;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.context.WindowToken;
import com.huawei.fit.waterflow.domain.enums.ParallelMode;
import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.utils.Identity;
import com.huawei.fit.waterflow.domain.utils.Tuple;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 开始节点，包装了from
 *
 * @param <O> 这个节点的输出数据类型
 * @param <D> 这个节点对应flow的初始数据类型
 * @since 1.0
 */
public class Start<O, D, I, F extends Flow<D>> extends Activity<D, F> {
    /**
     * from
     */
    protected final Publisher<O> from;

    public Start(Publisher<O> from, F flow) {
        super(flow);
        this.from = from;
    }

    /**
     * 设置该节点的别名id
     *
     * @param id 别名id
     * @return 节点自身
     */
    public Start<O, D, I, F> id(String id) {
        return ObjectUtils.cast(super.id(id));
    }

    /**
     * 获取该节点的publisher
     *
     * @return publisher
     */
    public Publisher<O> publisher() {
        return this.from;
    }

    /**
     * 获取所有订阅改节点的下游节点id列表
     *
     * @return 下游节点id列表
     */
    public List<String> getSubscriptionsId() {
        return this.from.getSubscriptions().stream().map(Identity::getId).collect(Collectors.toList());
    }

    /**
     * 开启条件节点
     *
     * @return 条件节点
     */
    public Conditions<D, O, F> conditions() {
        return new Conditions<>(new State<>(this.from.conditions(null, null), this.getFlow()));
    }

    /**
     * 开启平行节点,默认all模式,啥也不预处理
     *
     * @return 平行节点
     */
    public Parallel<D, O, F> parallel() {
        return this.parallel(ParallelMode.ALL);
    }

    /**
     * 开启平行节点,啥也不预处理
     *
     * @param mode 平行节点模式：Either还是All
     * @return 平行节点
     */
    private Parallel<D, O, F> parallel(ParallelMode mode) {
        return new Parallel<>(new State<>(this.from.parallel(mode, null, null), this.getFlow()));
    }

    /**
     * just，只处理，不转换
     *
     * @param processor just转换器
     * @return 新的处理节点
     */
    public State<O, D, O, F> just(Operators.Just<O> processor) {
        Operators.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData());
        return new State<>(this.from.just(wrapper, null, null), this.getFlow());
    }

    /**
     * just，只处理，不转换
     *
     * @param processor 携带session KV状态数据的just转换器
     * @return 新的处理节点
     */
    public State<O, D, O, F> just(Operators.ProcessJust<O> processor) {
        Operators.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData(), input);
        return new State<>(this.from.just(wrapper, null, null), this.getFlow());
    }

    /**
     * map,处理，并转换类型
     *
     * @param processor map处理器
     * @param <R> 处理完类型
     * @return 新的处理节点
     */
    public <R> State<R, D, O, F> map(Operators.Map<O, R> processor) {
        Operators.Map<FlowContext<O>, R> wrapper = input -> processor.process(input.getData());
        return new State<>(this.from.map(wrapper, null, null), this.getFlow());
    }

    /**
     * process处理，并往下发射新的数据，支持操作session KV状态数据
     *
     * @param processor 携带数据、KV下文和发射器的处理器
     * @return 新的处理节点
     */
    public <R> State<R, D, O, F> process(Operators.Process<O, R> processor) {
        AtomicReference<State<R, D, O, F>> wrapper = new AtomicReference<>();
        State<R, D, O, F> state = new State<>(this.publisher().map(input -> {
            processor.process(input.getData(), input, data -> wrapper.get().from.offer(data, input.getSession()));
            return null;
        }, null, null), this.getFlow());
        wrapper.set(state);
        return state;
    }

    /**
     * map处理，并转换类型
     *
     * @param processor 携带KV下文的map处理器
     * @param <R> 处理完类型
     * @return 新的处理节点
     */
    public <R> State<R, D, O, F> map(Operators.ProcessMap<O, R> processor) {
        Operators.Map<FlowContext<O>, R> wrapper = input -> processor.process(input.getData(), input);
        return new State<>(this.from.map(wrapper, null, null), this.getFlow());
    }

    /**
     * flat map,处理，1变多，并转换类型
     *
     * @param processor flat map处理器
     * @param <R> 处理完类型
     * @return 新的处理节点
     */
    public <R> State<R, D, O, F> flatMap(Operators.FlatMap<O, R> processor) {
        AtomicReference<State<R, D, O, F>> state = new AtomicReference<>();
        Operators.Map<FlowContext<O>, R> wrapper = input -> {
            DataStart<R, R, ?> start = processor.process(input.getData());
            start.just(ctx -> state.get().from.offer(ctx)).offer();
            return null;
        };
        state.set(new State<>(this.from.map(wrapper, null, null), this.getFlow()));
        return state.get();
    }

    /**
     * 缓存流中的数据
     * <p>
     * 通常出现在window后，当满足window的条件后，为后续节点提供window中缓存的数据列表
     * </p>
     *
     * @return buffer后的节点
     */
    public State<List<O>, D, O, F> buffer() {
        return this.reduce(null, (acc, cur) -> {
            if (acc == null) {
                acc = new ArrayList<>();
            }
            acc.add(cur);
            return acc;
        });
    }

    /**
     * reduce处理节点：m->1
     * <p>
     * 不提供初始值，reduce之后的数据类型还是原数据类型
     * </p>
     *
     * @param processor reduce处理器
     * @return 新的处理节点
     */
    public State<O, D, O, F> reduce(Operators.Reduce<O, O> processor) {
        return this.reduce(null, processor);
    }

    /**
     * reduce处理节点：m->1
     * <p>
     * 处理后的数据类型是根据初始值来确认
     * </p>
     *
     * @param init 初始值
     * @param processor reduce处理器
     * @param <R> 通过初始值指定的处理完类型
     * @return 新的处理节点
     */
    public <R> State<R, D, O, F> reduce(R init, Operators.Reduce<O, R> processor) {
        Operators.ProcessReduce<O, R> wrapper = (acc, input, context) -> processor.process(acc, input);
        return this.reduce(init, wrapper);
    }

    /**
     * reduce处理节点：m->1, 支持操作session KV状态数据
     *
     * @param init 初始值
     * @param processor reduce处理器
     * @param <R> 处理完类型
     * @return 新的处理节点
     */
    public <R> State<R, D, O, F> reduce(R init, Operators.ProcessReduce<O, R> processor) {
        // 有keyby的情况下，累加器按照key分开计算
        Map<Object, Tuple<FlowSession, R>> globalAccs = new HashMap<>();
        AtomicReference<State<R, D, O, F>> stateWrapper = new AtomicReference<>();
        Operators.Map<FlowContext<O>, R> wrapper = new Operators.Map<FlowContext<O>, R>() {
            @Override
            public synchronized R process(FlowContext<O> input) {
                input.setAsAccumulator();
                Object key = input.keyBy();
                WindowToken windowToken = input.getWindowToken();
                Map<Object, Tuple<FlowSession, R>> accs = globalAccs;
                if (windowToken != null) {
                    accs = windowToken.accs();
                }
                R acc;
                acc = processor.process(accs.get(key) == null ? init : accs.get(key).second(), input.getData(), input);
                accs.put(key, Tuple.from(input.getSession(), acc));
                if (windowToken == null) {
                    return acc;
                } else {
                    windowToken.setProcessor(stateWrapper.get().from);
                    // 有window支持情况下不返回，直到window关闭时返回最终累加值
                    return null;
                }
            }
        };
        stateWrapper.set(new State<>(this.from.map(wrapper, null, null), this.getFlow()));
        return stateWrapper.get();
    }

    /**
     * 形成一个window，window中的数据满足条件后，将触发后续的数据聚合处理
     *
     * @param window window的条件
     * @return window的后续节点
     */
    public State<O, D, O, F> window(Operators.Window<O> window) {
        final Map<Object, WindowToken<O>> windowTokens = new HashMap<>();
        Operators.Just<FlowContext<O>> wrapper = new Operators.Just<FlowContext<O>>() {
            @Override
            public synchronized void process(FlowContext<O> input) {
                Object tokenKey = window.getSessionKey(input);
                WindowToken<O> windowToken = windowTokens.get(tokenKey);
                if (windowToken == null) {
                    windowToken = new WindowToken<>(window);
                    windowTokens.put(tokenKey, windowToken);
                }

                windowToken.addToDo(input.getData());
                windowToken.addOrigin(input.getData());
                input.setWindowToken(windowToken);
                if (windowToken.fulfilled()) {
                    windowTokens.remove(tokenKey);
                }
            }
        };
        return new State<>(this.from.just(wrapper, null, null), this.getFlow());
    }

    /**
     * 聚合处理
     *
     * @param keyGetter 提供聚合的key
     * @param <R> 聚合后的key类型
     * @return 聚合后的节点，其数据类型为 {@link Tuple}
     */
    public <R> State<Tuple<R, O>, D, O, F> keyBy(Operators.Map<O, R> keyGetter) {
        Operators.Map<FlowContext<O>, Tuple<R, O>> wrapper = input -> {
            R key = keyGetter.process(input.getData());
            input.setKeyBy(key);
            return Tuple.from(key, input.getData());
        };
        return new State<>(this.from.map(wrapper, null, null), this.getFlow());
    }

    /**
     * produce处理节点：m->n
     *
     * @param processor produce处理器
     * @param <R> 处理完类型
     * @return 新的处理节点
     */
    public <R> State<List<R>, D, ?, F> produce(Operators.Produce<O, R> processor) {
        return this.buffer().map(contexts -> processor.process(contexts));
    }
}
