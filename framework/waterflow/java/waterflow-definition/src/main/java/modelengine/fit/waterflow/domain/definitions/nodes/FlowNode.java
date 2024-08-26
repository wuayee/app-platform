/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.nodes;

import static modelengine.fit.waterflow.common.ErrorCodes.FLOW_ENGINE_EXECUTOR_ERROR;
import static modelengine.fit.waterflow.common.ErrorCodes.FLOW_NODE_OPERATOR_NOT_SUPPORT;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.waterflow.common.exceptions.WaterflowException;
import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.domain.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.domain.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.domain.definitions.nodes.filters.FlowFilter;
import modelengine.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.domain.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;
import modelengine.fit.waterflow.domain.enums.FlowNodeTriggerMode;
import modelengine.fit.waterflow.domain.enums.FlowNodeType;
import modelengine.fit.waterflow.domain.stream.operators.Operators;
import modelengine.fit.waterflow.domain.stream.reactive.Processor;
import modelengine.fit.waterflow.domain.stream.reactive.Publisher;
import modelengine.fit.waterflow.domain.stream.reactive.Subscriber;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 流程定义节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowNode {
    /**
     * 流程节点metaId，与前端保持一致
     */
    protected String metaId;

    /**
     * 流程节点名称
     */
    protected String name;

    /**
     * 流程节点类型
     */
    protected FlowNodeType type;

    /**
     * 流程节点触发类型
     */
    protected FlowNodeTriggerMode triggerMode;

    /**
     * 流程节点属性Map，key为属性的键值，value为属性具体的值
     */
    protected Map<String, String> properties;

    /**
     * 流程节点事件列表
     */
    protected List<FlowEvent> events;

    /**
     * 流程节点自动任务
     */
    protected FlowJober jober;

    /**
     * 流程节点自动任务数据过滤器
     */
    protected FlowFilter joberFilter;

    /**
     * 流程节点手动任务
     */
    protected FlowTask task;

    /**
     * 流程节点手动任务数据过滤器
     */
    protected FlowFilter taskFilter;

    /**
     * 流程节点回调函数
     */
    protected FlowCallback callback;

    /**
     * 流程节点对应的{@link Processor}
     */
    protected Processor<FlowData, FlowData> processor;

    /**
     * 判断节点是否属于该类型
     *
     * @param type {@link FlowNodeType} 判断类型
     * @return 节点是否属于该类型
     */
    public boolean belongTo(FlowNodeType type) {
        return this.type.equals(type);
    }

    /**
     * 获取节点内的publisher
     * 只有开始节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link Processor}
     */
    public Publisher<FlowData> getPublisher(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getPublisher");
    }

    /**
     * 获取节点内的processor
     * 只有普通节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link Processor}
     */
    public Processor<FlowData, FlowData> getProcessor(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getProcessor");
    }

    /**
     * 获取节点内的subscriber
     * 只有结束节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link Subscriber}
     */
    public Subscriber<FlowData, FlowData> getSubscriber(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getSubscriber");
    }

    /**
     * from节点subscribe下一个节点
     *
     * @param streamId stream流程Id
     * @param repo {@link FlowContextRepo}stream流程上下文repo
     * @param messenger {@link FlowContextMessenger}stream流程事件发送器
     * @param locks 流程锁
     * @param toNode {@link FlowNode}下一个节点
     * @param event {@link FlowEvent}节点之间的连线
     */
    public void subscribe(String streamId, FlowContextRepo<FlowData> repo, FlowContextMessenger messenger,
            FlowLocks locks, FlowNode toNode, FlowEvent event) {
        this.subscribe(getFrom(streamId, repo, messenger, locks), getTo(streamId, repo, messenger, locks, toNode),
                event);
    }

    /**
     * subscribe
     *
     * @param from from
     * @param to to
     * @param event event
     */
    protected void subscribe(Publisher<FlowData> from, Subscriber<FlowData, FlowData> to, FlowEvent event) {
        from.subscribe(event.getMetaId(), to);
    }

    /**
     * errorHandler
     *
     * @param streamId streamId
     * @return Processors.Error<FlowData>
     */
    protected Operators.ErrorHandler<FlowData> errorHandler(String streamId) {
        return (exception, retry, flowContexts) -> {
            String errorMessage = MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId, this.metaId,
                    this.name, exception.getClass().getSimpleName(),
                    Optional.ofNullable(exception.getMessage()).orElse("internal error"));
            flowContexts.forEach(context -> {
                context.setStatus(FlowNodeStatus.ERROR);
                context.getData().setErrorMessage(errorMessage);
            });
            retry.process(flowContexts);
        };
    }

    /**
     * 当用户给流程节点配置回调函数时，设置回调函数处理机制
     *
     * @param subscriber {@link Subscriber} 表示流程节点内的subscriber
     * @param messenger {@link FlowContextMessenger} 表示stream流程事件发送器
     */
    protected void setCallback(Subscriber<FlowData, FlowData> subscriber, FlowContextMessenger messenger) {
        Optional.ofNullable(this.callback)
                .ifPresent(any -> subscriber.onComplete(callback -> messenger.sendCallback(callback.getAll())));
    }

    private Publisher<FlowData> getFrom(String streamId, FlowContextRepo<FlowData> repo, FlowContextMessenger messenger,
            FlowLocks locks) {
        Publisher<FlowData> publisher;
        if (this.belongTo(FlowNodeType.START)) {
            publisher = this.getPublisher(streamId, repo, messenger, locks);
        } else {
            publisher = this.getProcessor(streamId, repo, messenger, locks);
        }
        return publisher;
    }

    private Subscriber<FlowData, FlowData> getTo(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNode toNode) {
        Subscriber<FlowData, FlowData> subscriber;
        if (toNode.belongTo(FlowNodeType.END)) {
            subscriber = toNode.getSubscriber(streamId, repo, messenger, locks);
        } else {
            subscriber = toNode.getProcessor(streamId, repo, messenger, locks);
        }
        return subscriber;
    }
}
