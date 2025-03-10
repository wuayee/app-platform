/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.ports.fit;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jade.aipp.extract.ContentExtractParam;
import modelengine.fit.jade.aipp.extract.ContentExtractService;
import modelengine.fit.jade.aipp.extract.ExtractResult;
import modelengine.fit.jade.aipp.extract.command.ContentExtractCommand;
import modelengine.fit.jade.aipp.extract.command.ExtractCommandHandler;
import modelengine.fit.jade.aipp.memory.AippChatRound;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.jade.common.exception.ModelEngineException;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 表示 {@link ContentExtractService} 的 aipp 实现。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-24
 */
@Component
public class AippInfoExtractService implements ContentExtractService {
    private final ExtractCommandHandler commandHandler;

    /**
     * 创建 {@link AippInfoExtractService} 的实例。
     *
     * @param commandHandler 表示消息提取命令服务的 {@link ExtractCommandHandler}。
     */
    public AippInfoExtractService(ExtractCommandHandler commandHandler) {
        this.commandHandler = notNull(commandHandler, "The command service cannot be null");
    }

    @Override
    @Fitable("aipp")
    public ExtractResult extract(ContentExtractParam extractParam, AippMemoryConfig memoryConfig,
            List<AippChatRound> histories) {
        notNull(extractParam, "The extract param cannot be null.");
        ContentExtractCommand command = Converter.INSTANCE.convertParamsToCommand(extractParam,
                extractParam.getAccessInfo(),
                memoryConfig,
                histories);
        try {
            Object extractedParams = this.commandHandler.handle(command);
            return new ExtractResult(true, extractedParams);
        } catch (ModelEngineException exception) {
            return new ExtractResult(false, exception.getMessage());
        }
    }

    /**
     * 表示 FIT 服务参数转换器。
     */
    @Mapper
    public interface Converter {
        /**
         * 获取 CommandConvertor 的实现。
         */
        Converter INSTANCE = Mappers.getMapper(Converter.class);

        /**
         * 组装信息提取命令。
         *
         * @param extractParam 表示提取参数的 {@link ContentExtractParam}。
         * @param accessInfo 表示模型服务信息的 {@link ModelAccessInfo}。
         * @param memoryConfig 表示历史记录配置的 {@link AippMemoryConfig}。
         * @param histories 表示历史记录的 {@link List}{@code <}{@link AippChatRound}{@code >}。
         * @return 表示信息提取请求命令的 {@link ContentExtractCommand}。
         */
        @Mapping(source = "extractParam.text", target = "text")
        @Mapping(source = "extractParam.desc", target = "desc")
        @Mapping(source = "extractParam.outputSchema", target = "outputSchema")
        @Mapping(source = "extractParam.temperature", target = "temperature")
        @Mapping(source = "accessInfo.serviceName", target = "model")
        @Mapping(source = "accessInfo.tag", target = "modelTag")
        @Mapping(source = "memoryConfig", target = "memoryConfig")
        @Mapping(source = "histories", target = "histories")
        ContentExtractCommand convertParamsToCommand(ContentExtractParam extractParam, ModelAccessInfo accessInfo,
                AippMemoryConfig memoryConfig, List<AippChatRound> histories);
    }
}