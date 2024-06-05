/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.execution.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.repository.ToolFactoryRepository;
import com.huawei.jade.carver.tool.service.ToolExecuteService;
import com.huawei.jade.carver.tool.service.ToolService;

import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link ToolExecuteService} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
@Component
public class DefaultToolExecuteService implements ToolExecuteService {
    private final ToolService toolService;
    private final ToolFactoryRepository toolFactoryRepository;

    /**
     * 通过工具的仓库和工具工厂的仓库来创建 {@link DefaultToolExecuteService} 的新实例。
     *
     * @param toolService 表示工具的仓库的 {@link ToolService}。
     * @param toolFactoryRepository 表示工具工厂的仓库的 {@link ToolFactoryRepository}。
     */
    public DefaultToolExecuteService(ToolService toolService, ToolFactoryRepository toolFactoryRepository) {
        this.toolService = notNull(toolService, "The tool repository cannot be null.");
        this.toolFactoryRepository = notNull(toolFactoryRepository, "The tool factory repository cannot be null.");
    }

    @Override
    @Fitable(id = "standard")
    public String executeTool(String uniqueName, String jsonArgs) {
        Tool tool = this.getTool(uniqueName);
        return tool.executeWithJson(jsonArgs);
    }

    @Override
    @Fitable(id = "standard")
    public Object executeTool(String uniqueName, Map<String, Object> jsonObjectArgs) {
        Tool tool = this.getTool(uniqueName);
        return tool.executeWithJsonObject(jsonObjectArgs);
    }

    private Tool getTool(String uniqueName) {
        notBlank(uniqueName, "The tool unique name cannot be blank.");
        Tool.Info info = ToolData.convertToInfo(this.toolService.getTool(uniqueName));
        if (info == null) {
            throw new IllegalStateException(StringUtils.format("No tool with specified unique name. [uniqueName={0}]",
                    uniqueName));
        }
        Optional<ToolFactory> factory = this.toolFactoryRepository.query(info.tags());
        if (!factory.isPresent()) {
            throw new IllegalStateException(StringUtils.format("No tool factory to create tool. [tags={0}]",
                    info.tags()));
        }
        Tool.Metadata metadata = Tool.Metadata.fromSchema(info.schema());
        return factory.get().create(info, metadata);
    }
}
