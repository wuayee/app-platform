/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.activities;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fit.waterflow.domain.flow.Flows;
import com.huawei.fit.waterflow.domain.states.Start;
import com.huawei.fit.waterflow.domain.states.State;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.stream.operators.SessionWindow;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.utils.Tuple;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.core.Pattern;
import com.huawei.jade.fel.core.formatters.Parser;
import com.huawei.jade.fel.core.model.BlockModel;
import com.huawei.jade.fel.core.retriever.Indexer;
import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.core.retriever.Splitter;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.activities.processors.AiBranchProcessor;
import com.huawei.jade.fel.engine.activities.processors.AiFlatMap;
import com.huawei.jade.fel.engine.flows.AiFlow;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.models.ChatChunk;
import com.huawei.jade.fel.engine.operators.models.StreamModel;
import com.huawei.jade.fel.engine.operators.patterns.AbstractFlowPattern;
import com.huawei.jade.fel.engine.operators.patterns.FlowPattern;
import com.huawei.jade.fel.engine.operators.patterns.SimpleFlowPattern;
import com.huawei.jade.fel.engine.operators.prompts.PromptTemplate;
import com.huawei.jade.fel.engine.util.AiFlowSession;
import com.huawei.jade.fel.engine.util.StateKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

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
     * 将每个数据通过指定的方式进行转换后继续流转，处理数据同时可以转换类型。
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
     * 将每个数据通过指定的方式转换为一个数据流，并将数据流的数据往下发射流转。
     *
     * @param processor 表示数据处理器的 {@link AiFlatMap}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示数据转换节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> flatMap(AiFlatMap<O, R> processor) {
        return new AiState<>(this.start.flatMap(input -> processor.process(input).toDataStart()), this.getFlow());
    }

    /**
     * 生成一个数据聚合节点，将每个数据通过指定的方式进行合并后，形成一个新的数据，并继续发送。
     * <p>如果流程是批量注入数据，那么聚合节点会将同一批次的数据聚合。</p>
     * <p>如果流程是逐个注入数据，那么需要配合 {@link AiStart#window(Operators.Window)} 表达式使用，才会聚合数据。</p>
     *
     * @param init 表示聚合操作初始值提供者的 {@link Supplier}{@code <}{@link R}{@code >}，当 {@code init} 为 {@code null}
     * 时，表示聚合之后的数据类型还是原数据类型。
     * @param processor 表示数据聚合器的 {@link Operators.Reduce}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示数据聚合节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> reduce(Supplier<R> init, Operators.Reduce<O, R> processor) {
        Validation.notNull(processor, "Reduce processor cannot be null.");
        return new AiState<>(this.start.reduce(init, processor), this.getFlow());
    }

    /**
     * 生成一个数据聚合节点， 将每个数据通过指定的方式进行合并后，形成一个新的数据，并继续发送。处理器内可消费自定义上下文。
     * <p>如果流程是批量注入数据，那么聚合节点会将同一批次的数据聚合。</p>
     * <p>如果流程是逐个注入数据，那么需要配合 {@link AiStart#window(Operators.Window)} 表达式使用，才会聚合数据。</p>
     *
     * @param init 表示聚合操作初始值提供者的 {@link Supplier}{@code <}{@link R}{@code >}，当 {@code init} 为 {@code null}
     * 时，表示聚合之后的数据类型还是原数据类型。
     * @param processor 表示数据聚合器的 {@link Operators.Reduce}{@code <}{@link O}{@code , }{@link R}{@code >}，
     * 捕获了从 {@link com.huawei.jade.fel.engine.flows.Conversation#bind(String, Object)} 绑定的自定义上下文。
     * @param <R> 表示新节点的输出数据类型。
     * @return 表示数据聚合节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> reduce(Supplier<R> init, Operators.ProcessReduce<O, R> processor) {
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
     * {@link AiStart#reduce(Supplier, Operators.Reduce)} 和 {@link AiStart#reduce(Supplier, Operators.ProcessReduce)}
     * 使用，后续的聚合操作按指定的键分组处理。
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
     * 生成自定义数据处理器，支持往后续的节点发射自定义数据。
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

    /**
     * 创建条件节点。
     *
     * @return 表示条件节点的 {@link AiConditions}{@code <}{@link D}{@code , }{@link O}{@code , }{@link RF}{@code ,
     * }{@link F}{@code >}。
     */
    public AiConditions<D, O, RF, F> conditions() {
        return new AiConditions<>(this.start.conditions(), this.getFlow());
    }

    /**
     * 生成数据检索节点，用于 RAG 流程。
     *
     * @param retriever 表示数据检索器的 {@link Retriever}{@code <}{@link R}{@code >}。
     * @return 表示数据检索节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code ,
     * }{@link O}{@code , }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code TextRetriever} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> retrieve(Retriever<O, R> retriever) {
        Validation.notNull(retriever, "Retriever operator cannot be null.");
        return new AiState<>(new State<>(this.publisher()
                .map(input -> retriever.invoke(input.getData()), null).displayAs("retrieve"),
                this.getFlow().origin()), this.getFlow());
    }

    /**
     * 生成文本切分节点。
     *
     * @param splitter 表示文本切分算子的 {@link Splitter}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示文本切分节点的输出数据类型。
     * @return 表示文本切分节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code splitter} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> split(Splitter<O, R> splitter) {
        Validation.notNull(splitter, "Splitter operator cannot be null.");
        AiState<R, D, O, RF, F> state = this.map(splitter::split);
        ((Processor<?, ?>) state.publisher()).displayAs("splitter");
        return state;
    }

    /**
     * 生成索引节点。
     *
     * @param indexer 表示索引算子的 {@link Splitter}{@code <}{@link O}{@code >}。
     * @return 表示索引节点的 {@link AiState}{@code <}{@link O}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code indexer} 为 {@code null} 时。
     */
    public AiState<O, D, O, RF, F> index(Indexer<O> indexer) {
        Validation.notNull(indexer, "Indexer operator cannot be null.");
        AiState<O, D, O, RF, F> state = this.just(indexer::process);
        ((Processor<?, ?>) state.publisher()).displayAs("indexer");
        return state;
    }

    /**
     * 将模型处理返回值的格式化解析。
     *
     * @param parser 表示格式化解析器的 {@link Parser}{@code <}{@link R}{@code >}。
     * @return 表示格式化解析节点的 {@link AiState}{@code <}{@link O}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code parser} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> format(Parser<O, R> parser) {
        Validation.notNull(parser, "Parser operator cannot be null.");
        AiState<R, D, O, RF, F> state = this.map(parser::parse);
        ((Processor<?, ?>) state.publisher()).displayAs("format");
        return state;
    }

    /**
     * 将数据委托给 {@link Pattern}{@code <}{@link O}{@code , }{@link R}{@code >}
     * 处理，然后自身放弃处理数据。处理后的数据会发送回该节点，作为该节点的处理结果。
     *
     * @param pattern 表示异步委托单元的 {@link Pattern}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示委托节点的输出数据类型。
     * @return 表示委托节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code pattern} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> delegate(Pattern<O, R> pattern) {
        Validation.notNull(pattern, "Pattern operator cannot be null.");
        FlowPattern<O, R> flowPattern = this.castFlowPattern(pattern);
        Processor<O, R> processor = this.publisher().map(input -> {
            AiFlowSession.applyPattern(flowPattern, input.getData(), input.getSession());
            return null;
        }, null);
        this.displayPatternProcessor(pattern, processor);
        AiState<R, D, O, RF, F> state = new AiState<>(new State<>(processor, this.getFlow().origin()), this.getFlow());
        state.offer(flowPattern);
        return state;
    }

    private <R> void displayPatternProcessor(Pattern<O, R> pattern, Processor<O, R> processor) {
        if (pattern instanceof AbstractFlowPattern) {
            Flow<O> originFlow = ObjectUtils.<AbstractFlowPattern<O, R>>cast(pattern).origin();
            processor.displayAs("delegate to flow", originFlow, originFlow.start().getId());
        } else {
            processor.displayAs("delegate to pattern");
        }
    }

    private <R> FlowPattern<O, R> castFlowPattern(Pattern<O, R> pattern) {
        if (pattern instanceof FlowPattern) {
            return ObjectUtils.cast(pattern);
        } else {
            return new SimpleFlowPattern<>(pattern);
        }
    }

    /**
     * 将数据委托给 {@link Operators.ProcessMap}{@code <}{@link O}{@code , }{@link R}{@code >}
     * 处理，然后自身放弃处理数据。处理后的数据会发送回该节点，作为该节点的处理结果。
     * <p>
     * 数据接收方 {@code operator} 将在 {@link SimpleFlowPattern}{@code <}{@link O}{@code , }{@link R}{@code >}
     * 里面的独立线程池中执行，不占用委托节点的线程。一般用于 {@code operator} 为 IO 密集型任务的场景。
     * </p>
     *
     * @param operator 表示数据接收方的 {@link Operators.ProcessMap}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示委托节点的输出数据类型。
     * @return 表示委托节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code operator} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> delegate(Operators.ProcessMap<O, R> operator) {
        Validation.notNull(operator, "Pattern operator cannot be null.");
        return this.delegate(new SimpleFlowPattern<>(operator));
    }

    /**
     * 将数据委托给 {@link AiProcessFlow}{@code <}{@link O}{@code , }{@link R}{@code >}
     * 处理，然后自身放弃处理数据。处理后的数据会发送回该节点，作为该节点的处理结果。
     *
     * @param aiFlow 表示子流程的 {@link AiProcessFlow}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param <R> 表示委托节点的输出数据类型。
     * @return 表示委托节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code aiFlow} 为 {@code null} 时。
     */
    public <R> AiState<R, D, O, RF, F> delegate(AiProcessFlow<O, R> aiFlow) {
        Validation.notNull(aiFlow, "Flow cannot be null.");
        Processor<O, R> processor = this.publisher().map(input -> {
            aiFlow.converse(input.getSession()).offer(input.getData());
            return (R) null;
        }, null).displayAs("delegate to flow", aiFlow.origin(), aiFlow.origin().start().getId());
        AiState<R, D, O, RF, F> state = new AiState<>(new State<>(processor, this.getFlow().origin()), this.getFlow());
        state.offer(aiFlow);
        return state;
    }

    /**
     * 将数据委托给 {@link AiProcessFlow}{@code <}{@link O}{@code , }{@link R}{@code >}
     * 的指定节点开始处理，然后自身放弃处理数据。处理后的数据会发送回该节点，作为该节点的处理结果。
     *
     * @param aiFlow 表示子流程的 {@link AiProcessFlow}{@code <}{@link O}{@code , }{@link R}{@code >}。
     * @param nodeId 表示节点名称的 {@link String}。
     * @param <R> 表示委托节点的输出数据类型。
     * @return 表示委托节点的 {@link AiState}{@code <}{@link R}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException
     * <ul>
     *     <li>当 {@code aiFlow} 为 {@code null}时。</il>
     *     <li>当 {@code nodeId} 为 {@code null} 、空字符串或只有空白字符的字符串时。</il>
     * </ul>
     */
    public <R> AiState<R, D, O, RF, F> delegate(AiProcessFlow<O, R> aiFlow, String nodeId) {
        Validation.notNull(aiFlow, "Flow cannot be null.");
        Validation.notBlank(nodeId, "Node id cannot be blank.");
        Processor<O, R> processor = this.publisher().map(input -> {
            aiFlow.converse(input.getSession()).offer(nodeId, Collections.singletonList(input.getData()));
            return (R) null;
        }, null).displayAs("delegate to node", aiFlow.origin(), nodeId);

        AiState<R, D, O, RF, F> state = new AiState<>(new State<>(processor, this.getFlow().origin()), this.getFlow());
        state.offer(aiFlow);
        return state;
    }

    /**
     * 通过提示模板和参数生成 {@link Prompt}，作为大模型输入参数。
     *
     * @param templates 表示提示词模板数组的 {@link PromptTemplate}{@code <}{@link O}{@code >[]}。
     * @return 表示提示词节点的 {@link AiState}{@code <}{@link Prompt}{@code , }{@link D}{@code , }{@link O}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     */
    @SafeVarargs
    public final AiState<Prompt, D, O, RF, F> prompt(PromptTemplate<O>... templates) {
        return new AiState<>(new State<>(this.publisher().map(input -> {
            ChatMessages messages = new ChatMessages();
            List<Prompt> prompts =
                    AiFlowSession.applyBatchPattern(Arrays.asList(templates), input.getData(), input.getSession());
            prompts.forEach(prompt -> messages.addAll(prompt.messages()));
            return (Prompt) messages;
        }, null).displayAs("prompt"), this.getFlow().origin()), this.getFlow());
    }

    /**
     * 生成大模型阻塞调用节点。
     *
     * @param model 表示模型算子实现的 {@link BlockModel}{@code <}{@link M}{@code >}。
     * @param <M> 表示模型节点的输入数据类型。
     * @return 表示大模型阻塞调用节点的 {@link AiState}{@code <}{@link ChatMessage}{@code , }{@link D}{@code ,
     * }{@link O}{@code , }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code model} 为 {@code null} 时。
     */
    public <M extends ChatMessage> AiState<M, D, O, RF, F> generate(BlockModel<O, M> model) {
        Validation.notNull(model, "Model operator cannot be null.");
        return new AiState<>(new State<>(this.publisher()
                .map(input -> AiFlowSession.applyPattern(model, input.getData(), input.getSession()), null)
                .displayAs("generate"), this.getFlow().origin()), this.getFlow());
    }

    /**
     * 生成大模型流式调用节点。
     *
     * @param model 表示流式模型算子实现的 {@link StreamModel}{@code <}{@link O}{@code >}。
     * @param <M> 表示模型节点的输入数据类型。
     * @return 表示大模型流式调用节点的 {@link AiState}{@code <}{@link ChatChunk}{@code , }{@link D}{@code ,
     * }{@link ChatChunk}{@code , }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code model} 为 {@code null} 时。
     */
    public <M extends ChatMessage> AiState<ChatChunk, D, ChatChunk, RF, F> generate(StreamModel<O, M> model) {
        Validation.notNull(model, "Streaming Model operator cannot be null.");

        AtomicReference<Processor<O, ChatChunk>> processorRef = new AtomicReference<>();
        Processor<O, ChatChunk> processor = this.publisher().flatMap(input -> {
            FlowSession session = input.getSession();
            input.setKeyBy(session.getId());
            session.setInnerState(StateKey.STREAMING_PROCESSOR, processorRef.get());
            session.setInnerState(StateKey.STREAMING_FLOW_CONTEXT, input);
            return Flows.source(AiFlowSession.applyPattern(model, input.getData(), input.getSession()));
        }, null).displayAs("generate streaming");
        processorRef.set(processor);
        return new AiState<>(new State<>(processor, this.getFlow().origin()), this.getFlow())
                .window(SessionWindow.from(inputs -> inputs.stream().anyMatch(ChatChunk::isEnd)));
    }

    /**
     * 生成平行分支节点。每个分支将输出一个键值对。
     *
     * @param patterns 表示同步委托单元数组的 {@link Pattern}{@code <}{@link O}{@code , }{@link Tip}{@code >[]}。
     * @return 表示平行分支节点的 {@link AiState}{@code <}{@link Tip}{@code , }{@link D}{@code , }{@link Tip}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code patterns} 数组为空时。
     */
    @SafeVarargs
    public final AiState<Tip, D, Tip, RF, F> runnableParallel(Pattern<O, Tip>... patterns) {
        Validation.isTrue(patterns.length > 0, "Patterns can not be empty.");

        F mineFlow = this.getFlow();
        RF mineOrigin = this.getFlow().origin();
        AiFork<Tip, D, O, RF, F> aiFork = null;
        for (Pattern<O, Tip> pattern : patterns) {
            AiBranchProcessor<Tip, D, O, RF, F> branchProcessor = node -> {
                Processor<O, Tip> processor = this.getPatternProcessor(pattern, node);
                return new AiState<>(new State<>(processor, mineOrigin), mineFlow);
            };
            aiFork = Optional.ofNullable(aiFork)
                    .map(node -> node.fork(branchProcessor))
                    .orElseGet(() -> new AiParallel<>(this.start.parallel(), mineFlow).fork(branchProcessor));
        }

        AiState<Tip, D, Tip, RF, F> state = aiFork.join(Tip::new, (acc, data) -> {
            acc.merge(data);
            return acc;
        });
        ((Processor<?, ?>) state.publisher()).displayAs("runnableParallel");
        return state;
    }

    private Processor<O, Tip> getPatternProcessor(Pattern<O, Tip> pattern, AiState<O, D, O, RF, F> node) {
        return node.publisher()
                .map(input -> AiFlowSession.applyPattern(pattern, input.getData(), input.getSession()), null);
    }
}
