/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.eco.huggingface;

import static com.huawei.fitframework.inspection.Validation.isInstanceOf;
import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.eco.AbstractTaskTool;
import com.huawei.jade.fel.pipeline.service.HuggingFacePipelineService;

import java.util.Map;

/**
 * 表示 {@link com.huawei.jade.carver.tool.Tool} 的 <a href="https://huggingface.co/">HuggingFace</a> 的实现。
 *
 * @author 季聿阶
 * @since 2024-06-04
 */
public class HuggingFaceTool extends AbstractTaskTool {
    private final HuggingFacePipelineService pipelineService;

    /**
     * 通过 Json 序列化器、工具的基本信息和工具元数据来初始化 {@link HuggingFaceTool} 的新实例。
     *
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param pipelineService 表示 HuggingFace 提供的流水线服务的 {@link HuggingFacePipelineService}。
     * @param itemInfo 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    protected HuggingFaceTool(ObjectSerializer serializer, HuggingFacePipelineService pipelineService, Info itemInfo,
            Metadata metadata) {
        super(serializer, itemInfo, metadata);
        this.pipelineService = notNull(pipelineService, "The hugging face pipeline service cannot be null.");
    }

    @Override
    public Object taskCall(String taskId, Object... args) {
        notNull(args, "The call args cannot be null.");
        isTrue(args.length == 2, "The call args must have 2 args.");
        String model = isInstanceOf(args[0], String.class, "The first arg must be String.class.");
        Map<String, Object> actual = cast(isInstanceOf(args[1], Map.class, "The second arg must be Map.class."));
        return this.pipelineService.call(taskId, model, actual);
    }
}
