/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.execution.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
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
 * @author 季聿阶
 * @since 2024-04-08
 */
@Component
public class DefaultToolExecuteService implements ToolExecuteService {
    private static final Logger log = Logger.get(DefaultToolExecuteService.class);

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
        log.info("Store-find-bug-0 exec tool. [args ={}]", jsonObjectArgs);
        if (tool == null) {
            log.error("Store-find-bug-5 tool is null. [unique name ={}]", uniqueName);
            throw new IllegalStateException(StringUtils.format("Tool is null. [uniqueName={0}]", uniqueName));
        }
        if (tool.info() != null) {
            log.info("Store-find-bug-6 exec tool. [unique name ={}, tags ={}]", uniqueName, tool.info().tags());
        }
        return tool.executeWithJsonObject(jsonObjectArgs);
    }

    private Tool getTool(String uniqueName) {
        notBlank(uniqueName, "The tool unique name cannot be blank.");
        Tool.Info info = ToolData.convertToInfo(this.toolService.getTool(uniqueName));
        if (info == null) {
            log.error("Store-find-bug-1 get tool info error. [unique name ={}]", uniqueName);
            throw new IllegalStateException(StringUtils.format("No tool with specified unique name. [uniqueName={0}]",
                    uniqueName));
        }
        log.info("Store-find-bug-2 get factory. [unique name ={}, tags ={}]", uniqueName, info.tags());
        Optional<ToolFactory> factory = this.toolFactoryRepository.query(info.tags());
        if (!factory.isPresent()) {
            log.error("Store-find-bug-3 get factory error. [unique name ={}, tags ={}]", uniqueName, info.tags());
            throw new IllegalStateException(StringUtils.format("No tool factory to create tool. [tags={0}]",
                    info.tags()));
        }
        Tool.Metadata metadata = Tool.Metadata.fromSchema(info.schema());
        log.info("Store-find-bug-4 create tool. [unique name ={}, tags ={}]", uniqueName, info.tags());
        return factory.get().create(info, metadata);
    }
}
