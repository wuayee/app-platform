/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.activities;

import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fit.waterflow.domain.states.Start;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.utils.Tuple;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.engine.flows.AiFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 流程的开始节点。
 *
 * @param <O> 表示开始节点的输出数据类型。
 * @param <D> 表示流程的初始数据类型。
 * @param <I> 表示入参数据类型。
 * @param <RF> 表示内部数据流程类型，是 {@link Flow}{@code <}{@link D}{@code >} 的扩展。
 * @param <F> 表示 AI 流程的类型，是 {@link AiFlow}{@code <}{@link D}{@code , }{@link RF}{@code >} 的扩展。
 * @author 刘信宏
 * @since 2024-04-28
 */
public class AiStart<O, D, I, RF extends Flow<D>, F extends AiFlow<D, RF>> extends AiActivity<D, RF, F> {
    private final Start<O, D, I, RF> start;

    /**
     * AI 流程开始节点的构造方法。
     *
     * @param start 表示被装饰的流程开始节点的 {@link Start}{@code <}{@link O}{@code , }{@link D}{@code , }{@link I}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @param flow 表示 AI 流程对象的 {@link F}。
     * @throws IllegalArgumentException 当 {@code start} 为 {@code null} 时。
     */
    public AiStart(Start<O, D, I, RF> start, F flow) {
        super(flow);
        this.start = Validation.notNull(start, "Start node cannot be null.");
    }

    /**
     * 设置节点别名。
     *
     * @param id 表示待设置的节点名称的 {@link String}。
     * @return 表示设置好别名的当前节点的 {@link AiStart}{@code <}{@link O}{@code , }{@link D}{@code , }{@link I}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code id} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public AiStart<O, D, I, RF, F> id(String id) {
        Validation.notBlank(id, "Node id cannot be blank.");
        this.start.id(id);
        return this;
    }

    /**
     * 获取流程的数据发布者。
     *
     * @return 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
     */
    public Publisher<O> publisher() {
        return this.start.publisher();
    }

    /**
     * 将每个数据通过指定的方式进行加工后继续流转，只处理数据，不转换类型。
     *
     * @param processor 表示数据处理器的 {@link Operators.Just}{@code <}{@link O}{@code >}。
     * @return 表示数据加工节点的 {@link AiState}{@code <}{@link O}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public AiState<O, D, O, RF, F> just(Operators.Just<O> processor) {
        Validation.notNull(processor, "Just processor cannot be null.");
        return new AiState<>(this.start.just(processor), this.getFlow());
    }

    /**
     * 将每个数据通过指定的方式进行加工后继续流转，只处理数据，不转换类型，处理器内可消费自定义上下文。
     *
     * @param processor 表示数据处理器的 {@link Operators.ProcessJust}{@code <}{@link O}{@code >}，捕获了从
     * {@link com.huawei.jade.fel.engine.flows.Conversation#bind(String, Object)} 绑定的自定义上下文。
     * @return 表示数据加工节点的 {@link AiState}{@code <}{@link O}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public AiState<O, D, O, RF, F> just(Operators.ProcessJust<O> processor) {
        Validation.notNull(processor, "Just processor cannot be null.");
        return new AiState<>(this.start.just(processor), this.getFlow());
    }

    /**
     * 将每个数据通过指定的方式进行转换后继续流转，处理数据，同时可以转换类型。
     *
     * @param processor 表示数据处理器的 {@link Operators.Map}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示数据转换节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> map(Operators.Map<O, R> processor) {
        Validation.notNull(processor, "Map processor cannot be null.");
        return new AiState<>(this.start.map(processor), this.getFlow());
    }

    /**
     * 将每个数据通过指定的方式进行加工和转换后继续流转，同时可以转换类型，处理器内可消费自定义上下文。
     *
     * @param processor 表示数据处理器的 {@link Operators.Map}{@code <}{@link O}{@code , }{@link R}{@code >}，
     * 捕获了从 {@link com.huawei.jade.fel.engine.flows.Conversation#bind(String, Object)} 绑定的自定义上下文。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示数据转换节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> map(Operators.ProcessMap<O, R> processor) {
        Validation.notNull(processor, "Map processor cannot be null.");
        return new AiState<>(this.start.map(processor), this.getFlow());
    }

    /**
     * 生成一个数据聚合节点，将每个数据通过指定的方式进行合并后，形成一个新的数据，并继续发送。
     * <p>如果流程是批量注入数据，那么聚合节点会将同一批次的数据聚合。</p>
     * <p>如果流程是逐个注入数据，那么需要配合 {@link AiStart#window(Operators.Window)} 表达式使用，才会聚合数据。</p>
     *
     * @param init 表示聚合操作的初始值的 {@link R}，当 {@code init} 为 {@code null} 时，表示聚合之后的数据类型还是原数据类型。
     * @param processor 表示数据聚合器的 {@link Operators.Reduce}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示数据聚合节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> reduce(R init, Operators.Reduce<O, R> processor) {
        Validation.notNull(processor, "Reduce processor cannot be null.");
        return new AiState<>(this.start.reduce(init, processor), this.getFlow());
    }

    /**
     * 生成一个数据聚合节点， 将每个数据通过指定的方式进行合并后，形成一个新的数据，并继续发送。处理器内可消费自定义上下文。
     * <p>如果流程是批量注入数据，那么聚合节点会将同一批次的数据聚合。</p>
     * <p>如果流程是逐个注入数据，那么需要配合 {@link AiStart#window(Operators.Window)} 表达式使用，才会聚合数据。</p>
     *
     * @param init 表示聚合操作的初始值的 {@link R}，当 {@code init} 为 {@code null} 时，表示聚合之后的数据类型还是原数据类型。
     * @param processor 表示数据聚合器的 {@link Operators.Reduce}{@code <}{@link O}{@code , }{@link R}{@code >}，
     * 捕获了从 {@link com.huawei.jade.fel.engine.flows.Conversation#bind(String, Object)} 绑定的自定义上下文。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示数据聚合节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> reduce(R init, Operators.ProcessReduce<O, R> processor) {
        Validation.notNull(processor, "Reduce processor cannot be null.");
        return new AiState<>(this.start.reduce(init, processor), this.getFlow());
    }

    /**
     * 形成一个窗口节点。需要与数据聚合节点配合使用，窗口节点单独使用不生效。
     * <p>当窗口节点中的数据满足条件后，窗口关闭，后续的数据聚合节点才会将聚合的数据往下发送，在此之前， 数据聚合节点将持续聚合数据。</p>
     *
     * @param window 表示窗口处理器的 {@link Operators.Window}{@code <}{@link O}{@code >}。
     * @return 表示窗口节点的 {@link AiState}{@code <}{@link O}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code window} 为 {@code null} 时。
     */
    public AiState<O, D, O, RF, F> window(Operators.Window<O> window) {
        Validation.notNull(window, "Window operator cannot be null.");
        return new AiState<>(this.start.window(window), this.getFlow());
    }

