/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jade.aipp.classify.question.command.ClassifyQuestionCommand;
import modelengine.fit.jade.aipp.classify.question.command.ClassifyQuestionCommandHandler;
import modelengine.fit.jade.aipp.memory.AippChatRound;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 问题分类算子服务。
 *
 * @author 张越
 * @since 2024-11-18
 */
@Component
public class AippClassifyQuestionService implements ClassifyQuestionService {
    private final ClassifyQuestionCommandHandler commandHandler;

    /**
     * 创建 {@link AippClassifyQuestionService} 的实例。
     *
     * @param commandHandler 表示问题分类命令服务的 {@link ClassifyQuestionCommandHandler}。
     */
    public AippClassifyQuestionService(ClassifyQuestionCommandHandler commandHandler) {
        this.commandHandler = notNull(commandHandler, "The command service cannot be null");
    }

    @Override
    @Fitable("aipp")
    public String classifyQuestion(ClassifyQuestionParam classifyQuestionParam, AippMemoryConfig memoryConfig,
            List<AippChatRound> histories) {
        notNull(classifyQuestionParam, "The classify question param cannot be null.");
        ClassifyQuestionCommand command = Converter.INSTANCE.convert(classifyQuestionParam, memoryConfig, histories);
        return this.commandHandler.handle(command);
    }

    /**
     * 表示 FIT 服务参数转换器。
     */
    @Mapper(imports = QuestionType.class)
    public interface Converter {
        /**
         * 转换器单例。
         */
        Converter INSTANCE = Mappers.getMapper(Converter.class);

        /**
         * 组装重写请求命令。
         *
         * @param classifyQuestionParam 表示重写参数的 {@link ClassifyQuestionParam}。
         * @param memoryConfig 表示历史记录配置的 {@link AippMemoryConfig}。
         * @param histories 表示历史记录的 {@link List}{@code <}{@link AippChatRound}{@code >}。
         * @return 表示重写请求命令的 {@link ClassifyQuestionParam}。
         */
        @Mapping(target = "model", expression = "java(classifyQuestionParam.getAccessInfo().getServiceName())")
        @Mapping(target = "modelTag", expression = "java(classifyQuestionParam.getAccessInfo().getTag())")
        @Mapping(target = "temperature", expression = "java(classifyQuestionParam.getTemperature())")
        @Mapping(target = "args", expression = "java(classifyQuestionParam.getArgs())")
        @Mapping(target = "template", expression = "java(classifyQuestionParam.getTemplate())")
        @Mapping(target = "questionTypes", expression = "java(classifyQuestionParam.getQuestionTypeList())")
        ClassifyQuestionCommand convert(ClassifyQuestionParam classifyQuestionParam, AippMemoryConfig memoryConfig,
                List<AippChatRound> histories);
    }
}
