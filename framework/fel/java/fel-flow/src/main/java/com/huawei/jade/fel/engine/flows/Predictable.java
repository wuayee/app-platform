/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.flows;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.stream.nodes.Retryable;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 流程对话级别的回调操作。
 *
 * @author 刘信宏
 * @since 2024-04-10
 */
public class Predictable<T> implements ConverseListener<T> {
    private Consumer<T> converseSuccessCb = doNothing();
    private Consumer<Throwable> converseErrorCb = doNothing();
    private final Operators.ErrorHandler<Object> flowErrorCb;
    private final String flowId;
    private final ConverseLatch<T> latch;

    /**
     * 对话回调操作对象的构造方法。
     *
     * @param flow 表示 AI 数据处理流程的 {@link AiProcessFlow}{@code <}{@code ?, }{@link T}{@code >}。
     * @param latch 表示流程调用同步器的 {@link ConverseLatch}{@code <}{@link T}{@code >}。
     * @throws IllegalArgumentException 当 {@code flow} 为 {@code null} 时。
     */
    public Predictable(AiProcessFlow<?, T> flow, ConverseLatch<T> latch) {
        Validation.notNull(flow, "Flow cannot be null.");

        this.flowErrorCb = (exception, retryable, contexts) -> {
            List<Operators.ErrorHandler<Object>> handlers = ObjectUtils.cast(flow.end().getErrorHandlers());
            handlers.forEach(handler -> handler.handle(exception, retryable, contexts));
        };
        this.flowId = flow.getId();
        this.latch = latch;
    }

    /**
     * 对话回调操作对象的构造方法。
     *
     * @param predictable 表示对话回调操作对象的 {@link Predictable}{@code <}{@link T}{@code >}。
     * @param latch 表示流程调用同步器的 {@link ConverseLatch}{@code <}{@link T}{@code >}。
     * @throws IllegalArgumentException 当 {@code predictable} 或 {@code latch} 为 {@code null} 时。
     */
    public Predictable(Predictable<T> predictable, ConverseLatch<T> latch) {
        Validation.notNull(predictable, "Predictable cannot be null.");
        Validation.notNull(latch, "Conversation latch cannot be null.");

        this.converseSuccessCb = predictable.converseSuccessCb.andThen(latch::setData);
        this.converseErrorCb = predictable.converseErrorCb.andThen(latch::setThrowable);
        this.setFinallyCb(latch::countDown);
        this.flowErrorCb = predictable.flowErrorCb;
        this.flowId = predictable.flowId;
        this.latch = latch;
    }

    /**
     * 设置成功回调。
     *
     * @param successCb 表示指定的对话成功时操作的 {@link Consumer}{@code <}{@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code successCb} 为 {@code null} 时。
     */
    public void setSuccessCb(Consumer<T> successCb) {
        this.converseSuccessCb = Validation.notNull(successCb, "Success processor cannot be null.");
    }

    /**
     * 设置异常回调。
     *
     * @param errorCb 表示指定的对话发生异常时操作的 {@link Consumer}{@code <}{@link Throwable}{@code >}。
     * @throws IllegalArgumentException 当 {@code errorCb} 为 {@code null} 时。
     */
    public void setErrorCb(Consumer<Throwable> errorCb) {
        this.converseErrorCb = Validation.notNull(errorCb, "error handler cannot be null.");
    }

    /**
     * 设置结束回调。
     *
     * @param finallyAction 表示待设置的结束回调的 {@link Action}。
     * @throws IllegalArgumentException 当 {@code finallyAction} 为 {@code null} 时。
     */
    public void setFinallyCb(Action finallyAction) {
        Validation.notNull(finallyAction, "Finally action cannot be null.");
        this.converseSuccessCb = this.converseSuccessCb.andThen(d -> finallyAction.exec());
        this.converseErrorCb = this.converseErrorCb.andThen(d -> finallyAction.exec());
    }

    /**
     * 清理会话成功回调和异常回调。
     */
    public void clear() {
        converseSuccessCb = doNothing();
        converseErrorCb = doNothing();
    }

    @Override
    public void onSuccess(String flowId, T data) {
        // 对话结束回调由直接起会话的流程触发
        if (!Objects.equals(flowId, this.flowId)) {
            return;
        }
        this.converseSuccessCb.accept(data);
    }

    @Override
    public void onError(String flowId, Exception exception, Retryable<Object> retryable,
            List<FlowContext<Object>> contexts) {
        // 会话错误处理
        this.converseErrorCb.accept(exception);
        if (Objects.equals(flowId, this.flowId)) {
            return;
        }
        // 父流程错误处理
        this.flowErrorCb.handle(exception, retryable, contexts);
    }

    @Override
    public boolean isCompleted() {
        if (this.latch == null) {
            return true;
        }
        return this.latch.getCountDownLatch().getCount() == 0;
    }

    private static <R> Consumer<R> doNothing() {
        return input -> {};
    }
}
