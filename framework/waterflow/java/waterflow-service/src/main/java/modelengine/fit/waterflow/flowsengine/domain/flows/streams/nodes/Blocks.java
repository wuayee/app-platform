/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes;

import lombok.Getter;
import lombok.Setter;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ProcessType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.To;

import java.util.List;
import java.util.Optional;

/**
 * stream操作过程中需要中断的地方设置block，stream在处理到该节点的时候会停住，等待外部驱动继续执行
 * block的核心是一个validator或者filter，在驱动的时候会选择符合条件的数据进入处理
 * 辉子 2019-11-20
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class Blocks {
    /**
     * Block
     *
     * @since 2023-09-15
     */
    public abstract static class Block<I> extends IdGenerator {
        /**
         * 需要中断的目标节点
         */
        @Getter
        @Setter
        private To<I, ?> target;

        /**
         * 外部触发执行目标节点中某一个流程实例进行数据处理
         *
         * @param contexts contexts
         */
        public void process(List<FlowContext<I>> contexts) {
            contexts.forEach(context ->
                    context.getTraceId().forEach(traceId ->
                            this.target.getRepo().getTraceOwnerService().own(traceId, context.getTrans().getId())));
            this.target.accept(ProcessType.PROCESS, contexts);
        }
    }

    /**
     * 用于MAP的block
     * 有能力validate某个item是否满足条件
     * 辉子 20190-11-21
     *
     * @param <I>
     */
    public static class ValidatorBlock<I> extends Block<I> {
        /**
         * 单项数据的validator，block在被触发的时候调用该validator过滤数据
         */
        private final Processors.Validator<I> validator;

        /**
         * 空validator构造器，其实就是默认全部通过验证
         */
        public ValidatorBlock() {
            this(null);
        }

        /**
         * 用户在设置时将validator传入block，但不会传入相应的节点，只会在调用process的时候传入节点
         *
         * @param validator 验证器
         */
        public ValidatorBlock(Processors.Validator<I> validator) {
            this.validator = validator;
        }

        /**
         * 在外部触发block执行的时候将validator传入对应的节点
         *
         * @param contexts contexts
         */
        @Override
        public void process(List<FlowContext<I>> contexts) {
            this.getTarget().setValidator((i, all) -> validator == null || validator.check(i, all));
            super.process(contexts);
        }
    }

    /**
     * FilterBlock
     *
     * @since 2023-09-15
     */
    public static class FilterBlock<I> extends Block<I> {
        private final Processors.Filter<I> filter;

        public FilterBlock() {
            this(null);
        }

        public FilterBlock(Processors.Filter<I> filter) {
            this.filter = filter;
        }

        /**
         * 在外部触发block执行的时候将validator传入对应的节点
         *
         * @param contexts contexts
         */
        @Override
        public void process(List<FlowContext<I>> contexts) {
            Optional.ofNullable(this.filter).ifPresent(f -> this.getTarget().postFilter(f));
            super.process(contexts);
        }
    }
}
