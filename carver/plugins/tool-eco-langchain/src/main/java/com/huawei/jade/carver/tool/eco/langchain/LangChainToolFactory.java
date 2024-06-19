/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.eco.langchain;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;
import com.huawei.jade.fel.service.langchain.LangChainRunnableService;

/**
 * 表示 {@link ToolFactory} 的 LangChain 的实现。
 *
 * @author 刘信宏
 * @since 2024-06-19
 */
@Component
public class LangChainToolFactory implements ToolFactory {
    private final ObjectSerializer serializer;
    private final LangChainRunnableService runnableService;

    public LangChainToolFactory(@Fit(alias = "json") ObjectSerializer serializer,
            LangChainRunnableService runnableService) {
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.runnableService = notNull(runnableService, "The LangChain runnable service cannot be null.");
    }

    @Nonnull
    @Override
    public String type() {
        return "LangChain";
    }

    @Override
    public Tool create(Tool.Info itemInfo, Tool.Metadata metadata) {
        return new LangChainTool(this.runnableService, this.serializer, itemInfo, metadata);
    }
}
