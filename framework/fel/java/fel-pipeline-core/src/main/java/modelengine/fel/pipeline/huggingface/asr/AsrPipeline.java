/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.pipeline.huggingface.asr;

import modelengine.fel.pipeline.huggingface.ExplicitPipeline;
import modelengine.fel.pipeline.huggingface.PipelineTask;
import modelengine.fel.service.pipeline.HuggingFacePipelineService;

/**
 * 表示 {@link PipelineTask#AUTOMATIC_SPEECH_RECOGNITION} 任务的流水线。
 *
 * @author 易文渊
 * @since 2024-06-04
 */
public class AsrPipeline extends ExplicitPipeline<AsrInput, AsrOutput> {
    /**
     * 创建语音识别流水线的实例。
     *
     * @param model 表示模型名的 {@link String}。
     * @param service 表示提供 pipeline 服务的 {@link HuggingFacePipelineService}。
     */
    public AsrPipeline(String model, HuggingFacePipelineService service) {
        super(PipelineTask.AUTOMATIC_SPEECH_RECOGNITION, model, service);
    }
}