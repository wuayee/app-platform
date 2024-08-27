/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.flow;

import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.domain.emitters.Emitter;
import modelengine.fit.waterflow.domain.emitters.EmitterListener;
import modelengine.fit.waterflow.domain.stream.nodes.From;

/**
 * 处理数据Flow
 * 用于先定义流程，再不停传入不同数据驱动stream往下走
 *
 * @param <D> 初始传入数据类型
 * @since 1.0
 */
public class ProcessFlow<D> extends Flow<D> implements EmitterListener<D, FlowSession>, Emitter<Object, FlowSession> {
    /**
     * 流从起始节点开始
     *
     * @param repo 上下文持久化
     * @param messenger 上下文发送器
     * @param locks 流程锁
     */
    public ProcessFlow(FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        this.start = new From<>(repo, messenger, locks);
    }

    @Override
    public void handle(D data, FlowSession token) {
        this.offer(data, token);
    }

    @Override
    public void register(EmitterListener<Object, FlowSession> handler) {
        this.end.register(handler);
    }

    @Override
    public void emit(Object data, FlowSession token) {
        this.end.emit(data, token);
    }
}
