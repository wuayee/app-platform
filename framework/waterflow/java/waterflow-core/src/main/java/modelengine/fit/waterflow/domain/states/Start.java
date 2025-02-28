/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.states;

import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.context.WindowToken;
import modelengine.fit.waterflow.domain.enums.ParallelMode;
import modelengine.fit.waterflow.domain.flow.Flow;
import modelengine.fit.waterflow.domain.stream.nodes.From;
import modelengine.fit.waterflow.domain.stream.operators.Operators;
import modelengine.fit.waterflow.domain.stream.reactive.Publisher;
import modelengine.fit.waterflow.domain.utils.Identity;
import modelengine.fit.waterflow.domain.utils.Tuple;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 开始节点，包装了 from。
 *
 * @param <O> 表示这个节点的输出数据类型。
 * @param <D> 表示这个节点对应流的初始数据类型。
 * @since 1.0
 */
public class Start<O, D, I, F extends Flow<D>> extends Activity<D, F> {
    /**
     * 表示节点起点的 {@link From}{@code <}{@link O}{@code >}。
     */
    protected final Publisher<O> from;

    /**
     * 构造一个 {@link Start} 实例。
     *
     * @param from 表示节点起点的 {@link From}{@code <}{@link O}{@code >}。
     * @param flow 表的节点对应流的 {@link Flow}{@code <}{@link D}{@code >}。
     */
    public Start(Publisher<O> from, F flow) {
        super(flow);
        this.from = from;
    }

    /**
     * 设置该节点的唯一标识。
     *
     * @param id 表示唯一标识的 {@link String}。
     * @return 表示节点自身的 {@link Start}{@code <}{@link O}{@code ,}
     *         {@link D}{@code ,}{@link I}{@code ,}{@link F}{@code >}。
     */
    public Start<O, D, I, F> id(String id) {
        ObjectUtils.<From>cast(this.from).setId(id);
        return ObjectUtils.cast(super.setId(id));
    }

    /**
     * 获取该节点的 Publisher。
     *
     * @return 表示该节点的 Publisher 的 {@link Publisher}{@code <}{@link O}{@code >}。
     */
    public Publisher<O> publisher() {
        return this.from;
    }

    /**
     * 获取所有订阅该节点的下游节点唯一标识列表。
     *
     * @return 表示下游节点唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public List<String> getSubscriptionsId() {
        return this.from.getSubscriptions().stream().map(Identity::getId).collect(Collectors.toList());
    }

    /**
     * 开启条件节点。
     *
     * @return 表示条件节点的 {@link Conditions}{@code <}{@link D}{@code ,}{@link O}{@code ,}{@link F}{@code >}。
     */
    public Conditions<D, O, F> conditions() {
        return new Conditions<>(new State<>(this.from.conditions(null), this.getFlow()));
    }

    /**
     * 开启并行节点，默认 all 模式，不预处理。
     *
     * @return 表示并行节点的 {@link Parallel}{@code <}{@link D}{@code ,}{@link O}{@code ,}{@link F}{@code >}。
     */
    public Parallel<D, O, F> parallel() {
        return this.parallel(ParallelMode.ALL);
    }

    /**
     * 开启并行节点，不预处理。
     *
     * @param mode 表示并行节点模式的 {@link ParallelMode}。
     * @return 表示并行节点的 {@link Parallel}{@code <}{@link D}{@code ,}{@link O}{@code ,}{@link F}{@code >}。
     */
    private Parallel<D, O, F> parallel(ParallelMode mode) {
        return new Parallel<>(new State<>(this.from.parallel(mode, null), this.getFlow()));
    }

