/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.activities;

import modelengine.fel.engine.flows.Action;
import modelengine.fitframework.inspection.Validation;

import java.util.function.Consumer;

/**
 * 流程回调对象。
 *
 * @param <O> 表示流程输出数据类型。
 * @author 刘信宏
 * @since 2024-05-28
 */
public class FlowCallBack<O> {
    private final Consumer<O> successCb;
    private final Consumer<Throwable> errorCb;

    /**
     * 使用构造器初始化 {@link FlowCallBack}。
     *
     * @param builder 表示 {@link FlowCallBack} 构造器的 {@link FlowCallBack.Builder}{@code <}{@link O}{@code >}。
     */
    public FlowCallBack(FlowCallBack.Builder<O> builder) {
        Validation.notNull(builder, "Flow callback builder can not be null.");
        this.successCb = builder.successCb.andThen(__ -> builder.finallyCb.exec());
        this.errorCb = builder.errorCb.andThen(__ -> builder.finallyCb.exec());
    }

    /**
     * 创建 {@link FlowCallBack} 的构造器。
     *
     * @param <T> 表示流程输出数据类型。
     * @return 表示 {@link FlowCallBack} 构造器的 {@link FlowCallBack.Builder}{@code <}{@link T}{@code >}。
     */
    public static <T> FlowCallBack.Builder<T> builder() {
        return new FlowCallBack.Builder<>();
    }

    /**
     * 获取空实现的回调对象。
     *
     * @param <T> 表示流程输出数据类型。
     * @return 表示流程回调对象的 {@link FlowCallBack}{@code <}{@link T}{@code >}。
     */
    public static <T> FlowCallBack<T> emptyCallBack() {
        return FlowCallBack.<T>builder().build();
    }

    public Consumer<O> successCb() {
        return this.successCb;
    }

    public Consumer<Throwable> errorCb() {
        return this.errorCb;
    }

    /**
     * {@link FlowCallBack} 的构造器。
     *
     * @param <O> 表示流程输出数据类型。
     */
    public static class Builder<O> {
        private Consumer<O> successCb = EmptyCallBack.doNothingOnSuccess();
        private Consumer<Throwable> errorCb = EmptyCallBack.doNothingOnError();
        private Action finallyCb = EmptyCallBack.doNothingOnFinally();

        /**
         * 设置成功回调。
         *
         * @param successCb 表示流程成功后操作的 {@link Consumer}{@code <}{@link O}{@code >}。
         * @return 表示 {@link FlowCallBack} 构造器的 {@link FlowCallBack.Builder}{@code <}{@link O}{@code >}。
         * @throws IllegalArgumentException 当 {@code successCb} 为 {@code null} 时。
         */
        public Builder<O> doOnSuccess(Consumer<O> successCb) {
            this.successCb = Validation.notNull(successCb, "Success processor can not be null.");
            return this;
        }

        /**
         * 设置异常回调。
         *
         * @param errorCb 表示流程异常后操作的 {@link Consumer}{@code <}{@link Throwable}{@code >}。
         * @return 表示 {@link FlowCallBack} 构造器的 {@link FlowCallBack.Builder}{@code <}{@link O}{@code >}。
         * @throws IllegalArgumentException 当 {@code errorCb} 为 {@code null} 时。
         */
        public Builder<O> doOnError(Consumer<Throwable> errorCb) {
            this.errorCb = Validation.notNull(errorCb, "error handler can not be null.");
            return this;
        }

        /**
         * 设置结束回调，流程成功或异常时都会触发该回调。
         *
         * @param finallyCb 表示流程结束后操作的 {@link Action}。
         * @return 表示 {@link FlowCallBack} 构造器的 {@link FlowCallBack.Builder}{@code <}{@link O}{@code >}。
         * @throws IllegalArgumentException 当 {@code finallyCb} 为 {@code null} 时。
         */
        public Builder<O> doOnFinally(Action finallyCb) {
            this.finallyCb = Validation.notNull(finallyCb, "Finally action can not be null.");
            return this;
        }

        /**
         * 构造 {@link FlowCallBack}。
         *
         * @return 表示流程回调对象的 {@link FlowCallBack}{@code <}{@link O}{@code >}。
         */
        public FlowCallBack<O> build() {
            return new FlowCallBack<>(this);
        }
    }

    private static class EmptyCallBack {
        static <R> Consumer<R> doNothingOnSuccess() {
            return input -> {};
        }

        static Consumer<Throwable> doNothingOnError() {
            return exception -> {};
        }

        static Action doNothingOnFinally() {
            return () -> {};
        }
    }
}
