/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.ports.fit;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jade.aipp.memory.AippChatRound;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;
import modelengine.fit.jade.aipp.rewrite.RewriteQueryParam;
import modelengine.fit.jade.aipp.rewrite.RewriteService;
import modelengine.fit.jade.aipp.rewrite.command.RewriteCommandHandler;
import modelengine.fit.jade.aipp.rewrite.command.RewriteQueryCommand;
import modelengine.fit.jade.aipp.rewrite.domain.entity.RewriteStrategy;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 表示 {@link RewriteService} 的 aipp 实现。
 *
 * @author 易文渊
 * @since 2024-09-28
 */
@Component
public class AippRewriteService implements RewriteService {
    private final RewriteCommandHandler commandHandler;

    /**
     * 创建 {@link AippRewriteService} 的实例。
     *
     * @param commandHandler 表示重写命令服务的 {@link RewriteCommandHandler}。
     */
    public AippRewriteService(RewriteCommandHandler commandHandler) {
        this.commandHandler = notNull(commandHandler, "The command service cannot be null");
    }

    @Override
    @Fitable("aipp")
    public List<String> rewriteQuery(RewriteQueryParam rewriteParam, AippMemoryConfig memoryConfig,
            List<AippChatRound> histories) {
        notNull(rewriteParam, "The rewrite param cannot be null.");
        RewriteQueryCommand command = Converter.INSTANCE.convert(rewriteParam, memoryConfig, histories);
        return this.commandHandler.handle(command);
    }

    /**
     * 表示 FIT 服务参数转换器。
     */
    @Mapper(imports = RewriteStrategy.class)
    public interface Converter {
        /**
         * 转换器单例。
         */
        Converter INSTANCE = Mappers.getMapper(Converter.class);

        /**
         * 组装重写请求命令。
         *
         * @param rewriteParam 表示重写参数的 {@link RewriteQueryParam}。
         * @param memoryConfig 表示历史记录配置的 {@link AippMemoryConfig}。
         * @param histories 表示历史记录的 {@link List}{@code <}{@link AippChatRound}{@code >}。
         * @return 表示重写请求命令的 {@link RewriteQueryCommand}。
         */
        @Mapping(target = "strategy", expression = "java(RewriteStrategy.from(rewriteParam.getStrategy()))")
        @Mapping(target = "model", expression = "java(rewriteParam.getAccessInfo().getServiceName())")
        @Mapping(target = "modelTag", expression = "java(rewriteParam.getAccessInfo().getTag())")
        RewriteQueryCommand convert(RewriteQueryParam rewriteParam, AippMemoryConfig memoryConfig,
                List<AippChatRound> histories);
    }
}