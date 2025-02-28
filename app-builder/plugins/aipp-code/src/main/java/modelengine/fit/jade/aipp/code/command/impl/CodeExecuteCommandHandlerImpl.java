/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.command.impl;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jade.aipp.code.command.CodeExecuteCommand;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommandHandler;
import modelengine.fit.jade.aipp.code.domain.entity.CodeExecutor;
import modelengine.fit.jade.aipp.code.domain.factory.CodeExecutorFactory;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;

/**
 * 表示 {@link CodeExecuteCommandHandler} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-10-14
 */
@Component
@Validated
public class CodeExecuteCommandHandlerImpl implements CodeExecuteCommandHandler {
    private final CodeExecutorFactory executorFactory;

    public CodeExecuteCommandHandlerImpl(CodeExecutorFactory executorFactory) {
        this.executorFactory = notNull(executorFactory, "The executor factory cannot be null.");
    }

    @Override
    public Object handle(CodeExecuteCommand command) {
        CodeExecutor executor = this.executorFactory.create(command.getLanguage());
        return executor.run(command.getArgs(), command.getCode());
    }
}