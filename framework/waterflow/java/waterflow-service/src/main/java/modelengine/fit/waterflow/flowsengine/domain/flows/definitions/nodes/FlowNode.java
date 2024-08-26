/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import static com.huawei.fit.jober.FlowExceptionService.HANDLE_EXCEPTION_GENERICABLE;
import static com.huawei.fit.jober.common.ErrorCodes.CONTEXT_TYPE_NOT_SUPPORT;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_ENGINE_EXECUTOR_ERROR;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_NODE_OPERATOR_NOT_SUPPORT;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_OHSCRIPT_INVOKE_FITABLE_ERROR;
import static modelengine.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.FlowExceptionService;
import com.huawei.fit.jober.common.OhscriptExecuteException;
import com.huawei.fit.jober.common.TypeNotSupportException;
import com.huawei.fit.jober.common.exceptions.JobberException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.common.utils.UUIDUtil;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.ContextErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeTriggerMode;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Retryable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程定义节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
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
    protected Map<String, Object> properties;

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
     * 流程节点对应的{@link FitStream.Processor}
     */
    protected FitStream.Processor<FlowData, FlowData> processor;

    /**
     * 所属的flow definition， 后续需要提取WaterFlow结构替换，node有归属的WaterFlow
     */
    protected FlowDefinition parentFlow;

    /**
     * 调用 fitable客户端
     */
    protected BrokerClient brokerClient;

    /**
     * 节点任务异常处理fitables集合
     */
    protected Set<String> exceptionFitables;

    public void setJober(FlowJober jober) {
        if (!Objects.isNull(jober)) {
            jober.setParentNode(this);
        }
        this.jober = jober;
    }

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
     * @return {@link FitStream.Processor}
     */
    public FitStream.Publisher<FlowData> getPublisher(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new JobberException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getPublisher");
    }

    /**
     * 获取节点内的processor
     * 只有普通节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link FitStream.Processor}
     */
    public FitStream.Processor<FlowData, FlowData> getProcessor(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new JobberException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getProcessor");
    }

    /**
     * 获取节点内的subscriber
     * 只有结束节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link FitStream.Subscriber}
     */
    public FitStream.Subscriber<FlowData, FlowData> getSubscriber(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        throw new JobberException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "getSubscriber");
    }

    /**
     * from节点subscribe下一个节点
     *
     * @param streamId stream流程Id
     * @param flowEnv flowEnv
     * @param toNode {@link FlowNode}下一个节点
     * @param event {@link FlowEvent}节点之间的连线
     */
    public void subscribe(String streamId, FlowEnv flowEnv, FlowNode toNode, FlowEvent event) {
        this.subscribe(getFrom(streamId, flowEnv.getRepo(), flowEnv.getMessenger(), flowEnv.getLocks()),
                getTo(streamId, flowEnv.getRepo(), flowEnv.getMessenger(), flowEnv.getLocks(), toNode), event);
    }

    /**
     * 获取节点事件
     *
     * @param metaId 事件metaId
     * @return {@link FlowEvent}节点事件
     */
    public Optional<FlowEvent> getEventByMetaId(String metaId) {
        return this.events.stream().filter(e -> e.getMetaId().equals(metaId)).findFirst();
    }

    /**
     * subscribe
     *
     * @param from from
     * @param to to
     * @param event event
     */
    protected void subscribe(FitStream.Publisher<FlowData> from, FitStream.Subscriber<FlowData, FlowData> to,
            FlowEvent event) {
        from.subscribe(event.getMetaId(), to);
    }

    /**
     * errorHandler
     *
     * @param streamId streamId
     * @return Processors.Error<FlowData>
     */
    protected Processors.Error<FlowData> errorHandler(String streamId) {
        return (exception, retry, flowContexts) -> {
            if (Retryable.isRetryableException(exception)) {
                String toBatch = flowContexts.stream()
                        .filter(c -> StringUtils.isNotEmpty(c.getToBatch()))
                        .findAny()
                        .map(FlowContext::getToBatch)
                        .orElseGet(UUIDUtil::uuid);
                flowContexts.forEach(context -> context.setStatus(FlowNodeStatus.RETRYABLE).toBatch(toBatch));
            } else {
                notifyException(exception, flowContexts);
                String errorMessage = MessageFormat.format(FLOW_ENGINE_EXECUTOR_ERROR.getMessage(), streamId,
                        this.metaId, this.name, exception.getClass().getSimpleName(),
                        Optional.ofNullable(exception.getMessage()).orElse("internal error"));
                ContextErrorInfo errorInfo;
                if (exception instanceof OhscriptExecuteException) {
                    errorInfo = ContextErrorInfo.builder()
                            .originMessage(exception.getMessage())
                            .errorCode(FLOW_OHSCRIPT_INVOKE_FITABLE_ERROR.getErrorCode())
                            .errorMessage(FLOW_OHSCRIPT_INVOKE_FITABLE_ERROR.getMessage())
                            .fitableId(((OhscriptExecuteException) exception).getFitableId())
                            .build();
                } else if (exception instanceof TypeNotSupportException) {
                    errorInfo = ContextErrorInfo.builder()
                            .originMessage(exception.getMessage())
                            .errorCode(CONTEXT_TYPE_NOT_SUPPORT.getErrorCode())
                            .errorMessage(CONTEXT_TYPE_NOT_SUPPORT.getMessage())
                            .build();
                } else {
                    errorInfo = ContextErrorInfo.builder()
                            .originMessage(exception.getMessage())
                            .errorCode(FLOW_ENGINE_EXECUTOR_ERROR.getErrorCode())
                            .errorMessage(FLOW_ENGINE_EXECUTOR_ERROR.getMessage())
                            .build();
                }
                errorInfo.setNodeName(this.getName());
                ContextErrorInfo finalErrorInfo = errorInfo;
                flowContexts.forEach(context -> {
                    context.setStatus(FlowNodeStatus.ERROR);
                    context.getData().setErrorMessage(errorMessage);
                    context.getData().setErrorInfo(finalErrorInfo);
                });
            }
            retry.process(exception, flowContexts);
        };
    }

    /**
     * 提醒异常处理.
     *
     * @param ex 异常对象.
     * @param inputs 上下文对象列表.
     */
    public void notifyException(Throwable ex, List<FlowContext<FlowData>> inputs) {
        for (String fitableId : exceptionFitables) {
            this.brokerClient.getRouter(FlowExceptionService.class, HANDLE_EXCEPTION_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(this.metaId, filterFlowData(inputs), ex.getMessage());
        }
    }

    private List<Map<String, Object>> filterFlowData(List<FlowContext<FlowData>> inputs) {
        return inputs.stream().map(cxt -> new HashMap<String, Object>() {
            {
                put(Constant.TRACE_ID_KEY, cxt.getTraceId());
                put(Constant.BUSINESS_DATA_KEY, cxt.getData().getBusinessData());
                put(Constant.CONTEXT_DATA, cxt.getData().getContextData());
                put(Constant.PASS_DATA, cxt.getData().getPassData());
                put("status", cxt.getStatus().name());
                put("createAt", cxt.getCreateAt());
                put("updateAt", cxt.getUpdateAt());
                put("archivedAt", cxt.getArchivedAt());
            }
        }).collect(Collectors.toList());
    }

    /**
     * 当用户给流程节点配置回调函数时，设置回调函数处理机制
     *
     * @param subscriber {@link FitStream.Subscriber} 表示流程节点内的subscriber
     * @param messenger {@link FlowContextMessenger} 表示stream流程事件发送器
     */
    protected void setCallback(FitStream.Subscriber<FlowData, FlowData> subscriber, FlowContextMessenger messenger) {
        subscriber.onComplete(c -> {
            Optional.ofNullable(this.callback).ifPresent(callback -> messenger.sendCallback(callback, c.getAll()));

            Optional.ofNullable(parentFlow.getCallback())
                    .ifPresent(callback -> messenger.sendCallback(callback, c.getAll()));
        });
    }

    /**
     * 查询所属流程的配置信息
     *
     * @param key 属性key
     * @return 属性对象
     */
    protected <T> T getFlowProperty(String key) {
        return cast(this.parentFlow.getProperties().get(key));
    }

    private FitStream.Publisher<FlowData> getFrom(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        FitStream.Publisher<FlowData> publisher;
        if (this.belongTo(FlowNodeType.START)) {
            publisher = this.getPublisher(streamId, repo, messenger, locks);
        } else {
            publisher = this.getProcessor(streamId, repo, messenger, locks);
        }
        return publisher;
    }

    private FitStream.Subscriber<FlowData, FlowData> getTo(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNode toNode) {
        FitStream.Subscriber<FlowData, FlowData> subscriber;
        if (toNode.belongTo(FlowNodeType.END)) {
            subscriber = toNode.getSubscriber(streamId, repo, messenger, locks);
        } else {
            subscriber = toNode.getProcessor(streamId, repo, messenger, locks);
        }
        return subscriber;
    }

    /**
     * flow中的一些全局公共对象
     *
     * @author 宋永坦
     * @since 2024/3/16
     */
    @Getter
    public static class FlowEnv {
        private final FlowContextRepo<FlowData> repo;

        private final FlowContextMessenger messenger;

        private final FlowLocks locks;

        /**
         * FlowEnv
         *
         * @param repo {@link FlowContextRepo}stream流程上下文repo
         * @param messenger {@link FlowContextMessenger}stream流程事件发送器
         * @param locks 流程锁
         */
        public FlowEnv(FlowContextRepo<FlowData> repo, FlowContextMessenger messenger, FlowLocks locks) {
            this.repo = repo;
            this.messenger = messenger;
            this.locks = locks;
        }
    }
}
