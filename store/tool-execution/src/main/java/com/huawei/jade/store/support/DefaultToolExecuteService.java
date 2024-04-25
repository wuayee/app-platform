/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.repository.ToolRepository;
import com.huawei.jade.store.service.ToolExecuteService;

/**
 * 表示 {@link ToolExecuteService} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
@Component
public class DefaultToolExecuteService implements ToolExecuteService {
    private final ToolRepository repository;

    public DefaultToolExecuteService(ToolRepository repository) {
        this.repository = repository;
    }

    @Override
    @Fitable(id = "standard")
    public String executeTool(String group, String toolName, String jsonArgs) {
        notBlank(toolName, "The tool name cannot be blank.");
        Tool tool = cast(this.repository.getTool(group, toolName)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("No tool. [name={0}]", toolName))));
        return tool.callByJson(jsonArgs);
    }
}
