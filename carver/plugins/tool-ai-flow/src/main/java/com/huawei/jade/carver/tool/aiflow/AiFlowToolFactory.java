/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.aiflow;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.ToolFactory;

/**
 * TODO
 *
 * @author 季聿阶
 * @since 2024-05-15
 */
@Component
public class AiFlowToolFactory implements ToolFactory {
    @Override
    public String type() {
        return "AiFlow";
    }

    @Override
    public Tool create(Tool.Info itemInfo, Tool.Metadata metadata) {
        return null;
    }
}
