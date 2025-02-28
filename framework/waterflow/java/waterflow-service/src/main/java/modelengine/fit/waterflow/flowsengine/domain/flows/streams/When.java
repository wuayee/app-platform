/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

import lombok.Getter;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ProcessType;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * fit中的subscription：简单理解为流程图中的连接线
 * 辉子 2019-10-31
 *
 * @param <I> 来源数据类型
 * @param <O> 转换后数据
 * @author 高诗意
 * @since 2023/08/14
 */
public class When<I, O> extends IdGenerator implements FitStream.Subscription<I, O> {
    /**
     * 到接收者前的预处理
     */
    private final Processors.Map<I, O> converter;

    /**
     * 满足condition才能进入cache
     */
    @Getter
    private final Processors.Whether<I> whether;

    /**
     * 要发往的接受者
     */
    @Getter
    private final FitStream.Subscriber<O, ?> to;

    private final FlowContextRepo repo;

    private final FlowContextMessenger messenger;

    private final String streamId;

    /**
     * When
     *
     * @param streamId streamId
     * @param to to
     * @param converter converter
     * @param whether whether
     * @param repo contextRepo
     * @param messenger messenger
     */
    public <R> When(String streamId, FitStream.Subscriber<O, R> to, Processors.Map<I, O> converter, Processors.Whether<I> whether, FlowContextRepo repo,
            FlowContextMessenger messenger) {
        this.streamId = streamId;
        this.converter = converter == null ? input -> (O) input : converter;
        this.whether = whether == null ? i -> true : whether;
        this.to = to;
        this.to.onSubscribe(this);
        this.repo = repo;
        this.messenger = messenger;
    }

    /**
     * When
     *
     * @param streamId streamId
     * @param eventId eventId
     * @param to to
     * @param converter converter
     * @param whether whether
     * @param repo contextRepo
     * @param messenger messenger
     */
    public <R> When(String streamId, String eventId, FitStream.Subscriber<O, R> to, Processors.Map<I, O> converter, Processors.Whether<I> whether,
            FlowContextRepo repo, FlowContextMessenger messenger) {
        this(streamId, to, converter, whether, repo, messenger);
        this.id = eventId;
    }

    @Override
    public void cache(List<FlowContext<I>> contexts) {
        if (CollectionUtils.isEmpty(contexts)) {
            return;
        }
        // 将context发送到节点边上，更新为PENDING状态，等待下一个节点处理
        // 该过程不产生新的context数据，只更新context的状态
        List<FlowContext<O>> converted = contexts.stream()
                .map(c -> c.convertData(this.converter.process(c.getData()), c.getId())
                        .setPosition(this.getId())
                        .setStatus(FlowNodeStatus.PENDING))
                .collect(Collectors.toList());
        repo.updateStatus(converted, converted.get(0).getStatus().toString(), converted.get(0).getPosition());
        messenger.send(this.to.isAuto() ? ProcessType.PROCESS : ProcessType.PRE_PROCESS, this.to, converted);
    }

    @Override
    public String getStreamId() {
        return this.streamId;
    }
}
