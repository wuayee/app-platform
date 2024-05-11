/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.states;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fit.waterflow.domain.stream.nodes.BlockToken;
import com.huawei.fit.waterflow.domain.stream.nodes.Node;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.stream.reactive.Callback;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscriber;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Node Activity
 * 一般节点：开始和结束中间的，既不是判定，也不是平行节点的后续节点的常规节点
 * 是processor，既是接收数据者，也是发送数据者
 * 除了开始和结束，其他特殊节点都是来自于这Node Activity【没有通过继承方式表示】
 *
 * @param <O> 这个节点的输出数据类型
 * @param <D> 这个节点对应flow的初始数据类型
 * @param <I> 输入数据类型
 * @param <F> 对应的是处理流，还是生产流，用于泛型推演
 * @since 1.0
 */
public class State<O, D, I, F extends Flow<D>> extends Start<O, D, I, F>
        implements EmitterListener<O, FlowSession>, Emitter<O, FlowSession> {
    /**
     * processor
     */
    protected final Processor<I, O> processor;

    public State(Processor<I, O> processor, F flow) {
        super(processor, flow);
        this.processor = processor;
        flow.nodes().add(this.processor);
    }

    @Override
    public Publisher<O> publisher() {
        return this.processor;
    }

    @Override
    public void handle(O data, FlowSession trans) {
        this.processor.handle(data, trans);
    }

    /**
     * 注册一个节点的监听器
     * 用于形成一个流程间的数据交互或者与非流程的数据交互
     *
     * @param handler 监听器
     */
    public void register(EmitterListener<O, FlowSession> handler) {
        this.processor.register(handler);
    }

    @Override
    public void emit(O data, FlowSession token) {
        this.processor.emit(data, token);
    }

    /**
     * 标识一个节点的别名id
     * 设定一个别名id后，通常用于to跳转，或者向一个该节点发射数据
     *
     * @param id 别名id
     * @return 返回节点本身，便于后续的链式调用
     */
    public State<O, D, I, F> id(String id) {
        ObjectUtils.<Node<I, O>>cast(this.processor).setId(id);
        return ObjectUtils.cast(super.setId(id));
    }

    /**
     * 跳转到指定节点，使用节点的别名id来标识一个节点
     * <p>
     * 类似于goto，通常在conditions的分支中使用，用于形成循环
     * </p>
     *
     * @param id 节点的别名id，通常使用 {@link State#id(String)} 指定
     */
    public void to(String id) {
        this.processor.subscribe(ObjectUtils.<State>cast(this.getFlow().getNode(id)).processor, null);
    }

    /**
     * 跳转到指定节点
     * 类似于goto，通常在conditions的分支中使用，用于形成循环
     *
     * @param state 指定的节点
     */
    public void to(State<?, D, O, F> state) {
        this.processor.subscribe(state.processor, null);
    }

    /**
     * 一般节点的block，用于系统或人为介入
     *
     * @param block block实现
     * @return block的节点
     */
    public State<O, D, O, F> block(BlockToken<O> block) {
        AtomicReference<State<O, D, O, F>> state = new AtomicReference<>();
        state.set(new State<>(this.from.map(new Operators.Map<FlowContext<O>, O>() {
            @Override
            public O process(FlowContext<O> input) {
                block.setHost(state.get().from, input);
                return null;
            }
        }, null), this.getFlow()));
        return state.get();
    }

    /**
     * 处理发生错误时的处理方式
     *
     * @param handler 错误处理器
     * @return state节点
     */
    public State<O, D, I, F> error(Operators.ErrorHandler<I> handler) {
        this.processor.onError(handler);
        return this;
    }

    /**
     * close 流程，也就是加终止节点
     *
     * @return 返回对应的流对象
     */
    public F close() {
        return this.close(data -> {});
    }

    /**
     * close 流程，也就是加终止节点
     *
     * @param callback 流程结束的回调处理器
     * @return 返回对应的流对象
     */
    public F close(Operators.Just<Callback<FlowContext<O>>> callback) {
        return this.close(callback, null);
    }

    /**
     * close 流程，也就是加终止节点
     * 所有未结束节点都会同时连接上end节点，这块遇到不同节点数据类型不同时有风险，需要重构
     *
     * @param callback 结束后的callback函数，可以用做事件回调
     * @param errHandler 流程错误处理器
     * @return F 流程实例
     */
    public F close(Operators.Just<Callback<FlowContext<O>>> callback, Operators.ErrorHandler<Object> errHandler) {
        getFlow().setEnd(this.processor.close());
        List<Publisher> nodes = this.getFlow()
                .nodes()
                .stream()
                .map(node -> ObjectUtils.<Publisher>cast(node))
                .collect(Collectors.toList());
        nodes.add(this.getFlow().start());
        nodes.stream().filter(node -> !node.subscribed()).forEach(node -> node.subscribe(getFlow().end()));
        getFlow().end().onComplete(callback);
        this.getFlow().nodes().forEach(node -> node.onGlobalError(errHandler));
        getFlow().end().onGlobalError(errHandler);
        return this.getFlow();
    }

    /**
     * 获取该state内部实际的Subscriber
     *
     * @return 内部的Subscriber
     */
    public Subscriber<I, O> subscriber() {
        return this.processor;
    }
}
