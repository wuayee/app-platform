/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.execution.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.carver.tool.Tool;
import modelengine.jade.carver.tool.ToolFactory;
import modelengine.jade.carver.tool.model.transfer.DefinitionData;
import modelengine.jade.carver.tool.model.transfer.ToolData;
import modelengine.jade.carver.tool.repository.ToolFactoryRepository;
import modelengine.jade.carver.tool.service.DefinitionService;
import modelengine.jade.carver.tool.service.ToolExecuteService;
import modelengine.jade.carver.tool.service.ToolService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 表示 {@link ToolExecuteService} 的默认实现。
 *
 * @author 季聿阶
 * @since 2024-04-08
 */
@Component
public class DefaultToolExecuteService implements ToolExecuteService {
    private final DefinitionService definitionService;
    private final ToolService toolService;
    private final ToolFactoryRepository toolFactoryRepository;
    private final ObjectSerializer serializer;

    /**
     * 通过工具的仓库和工具工厂的仓库来创建 {@link DefaultToolExecuteService} 的新实例。
     *
     * @param definitionService 表示定义仓库服务的 {@link DefinitionService}。
     * @param toolService 表示工具的服务的 {@link ToolService}。
     * @param toolFactoryRepository 表示工具工厂的仓库的 {@link ToolFactoryRepository}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     */
    public DefaultToolExecuteService(DefinitionService definitionService, ToolService toolService,
            ToolFactoryRepository toolFactoryRepository, @Fit(alias = "json") ObjectSerializer serializer) {
        this.definitionService = notNull(definitionService, "The definition service cannot be null.");
        this.toolService = notNull(toolService, "The tool service cannot be null.");
        this.toolFactoryRepository = notNull(toolFactoryRepository, "The tool factory repository cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Override
    @Fitable(id = "standard")
    public String execute(String uniqueName, String jsonArgs) {
        Tool tool = this.getTool(uniqueName);
        Object output = tool.executeWithJson(jsonArgs);
        return this.convertOutput(tool.info().returnConverter(), output);
    }

    @Override
    @Fitable(id = "standard")
    public String execute(String uniqueName, Map<String, Object> jsonObject) {
        Tool tool = this.getTool(uniqueName);
        Object output = tool.executeWithJsonObject(jsonObject);
        return this.convertOutput(tool.info().returnConverter(), output);
    }

    private Tool getTool(String uniqueName) {
        notBlank(uniqueName, "The tool unique name cannot be blank.");
        ToolData toolData = this.toolService.getTool(uniqueName);
        Tool.ToolInfo info = notNull(ToolData.convertToInfo(toolData),
                StringUtils.format("No tool with specified unique name. [uniqueName={0}]", uniqueName));
        Set<String> runnables = info.runnables().keySet();
        Optional<ToolFactory> factory = this.toolFactoryRepository.match(runnables);
        if (!factory.isPresent()) {
            throw new IllegalStateException(StringUtils.format("No tool factory to create tool. [tags={0}]",
                    runnables));
        }

        DefinitionData definitionData =
                this.definitionService.get(toolData.getDefGroupName(), toolData.getDefName());
        Tool.Metadata metadata = Tool.Metadata.fromSchema(uniqueName, definitionData.getSchema());
        return factory.get().create(info, metadata);
    }

    private String convertOutput(String convertor, Object output) {
        Tool convertorTool = null;
        if (StringUtils.isBlank(convertor) || (convertorTool = this.getTool(convertor)) == null) {
            return serializer.serialize(output);
        }
        return convertorTool.execute(output) == null ? StringUtils.EMPTY : convertorTool.execute(output).toString();
    }
}
