/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.pipeline.Pipeline;
import com.huawei.jade.fel.service.pipeline.HuggingFacePipelineService;

import java.util.Map;

/**
 * 表示 huggingface pipeline 的特化实现。
 *
 * @param <I> 表示流水线输入参数类型的 {@link I}。
 * @param <O> 表示流水线输出参数类型的 {@link O}。
 * @author 易文渊
 * @since 2024-06-04
 */
public abstract class ExplicitPipeline<I, O> implements Pipeline<I, O> {
    private final GeneralPipeline generalPipeline;

    private final PipelineTask task;

    /**
     * 创建特化流水线的实例。
     *
     * @param task 表示任务类型的 {@link PipelineTask}。
     * @param model 表示模型名的 {@link String}。
     * @param service 表示提供 pipeline 服务的 {@link HuggingFacePipelineService}。
     */
    protected ExplicitPipeline(PipelineTask task, String model, HuggingFacePipelineService service) {
        Validation.notBlank(model, "The model cannot be blank.");
        Validation.notNull(service, "The HuggingFacePipelineService cannot be null.");
        this.generalPipeline = new GeneralPipeline(task, model, service);
        this.task = Validation.notNull(task, "The PipelineTask cannot be null.");
    }

    @Override
    public O apply(I input) {
        Object obj = ObjectUtils.toJavaObject(input);
        Validation.isInstanceOf(obj, Map.class, "Pipeline input needs to be able to be converted to a Map");
        Map<String, Object> args = ObjectUtils.cast(obj);
        return ObjectUtils.toCustomObject(generalPipeline.apply(args), task.getOutputType());
    }
}