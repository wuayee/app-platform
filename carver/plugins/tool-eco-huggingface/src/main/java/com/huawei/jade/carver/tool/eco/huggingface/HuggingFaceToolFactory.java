/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.eco.huggingface;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;
import com.huawei.jade.fel.service.pipeline.HuggingFacePipelineService;

/**
 * 表示 {@link ToolFactory} 的 HuggingFace 的实现。
 *
 * @author 季聿阶
 * @since 2024-06-04
 */
@Component
public class HuggingFaceToolFactory implements ToolFactory {
    private final ObjectSerializer serializer;
    private final HuggingFacePipelineService pipelineService;

    HuggingFaceToolFactory(@Fit(alias = "json") ObjectSerializer serializer,
            HuggingFacePipelineService pipelineService) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.pipelineService = notNull(pipelineService, "The hugging face pipeline service cannot be null.");
    }

    @Override
    public String type() {
        return "HuggingFace";
    }

    @Override
    public Tool create(Tool.Info itemInfo, Tool.Metadata metadata) {
        return new HuggingFaceTool(this.serializer, this.pipelineService, itemInfo, metadata);
    }
}