    /**
     * 设置分组聚合的键，需要配合 {@link AiStart#window(Operators.Window)}、
     * {@link AiStart#reduce(Object, Operators.Reduce)} 和 {@link AiStart#reduce(Object, Operators.ProcessReduce)} 使用，
     * 后续的聚合操作按指定的键分组处理。
     *
     * @param keyBy 表示分组配置器的 {@link Operators.Map}{@code <}{@link O}{@code , }{@link R}{@code >}，提供分组聚合的键。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示数据分组节点的 {@link AiState}{@code <}{@link Tuple}{@code <}{@link R}{@code ,
     * }{@link O}{@code >}{@code , }{@link D}{@code , }{@link O}{@code , }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code keyBy} 为 {@code null} 时。
     */
    public <R> AiState<Tuple<R, O>, D, O, RF, F> keyBy(Operators.Map<O, R> keyBy) {
        Validation.notNull(keyBy, "KeyBy operator cannot be null.");
        return new AiState<>(this.start.keyBy(keyBy), this.getFlow());
    }

    /**
     * 按指定容量缓存流中的数据，为后续节点提供缓存的数据列表。
     *
     * @param size 表示待指定的数据缓存容量的 {@code int}。
     * @return 表示数据缓存节点的 {@link AiState}{@code <}{@link List}{@code <}{@link O}{@code >}{@code ,
     * }{@link D}{@code , }{@link O}{@code , }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code size} 不大于0时。
     */
    public AiState<List<O>, D, O, RF, F> buffer(int size) {
        Validation.isTrue(size > 0, "Buffer size must be greater than 0.");
        return this.window(inputs -> inputs.size() == size).reduce(null, (acc, input) -> {
            acc = ObjectUtils.getIfNull(acc, ArrayList::new);
            acc.add(input);
            return acc;
        });
    }

    /**
     * 自定义数据处理器，支持往后续的节点发射自定义数据。
     *
     * @param processor 表示自定义数据处理器的 {@link Operators.Process}{@code <}{@link O}{@code , }{@link R}{@code >}，
     * 捕获了从 {@link com.huawei.jade.fel.engine.flows.Conversation#bind(String, Object)} 绑定的自定义上下文，
     * 支持往后续的节点发射自定义数据。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示自定义数据处理节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> process(Operators.Process<O, R> processor) {
        Validation.notNull(processor, "Process operator cannot be null.");
        return new AiState<>(this.start.process(processor), this.getFlow());
    }
}
