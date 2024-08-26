/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows;

import lombok.Getter;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ParallelMode;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Identity;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Flow由若干个Activities组成，每种activity对应一种stream元素
 * 通过泛型推演下一个可用函数
 * 辉子 2019-10-31
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public final class Activities {
    /**
     * state別名，用於flow之間聯絡，兩個作用
     * 1： 供其他flow.to
     * 2：供自身.to其他流程
     *
     * @param <I> state的进数据类型: 用于被其他flow.to
     * @param <O> state的出数据类型: 用于.to其他flow
     */
    public interface StateAlias<I, O> {
        /**
         * getState
         *
         * @return State<O, ?, I, ?>
         */
        State<O, ?, I, ?> getState();

        /**
         * to
         *
         * @param next next
         * @param convert convert
         */
        <R> void to(StateAlias<R, ?> next, Processors.Map<O, R> convert);
    }

    /**
     * Fork Activity
     * 平行并发节点后面的分支
     *
     * @param <O> 这个节点的输出数据类型
     * @param <D> 这个节点对应flow的初始数据类型
     * @param <F> 对应的是处理流，还是生产流，用于泛型推演
     */
    public static class Fork<O, D, F extends Flows.Flow<D>> extends Activity<D, F> {
        private final State<O, D, O, F> node;

        private final Parallel<O, D, F> host;

        /**
         * Fork
         *
         * @param node node
         * @param host host
         */
        public Fork(State<O, D, O, F> node, Parallel<O, D, F> host) {
            super(node.getFlow());
            this.node = node;
            this.host = host;
            this.host.forks.add(this);
        }

        /**
         * fork后的节点只允许just，不允许任何类型转换处理
         * 因为fork后的任何数据转换都会在join的时候造成问题
         * parallel(A).fork出B，B.just出C，也就是A-B-C，C是B的下一个节点
         *
         * @param processor just处理器
         * @return just出另外一个fork节点
         */
        public Fork<O, D, F> just(Processors.Just<O> processor) {
            this.host.forks.remove(this);
            return new Fork<>(this.node.just(processor), host);
        }

        /**
         * fork节点的fork就是父平行节点的fork，比如
         * parallel(A).fork出B，B.fork出C，其实调用的是A.fork，结果是A fork出了B,C。B,C是平级节点
         *
         * @param processor fork节点处理器
         * @return 另外一个fork节点
         */
        public Fork<O, D, F> fork(Processors.Just<O> processor) {
            return this.host.fork(processor);
        }

        /**
         * 生成join节点，到这里parallel结束，回到一般节点
         * 因为没有processor，就是把处理过的数据返回
         *
         * @return 回到一般节点
         */
        public State<O, D, O, F> join() {
            return this.join(i -> i.get(0));
        }

        /**
         * 生成join节点，到这里parallel结束，回到一般节点
         *
         * @param processor join后的数据再处理一下
         * @param <R> 处理后的数据类型
         * @return 回到一般节点
         */
        public <R> State<R, D, O, F> join(Processors.Reduce<O, R> processor) {
            Processors.Reduce<FlowContext<O>, R> wrapper = input -> processor.process(
                    input.stream().map(i -> i.getData()).collect(Collectors.toList()));
            FitStream.Processor<O, R> pro = this.node.processor.join(wrapper, null, null);
            this.host.forks.forEach(f -> {
                if (f == this) {
                    return;
                }
                f.node.processor.subscribe(pro);
            });
            return new State<>(pro, this.node.getFlow());
        }

        /**
         * 阻塞，等待外部调用驱动该fork节点
         *
         * @param block 阻塞实现
         * @return 要阻塞的fork节点
         */
        public Fork<O, D, F> block(Blocks.ValidatorBlock<O> block) {
            this.node.block(block);
            return this;
        }
    }

    /**
     * Condition Activity
     * 条件节点，从这个节点后面，match函数才能推演出来
     *
     * @param <O> 这个节点的输出数据类型
     * @param <D> 这个节点对应flow的初始数据类型
     * @param <F> 对应的是处理流，还是生产流，用于泛型推演
     */
    public static class Conditions<O, D, F extends Flows.Flow<D>> extends Activity<D, F> {
        /**
         * node
         */
        protected final State<O, D, O, F> node;

        /**
         * Conditions
         *
         * @param node node
         */
        public Conditions(State<O, D, O, F> node) {
            super(node.getFlow());
            this.node = node;
        }

        /**
         * 条件节点后面只能跟match：条件分支
         *
         * @param whether 条件判定函数
         * @return 得到条件关系，这里不是真实的节点，节点在MatchHappen后面在出来
         */
        public MatchHappen<O, D, F> match(Processors.Whether<O> whether) {
            return new MatchHappen<>(whether, this);
        }
    }

    /**
     * Match Activity
     * 条件节点后面的选择分支，也就是流程图里菱形后面的节点
     *
     * @param <O>这个节点的输出数据类型
     * @param <D>这个节点对应flow的初始数据类型
     * @param <I>这个节点的输入数据类型
     * @param <F>对应的是处理流，还是生产流，用于泛型推演
     */
    public static class Match<O, D, I, F extends Flows.Flow<D>> extends State<O, D, I, F> {
        private final Conditions<I, D, F> host;

        Match(Conditions<I, D, F> host, FitStream.Processor<I, O> processor, F flow) {
            super(processor, flow);
            this.host = host;
        }

        /**
         * 参考条件节点的match
         *
         * @param whether 条件判定函数
         * @return 得到条件关系
         */
        public MatchHappen<I, D, F> match(Processors.Whether<I> whether) {
            return new MatchHappen<>(whether, host);
        }

        /**
         * if else里的else
         *
         * @param processor map处理器
         * @param <R> 处理后的数据类型
         * @return else节点，这里是节点，不是条件
         */
        public <R> State<R, D, I, F> others(Processors.Map<I, R> processor) {
            Processors.Map<FlowContext<I>, R> wrapper = input -> processor.process(input.getData());
            return new State<>(this.host.node.processor.map(wrapper, null, null), this.getFlow());
        }
    }

    /**
     * Parallel Activity 平行节点，分all，either两种模式
     * 这个节点后面之哟啊没有遇到join节点，分支都属于平行分支
     * all模式下，所有分支走完餐能触发join节点
     * either模式下，只要有一个分支到达join就会触发join节点，并作废其他任务
     *
     * @param <O> 这个节点的输出数据类型
     * @param <D> 这个节点对应flow的初始数据类型
     * @param <F> 对应的是处理流，还是生产流，用于泛型推演
     */
    public static class Parallel<O, D, F extends Flows.Flow<D>> extends Activity<D, F> {
        private final State<O, D, O, F> node;

        private final List<Fork<O, D, F>> forks = new ArrayList<>();

        /**
         * Parallel
         *
         * @param node node
         */
        public Parallel(State<O, D, O, F> node) {
            super(node.getFlow());
            this.node = node;
        }

        /**
         * parallel节点就是用来fork出其他子节点的
         *
         * @param processor just处理器
         * @return 新的fork节点
         */
        public Fork<O, D, F> fork(Processors.Just<O> processor) {
            return new Fork<>(this.node.just(processor), this);
        }
    }

    /**
     * Node Activity
     * 一般节点：开始和结束中间的，既不是判定，也不是平行节点的后续节点的常规节点
     * 是processor，既是接收数据者，也是发送数据者
     * 除了开始和结束，其他特殊节点都是来自于这Node Activity【没有通过继承方式表示】
     *
     * @param <O> 这个节点的输出数据类型
     * @param <D> 这个节点对应flow的初始数据类型
     * @param <F> 对应的是处理流，还是生产流，用于泛型推演
     * @param <I> 输入数据类型
     */
    public static class State<O, D, I, F extends Flows.Flow<D>> extends Start<O, D, F>
            implements InterStreamHandler<O>, InterStream<O> {
        /**
         * processor
         */
        protected final FitStream.Processor<I, O> processor;

        public State(FitStream.Processor<I, O> processor, F flow) {
            super(processor, flow);
            this.processor = processor;
            flow.nodes.add(this.processor);
        }

        @Override
        public FitStream.Publisher<O> publisher() {
            return this.processor;
        }

        @Override
        public void handle(O data, String token) {
            this.processor.handle(data, token);
        }

        @Override
        public void handle(O[] data, String token) {
            this.processor.handle(data, token);
        }

        @Override
        public void register(InterStreamHandler<O> handler) {
            this.processor.register(handler);
        }

        @Override
        public void publish(O data, String token) {
            this.processor.publish(data, token);
        }

        @Override
        public void publish(O[] data, String token) {
            this.processor.publish(data, token);
        }

        /**
         * id
         *
         * @param id id
         * @return State
         */
        public State<O, D, I, F> id(String id) {
            return (State<O, D, I, F>) super.id(id);
        }

        /**
         * 一般节点的block，用于系统或人为介入
         *
         * @param block block实现
         * @return block的节点
         */
        public State<O, D, I, F> block(Blocks.Block<I> block) {
            this.processor.block(block);
            return this;
        }

        /**
         * wrap后供cross flow.to引用
         *
         * @param wrapper wrapper
         * @return State
         */
        public State<O, D, I, F> alias(Processors.Just<StateAlias<I, O>> wrapper) {
            State<O, D, I, F> self = this;
            wrapper.process(new StateAlias<I, O>() {
                @Override
                public State<O, ?, I, ?> getState() {
                    return self;
                }

                @Override
                public <R> void to(StateAlias<R, ?> next, Processors.Map<O, R> convert) {
                    self.processor.subscribe(next.getState().processor, convert, i -> true);
                }
            });
            return this;
        }

        /**
         * 处理发生错误时的处理方式
         *
         * @param handler 错误处理器
         * @return state节点
         */
        public State<O, D, I, F> error(Processors.Error<I> handler) {
            this.processor.onError(handler);
            return this;
        }

        /**
         * close 流程，也就是加终止节点
         *
         * @return 返回对应的流对象
         */
        public F close() {
            return this.close(i -> {
            });
        }

        /**
         * close 流程，也就是加终止节点
         *
         * @param callback 流程结束的回调处理器
         * @return 返回对应的流对象
         */
        public F close(Processors.Just<FitStream.Callback<FlowContext<O>>> callback) {
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
        public F close(Processors.Just<FitStream.Callback<FlowContext<O>>> callback,
                Processors.Error<Object> errHandler) {
            getFlow().end = this.processor.close();
            List<FitStream.Publisher> nodes = this.getFlow().nodes.stream()
                    .map(ObjectUtils::<FitStream.Publisher>cast)
                    .collect(Collectors.toList());
            nodes.add(this.getFlow().start);
            nodes.stream().filter(n -> !n.subscribed()).forEach(n -> n.subscribe(getFlow().end));
            getFlow().end.onComplete(callback);
            this.getFlow().nodes.forEach(n -> n.onGlobalError(errHandler));
            return this.getFlow();
        }

        public FitStream.Subscriber<I, O> subscriber() {
            return this.processor;
        }
    }

    /**
     * 开始节点，包装了from
     *
     * @param <O> 这个节点的输出数据类型
     * @param <D> 这个节点对应flow的初始数据类型
     */
    public static class Start<O, D, F extends Flows.Flow<D>> extends Activity<D, F> {
        /**
         * from
         */
        protected final FitStream.Publisher<O> from;

        public Start(FitStream.Publisher<O> from, F flow) {
            super(flow);
            this.from = from;
        }

        /**
         * publisher
         *
         * @return FitStream.Publisher
         */
        public FitStream.Publisher<O> publisher() {
            return this.from;
        }

        /**
         * id
         *
         * @param id id
         * @return Start
         */
        public Start<O, D, F> id(String id) {
            this.getFlow().setId(id);
            return (Start<O, D, F>) super.id(id);
        }

        /**
         * getSubscriptionsId
         *
         * @return List<String>
         */
        public List<String> getSubscriptionsId() {
            return this.from.getSubscriptions().stream().map(Identity::getId).collect(Collectors.toList());
        }

        /**
         * 开启条件节点
         *
         * @param processor 进入条件节点的just处理函数
         * @return 条件节点
         */
        public Conditions<O, D, F> conditions(Processors.Just<O> processor) {
            Processors.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData());
            return new Conditions<>(new State<>(this.from.conditions(wrapper, null, null), this.getFlow()));
        }

        /**
         * 开启条件节点
         *
         * @return 条件界定啊
         */
        public Conditions<O, D, F> conditions() {
            return this.conditions(i -> {
            });
        }

        /**
         * 开启平行节点
         *
         * @param processor 进入平行节点处理函数
         * @param mode 平行节点模式：Either还是All
         * @return 平行节点
         */
        public Parallel<O, D, F> parallel(Processors.Just<O> processor, ParallelMode mode) {
            Processors.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData());
            return new Parallel<>(new State<>(this.from.parallel(wrapper, mode, null, null), this.getFlow()));
        }

        /**
         * 开启平行节点,默认all模式
         *
         * @param processor 进入平行节点处理函数
         * @return 平行节点
         */
        public Parallel<O, D, F> parallel(Processors.Just<O> processor) {
            return this.parallel(processor, ParallelMode.ALL);
        }

        /**
         * 开启平行节点,默认all模式,啥也不预处理
         *
         * @return 平行节点
         */
        public Parallel<O, D, F> parallel() {
            return this.parallel(o -> {
            }, ParallelMode.ALL);
        }

        /**
         * 开启平行节点,啥也不预处理
         *
         * @param mode 平行节点模式：Either还是All
         * @return 平行节点
         */
        public Parallel<O, D, F> parallel(ParallelMode mode) {
            return this.parallel(o -> {
            }, mode);
        }

        /**
         * just，只处理，不转换
         *
         * @param processor just转换器
         * @return 新的处理节点
         */
        public State<O, D, O, F> just(Processors.Just<O> processor) {
            Processors.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData());
            return new State<>(this.from.just(wrapper, null, null), this.getFlow());
        }

        /**
         * map,处理，并转换类型
         *
         * @param processor map处理器
         * @param <R> 处理完类型
         * @return 新的处理节点
         */
        public <R> State<R, D, O, F> map(Processors.Map<O, R> processor) {
            Processors.Map<FlowContext<O>, R> wrapper = input -> processor.process(input.getData());
            return new State<>(this.from.map(wrapper, null, null), this.getFlow());
        }

        /**
         * flat map,处理，1变多，并转换类型
         *
         * @param processor flat map处理器
         * @param <R> 处理完类型
         * @return 新的处理节点
         */
        public <R> State<R, D, O, F> flatMap(Processors.FlatMap<O, R> processor) {
            Processors.FlatMap<FlowContext<O>, R> wrapper = input -> processor.process(input.getData());
            return new State<>(this.from.flatMap(wrapper, null, null), this.getFlow());
        }

        /**
         * reduce处理节点：m->1
         *
         * @param processor reduce处理器
         * @param <R> 处理完类型
         * @return 新的处理节点
         */
        public <R> State<R, D, O, F> reduce(Processors.Reduce<O, R> processor) {
            Processors.Reduce<FlowContext<O>, R> wrapper = input -> processor.process(
                    input.stream().map(i -> i.getData()).collect(Collectors.toList()));
            return new State<>(this.from.reduce(wrapper, null, null), this.getFlow());
        }

        /**
         * produce处理节点：m->n
         *
         * @param processor produce处理器
         * @param <R> 处理完类型
         * @return 新的处理节点
         */
        public <R> State<R, D, O, F> produce(Processors.Produce<O, R> processor) {
            Processors.Produce<FlowContext<O>, R> wrapper = input -> processor.process(
                    input.stream().map(i -> i.getData()).collect(Collectors.toList()));
            return new State<>(this.from.produce(wrapper, null, null), this.getFlow());
        }

        /**
         * when就是两个节点之间的连线
         * 只有预处理的when
         *
         * @param convert 转换函数
         * @return 新的连接器
         */
        public <R> WhenHappen<R, O, D, F> when(Processors.Map<O, R> convert) {
            return this.when(convert, null);
        }

        /**
         * 既有预处理，又有过滤条件的when
         *
         * @param convert 转换函数
         * @param whether 判断条件
         * @return 新的连接器
         */
        public <R> WhenHappen<R, O, D, F> when(Processors.Map<O, R> convert, Processors.Whether<O> whether) {
            return new WhenHappen<>(convert, whether, this);
        }
    }

    /**
     * 所有activity基类
     *
     * @param <D> 这个节点对应flow的初始数据类型
     * @param <F> 对应的是处理流，还是生产流，用于泛型推演
     */
    public abstract static class Activity<D, F extends Flows.Flow<D>> {
        @Getter
        private final F flow;

        protected Activity(F flow) {
            this.flow = flow;
        }

        /**
         * id
         *
         * @param id id
         * @return Activity
         */
        protected Activity<D, F> id(String id) {
            this.flow.tagNode(id, this);
            return this;
        }
    }

    /**
     * 别名包裹器：lambda里无法对外部变量赋值而产生的方案
     */
    public static class AtomicStateAlias<I, O> {
        private StateAlias<I, O> alias;

        public void set(StateAlias<I, O> alias) {
            this.alias = alias;
        }

        public StateAlias<I, O> get() {
            return this.alias;
        }
    }

    /**
     * 代表了一般节点when
     * 用于缓存和数据预处理
     * 连接线后面只有map方法，以免增加复杂度
     *
     * @param <I> 来源节点数据类型
     * @param <D> 这个节点对应flow的初始数据类型
     * @param <F> 对应的是处理流，还是生产流，用于泛型推演
     * @param <O> when转换后类型
     */
    public static class WhenHappen<O, I, D, F extends Flows.Flow<D>> {
        private final Start<I, D, F> node;

        private final Processors.Map<I, O> convert;

        private final Processors.Whether<I> whether;

        private WhenHappen(Processors.Map<I, O> convert, Processors.Whether<I> whether, Start<I, D, F> node) {
            this.node = node;
            this.convert = convert;
            this.whether = whether;
        }

        /**
         * map,处理，并转换类型
         *
         * @param processor map处理器
         * @param <R> 处理完类型
         * @return 新的处理节点
         */
        public <R> State<R, D, O, F> map(Processors.Map<O, R> processor) {
            Processors.Map<FlowContext<O>, R> wrapper = input -> processor.process(input.getData());
            return new State<>(node.from.map(wrapper, this.convert, this.whether), node.getFlow());
        }

        /**
         * reduce
         *
         * @param processor processor
         * @return State<R, D, O, F>
         */
        public <R> State<R, D, O, F> reduce(Processors.Reduce<O, R> processor) {
            Processors.Reduce<FlowContext<O>, R> wrapper = input -> processor.process(
                    input.stream().map(i -> i.getData()).collect(Collectors.toList()));
            return new State<>(node.from.reduce(wrapper, this.convert, this.whether), node.getFlow());
        }

        /**
         * produce
         *
         * @param processor processor
         * @return State<R, D, O, F>
         */
        public <R> State<R, D, O, F> produce(Processors.Produce<O, R> processor) {
            Processors.Produce<FlowContext<O>, R> wrapper = input -> processor.process(
                    input.stream().map(i -> i.getData()).collect(Collectors.toList()));
            return new State<>(node.from.produce(wrapper, this.convert, this.whether), node.getFlow());
        }

        /**
         * just
         *
         * @param processor processor
         * @return State<O, D, O, F>
         */
        public State<O, D, O, F> just(Processors.Just<O> processor) {
            Processors.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData());
            return new State<>(node.from.just(wrapper, this.convert, this.whether), node.getFlow());
        }

        /**
         * to
         *
         * @param crossNode crossNode
         * @return Start<I, D, F>
         */
        public Start<I, D, F> to(StateAlias<O, ?> crossNode) {
            node.from.subscribe(crossNode.getState().processor, this.convert, this.whether);
            return node;
        }
    }

    /**
     * 代表了条件分支的when
     * 有判定条件，满足条件的才会缓存
     * 判定条件下只有just，map方法，以免增加复杂度
     *
     * @param <O> 这个节点的输出数据类型
     * @param <D> 这个节点对应flow的初始数据类型
     * @param <F> 对应的是处理流，还是生产流，用于泛型推演
     */
    public static class MatchHappen<O, D, F extends Flows.Flow<D>> {
        private final Conditions<O, D, F> node;

        private final Processors.Whether<O> whether;

        private MatchHappen(Processors.Whether<O> whether, Conditions<O, D, F> node) {
            this.node = node;
            this.whether = whether;
        }

        /**
         * map,处理，并转换类型
         *
         * @param processor map处理器
         * @param <R> 处理完类型
         * @return 新的处理节点
         */
        public <R> Match<R, D, O, F> map(Processors.Map<O, R> processor) {
            Processors.Map<FlowContext<O>, R> wrapper = input -> processor.process(input.getData());
            return new Match<>(node, node.node.from.map(wrapper, null, whether), node.getFlow());
        }

        /**
         * just，只处理，不转换
         *
         * @param processor just转换器
         * @return 新的处理节点
         */
        public Match<O, D, O, F> just(Processors.Just<O> processor) {
            Processors.Just<FlowContext<O>> wrapper = input -> processor.process(input.getData());
            return new Match<>(node, node.node.from.just(wrapper, null, whether), node.getFlow());
        }
    }
}