    /**
     * 只处理，不转换。
     *
     * @param processor 表示 just 转换器的 {@link Operators.Just}{@code <}{@link O}{@code >}。
     * @return 表示新的处理节点的 {@link State}{@code <}{@link O}{@code ,}{@link D}
     *         {@code ,}{@link O}{@code , }{@link F}{@code >}。
     */
    public State<O, D, O, F> just(Operators.Just<O> processor) {
        Operators.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData());
        return new State<>(this.from.just(wrapper, null), this.getFlow());
    }

    /**
     * 只处理，不转换。
     *
     * @param processor 表示处理器的 {@link Operators.ProcessJust}{@code <}{@link O}{@code ,}{@link D}{@code >}。
     * @return 表示新的处理节点的 {@link State}{@code <}{@link O}{@code , }{@link D}
     *         {@code , }{@link O}{@code , }{@link F}{@code >}。
     */
    public State<O, D, O, F> just(Operators.ProcessJust<O> processor) {
        Operators.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData(), input);
        return new State<>(this.from.just(wrapper, null), this.getFlow());
    }

    /**
     * 处理，并转换类型。
     *
     * @param processor 表示 map 处理器的 {@link Operators.Map}{@code <}{@link O}{@code ,}{@link R}{@code >}。
     * @return 表示新的处理节点的 {@link State}{@code <}{@link R}{@code , }{@link D}{@code , }
     *         {@link O}{@code , }{@link F}{@code >}。
     */
    public <R> State<R, D, O, F> map(Operators.Map<O, R> processor) {
        Operators.Map<FlowContext<O>, R> wrapper = input -> processor.process(input.getData());
        return new State<>(this.from.map(wrapper, null), this.getFlow());
    }

    /**
     * 处理，并往下发射新的数据，支持操作 session KV 状态数据。
     *
     * @param processor 表示携带数据、KV 下文和发射器的处理器的{@link Operators.Process}{@code <}{@link O}
     *                  {@code ,}{@link R}{@code >}}。
     * @return 表示新的处理节点的 {@link State}{@code <}{@link R}{@code ,}{@link D}
     *         {@code ,}{@link O}{@code ,}{@link F}{@code >}。
     */
    public <R> State<R, D, O, F> process(Operators.Process<O, R> processor) {
        AtomicReference<State<R, D, O, F>> wrapper = new AtomicReference<>();
        State<R, D, O, F> state = new State<>(this.publisher().map(input -> {
            processor.process(input.getData(), input, data -> wrapper.get().from.offer(data, input.getSession()));
            return null;
        }, null), this.getFlow());
        wrapper.set(state);
        return state;
    }

    /**
     * 处理，并转换类型。
     *
     * @param processor 表示携带 KV 下文的 map 处理器的 {@link Operators.ProcessMap}{@code <}{@link O}
     *                  {@code ,}{@link R}{@code >}。
     * @return 表示新的处理节点的 {@link State}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}
     *         {@code , }{@link F}{@code >}。
     */
    public <R> State<R, D, O, F> map(Operators.ProcessMap<O, R> processor) {
        Operators.Map<FlowContext<O>, R> wrapper = input -> processor.process(input.getData(), input);
        return new State<>(this.from.map(wrapper, null), this.getFlow());
    }

    /**
     * 处理，并转换类型。
     *
     * @param processor 表示 flat map 处理器的 {@link Operators.FlatMap}{@code <}{@link O}{@code ,}{@link R}{@code >}。
     * @return 表示新的处理节点的 {@link State}{@code <}{@link R}{@code , }{@link D}
     *         {@code , }{@link O}{@code ,}{@link F}{@code >}。
     */
    public <R> State<R, D, O, F> flatMap(Operators.FlatMap<O, R> processor) {
        Validation.notNull(processor, "Flat map processor can not be null.");
        Operators.FlatMap<FlowContext<O>, R> wrapper = input -> processor.process(input.getData());
        return new State<>(this.from.flatMap(wrapper, null).displayAs("flat map"), this.getFlow());
    }

    /**
     * 缓存流中的数据。
     * <p>
     * 通常出现在 window 后，当满足 window 的条件后，为后续节点提供 window 中缓存的数据列表。
     * </p>
     *
     * @return 表示缓存后的节点的 {@link State}{@code <}{@link List}{@code <}{@link O}{@code >}{@code ,}
     *         {@link D}{@code ,}{@link O}{@code >}{@code,}{@link F}{@code >}。
     */
    public State<List<O>, D, O, F> buffer() {
        State<List<O>, D, O, F> state = this.reduce(null, (acc, cur) -> {
            if (acc == null) {
                acc = new ArrayList<>();
            }
            acc.add(cur);
            return acc;
        });
        state.processor.displayAs("buffer");
        return state;
    }

    /**
     * 生成一个数据聚合节点，将每个数据通过指定的方式进行合并后，形成一个新的数据，并继续发送。
     * <p>
     * 不提供初始值，聚合之后的数据类型还是原数据类型。
     * </p>
     *
     * @param processor 表示数据聚合器的 {@link Operators.ProcessReduce}{@code <}{@link O}{@code , }{@link O}{@code >}。
     * @return 表示数据聚合节点的 {@link State}{@code <}{@link O}{@code , }{@link D}{@code , }
     *         {@link O}{@code ,}{@link F}{@code >}。
     */
    public State<O, D, O, F> reduce(Operators.Reduce<O, O> processor) {
        return this.reduce(null, processor);
    }

    /**
     * 生成一个数据聚合节点，将每个数据通过指定的方式进行合并后，形成一个新的数据，并继续发送。
     * <p>
     * 处理后的数据类型是根据初始值来确认。
     * </p>
     *
     * @param init 表示聚合操作初始值提供者的 {@link Supplier}{@code <}{@link R}{@code >}。
     * @param processor 表示数据聚合器的 {@link Operators.ProcessReduce}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @return 表示数据聚合节点的 {@link State}{@code <}{@link R}{@code , }{@link D}{@code , }
     *         {@link O}{@code ,}{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> State<R, D, O, F> reduce(Supplier<R> init, Operators.Reduce<O, R> processor) {
        Operators.ProcessReduce<O, R> wrapper = (acc, input, context) -> processor.process(acc, input);
        return this.reduce(init, wrapper);
    }

    /**
     * 生成一个数据聚合节点，将每个数据通过指定的方式进行合并后，形成一个新的数据，并继续发送。支持操作会话的自定义状态数据。
     *
     * @param init 表示初始值提供者的 {@link Supplier}{@code <}{@link R}{@code >}。
     * @param processor 表示数据聚合器的 {@link Operators.ProcessReduce}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示输出数据类型。
     * @return 表示数据聚合节点的 {@link State}{@code <}{@link R}{@code , }{@link D}{@code , }
     *         {@link O}{@code ,}{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> State<R, D, O, F> reduce(Supplier<R> init, Operators.ProcessReduce<O, R> processor) {
        Validation.notNull(processor, "Reduce processor can not be null.");
        Supplier<R> actualInit = ObjectUtils.nullIf(init, () -> null);

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
                R acc = Optional.ofNullable(accs.get(key)).map(Tuple::second).orElseGet(actualInit);
                acc = processor.process(acc, input.getData(), input);
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
        stateWrapper.set(new State<>(this.from.map(wrapper, null).displayAs("reduce"), this.getFlow()));
        return stateWrapper.get();
    }

    /**
     * 形成一个 window，window 中的数据满足条件后，将触发后续的数据聚合处理。
     *
     * @param window 表示 window 条件的 {@link Operators.Window}{@code <}{@link O}{@code >}。
     * @return 表示 window 的后续节点的 {@link State}{@code <}{@link O}{@code , }{@link D}{@code , }
     *         {@link O}{@code ,}{@link F}{@code >}。
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
        return new State<>(this.from.just(wrapper, null).displayAs("window"), this.getFlow());
    }

    /**
     * 对数据流进行分组处理，根据指定的 keyGetter 获取的键进行分组。
     *
     * @param keyGetter 表示提供聚合键的 {@link Operators.Map}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @return 表示聚合后的节点的 {@link Tuple}{@code <}{@link R}{@code , }{@link O}{@code >}。
     */
    public <R> State<Tuple<R, O>, D, O, F> keyBy(Operators.Map<O, R> keyGetter) {
        Operators.Map<FlowContext<O>, Tuple<R, O>> wrapper = input -> {
            R key = keyGetter.process(input.getData());
            input.setKeyBy(key);
            return Tuple.from(key, input.getData());
        };
        return new State<>(this.from.map(wrapper, null).displayAs("key by"), this.getFlow());
    }

    /**
     * 生成一个数据处理节点，将每个数据通过指定的方式进行处理后，形成一个新的数据，并继续发送。
     *
     * @param processor 表示处理器的 {@link Operators.Produce}{@code <}{@link O}{@code ,}{@link R}{@code >}。
     * @return 表示新的处理节点的 {@link State}{@code <}{@link List}{@code <}{@link R}{@code >}
     *         {@code ,}{@link D}{@code ,?,}{@link F}{@code >}
     */
    public <R> State<List<R>, D, ?, F> produce(Operators.Produce<O, R> processor) {
        return this.buffer().map(contexts -> processor.process(contexts));
    }
}
