/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.states;

import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.context.WindowToken;
import modelengine.fit.waterflow.domain.enums.ParallelMode;
import modelengine.fit.waterflow.domain.flow.Flow;
import modelengine.fit.waterflow.domain.stream.operators.Operators;
import modelengine.fit.waterflow.domain.stream.reactive.Processor;
import modelengine.fit.waterflow.domain.stream.reactive.Publisher;
import modelengine.fit.waterflow.domain.utils.Tuple;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Fork Activity
 * 平行并发节点后面的分支
 *
 * @param <O> 这个节点的输出数据类型
 * @param <D> 这个节点对应flow的初始数据类型
 * @param <F> 对应的是处理流，还是生产流，用于泛型推演
 * @since 1.0
 */
public class Fork<O, D, I, F extends Flow<D>> extends Activity<D, F> {
    private final State<I, D, I, F> node;

    private final List<State<O, D, ?, F>> forks = new ArrayList<>();

    /**
     * Fork
     *
     * @param processor fork该分支的处理器
     * @param node node
     */
    public Fork(Operators.BranchProcessor<O, D, I, F> processor, State<I, D, I, F> node) {
        super(node.getFlow());
        this.node = node;
        this.fork(processor);
    }

    /**
     * fork节点的fork就是父平行节点的fork，比如
     * parallel(A).fork出B，B.fork出C，其实调用的是A.fork，结果是A fork出了B,C。B,C是平级节点
     *
     * @param processor fork节点处理器
     * @return 另外一个fork节点
     */
    public Fork<O, D, I, F> fork(Operators.BranchProcessor<O, D, I, F> processor) {
        this.forks.add(processor.process(this.node));
        return this;
    }

    /**
     * 生成join节点，到这里parallel结束，回到一般节点
     *
     * @param init 初始值
     * @param processor join后的数据再处理一下
     * @return 回到一般节点
     */
    public <R> State<R, D, O, F> join(Supplier<R> init, Operators.Reduce<O, R> processor) {
        Fork<O, D, I, F> me = this;
        AtomicReference<Publisher<O>> processWrapper = new AtomicReference<>();
        Supplier<R> actualInit = ObjectUtils.nullIf(init, () -> null);
        Operators.Map<FlowContext<O>, R> wrapper = new Operators.Map<FlowContext<O>, R>() {
            @Override
            public synchronized R process(FlowContext<O> input) {
                input.setAsAccumulator();
                WindowToken windowToken = input.getWindowToken();
                windowToken.removeToDo(input.getData());
                if (windowToken.fulfilled()) {
                    return null;
                }
                Tuple<FlowSession, R> acc = ObjectUtils.cast(windowToken.accs().get(input.keyBy()));
                if (acc == null) {
                    acc = Tuple.from(input.getSession(), actualInit.get());
                }
                acc = Tuple.from(input.getSession(), processor.process(acc.second(), input.getData()));
                windowToken.accs().put(input.keyBy(), acc);
                windowToken.addOrigin(input.getParallelMode() == ParallelMode.ALL.name() ? me.forks.size() : 1);
                windowToken.setProcessor(processWrapper.get());
                return null;
            }
        };
        Processor<O, R> pro = this.forks.get(0).processor.join(wrapper, null);
        processWrapper.set(ObjectUtils.cast(pro));
        this.forks.stream().skip(1).forEach(fork -> fork.processor.subscribe(pro));
        return new State<>(pro, this.node.getFlow());
    }
}
